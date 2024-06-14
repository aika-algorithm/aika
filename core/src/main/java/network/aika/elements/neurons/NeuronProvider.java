/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.aika.elements.neurons;

import network.aika.Model;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.SynapseTypeDefinition;
import network.aika.elements.typedef.TypeDefinition;
import network.aika.enums.Scope;
import network.aika.enums.Trigger;
import network.aika.exceptions.NeuronSerializationException;
import network.aika.utils.ReadWriteLock;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static network.aika.elements.neurons.RefType.*;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 * The {@code NeuronProvider} class is a proxy implementation for the real neuron implementation in the class {@code Neuron}.
 * Aika uses the provider pattern to store and reload rarely used neurons or logic nodes.
 *
 * @author Lukas Molzberger
 */
public class NeuronProvider implements Comparable<NeuronProvider> {

    public static final NeuronProvider MIN_NEURON = new NeuronProvider(Long.MIN_VALUE);
    public static final NeuronProvider MAX_NEURON = new NeuronProvider(Long.MAX_VALUE);

    private Model model;
    private final Long id;

    private volatile Neuron neuron;

    HashMap<Integer, Synapse> inputSynapses = new HashMap<>();
    HashMap<Long, Synapse> outputSynapses = new HashMap<>();

    private Set<NeuronProvider> propagableRefs = new HashSet<>();

    protected final ReadWriteLock inputLock = new ReadWriteLock();
    protected final ReadWriteLock outputLock = new ReadWriteLock();

    private int refCount;
    private int[] refCountByType = new int[RefType.values().length];

    private boolean persistent;

    private volatile long lastUsed;
    private boolean isRegistered;

    private NeuronProvider(Long id) {
        this.id = id;
    }

    public NeuronProvider(Model model, Long id, RefType rt) {
        this(id);
        assert model != null;
        this.model = model;

        if(rt != null)
            increaseRefCount(rt);
    }

    public NeuronProvider(Model model, Neuron n, RefType rt) {
        this(model, model.createNeuronId(), rt);
        assert model != null && n != null;

        neuron = n;
    }

    public synchronized <N extends Neuron> N getNeuron() {
        assert isRegistered;

        if (neuron == null)
            reactivate();

        return (N) neuron;
    }

    public void setNeuron(Neuron n) {
        this.neuron = n;
    }

    public String getLabel() {
        return getNeuron().getLabel();
    }

    public Long getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }

    public void increaseRefCount(RefType rt) {
        synchronized (refCountByType) {
            refCount++;
            refCountByType[rt.ordinal()]++;

            if (!isRegistered && refCount > 0) {
                model.register(this);
                isRegistered = true;
            }
        }
    }

    public void decreaseRefCount(RefType rt) {
        synchronized (refCountByType) {
            refCount--;
            refCountByType[rt.ordinal()]--;

            assert refCount >= 0;
            assert refCountByType[rt.ordinal()] >= 0;

            if (isRegistered && refCount == 0) {
                model.unregister(this);
                isRegistered = false;
            }
        }
    }

    public int getRefCount() {
        synchronized (refCountByType) {
            return refCount;
        }
    }

    public boolean isReferenced() {
        synchronized (refCountByType) {
            return refCount > 0;
        }
    }

    public boolean isSuspended() {
        return neuron == null;
    }

    public Neuron getIfNotSuspended() {
        return neuron;
    }

    public Synapse getSynapseBySynId(Integer synId) {
        if(synId == null)
            return null;

        return inputSynapses.get(synId);
    }

    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Prevent the neuron from being suspended.
     * @param persistent
     */
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;

        if(persistent)
            increaseRefCount(PERSISTENCE);
        else
            decreaseRefCount(PERSISTENCE);
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void updateLastUsed(long thoughtId) {
        lastUsed = Math.max(lastUsed, thoughtId);
    }

    public synchronized boolean suspend(boolean saveOnSuspend) {
        if(neuron == null)
            return false;

        if(neuron.inUse())
            return false;

        assert model.getSuspensionCallback() != null;

        if(saveOnSuspend)
            save();

        neuron.suspend();
        neuron = null;

        return true;
    }

    public void save() {
        if(neuron == null || !neuron.isModified())
            return;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            neuron.write(dos);

            model.getSuspensionCallback().store(
                    id,
                    neuron.getLabel(),
                    neuron.getCustomData(),
                    baos.toByteArray()
            );
        } catch (IOException e) {
            throw new NeuronSerializationException(id, e);
        }
        neuron.resetModified();
    }

    private void reactivate() {
        assert model.getSuspensionCallback() != null;

        Neuron n;
        try (DataInputStream dis = new DataInputStream(
                new ByteArrayInputStream(
                        model.getSuspensionCallback().retrieve(id)
                )
        )) {
            n = Neuron.read(dis, this);
        } catch (Exception e) {
            throw new NeuronSerializationException(id, e);
        }
        n.setProvider(this);
        n.reactivate(model);
        neuron = n;
    }

    public void addInputSynapse(Synapse s) {
        inputLock.acquireWriteLock();
        inputSynapses.put(s.getSynapseId(), s);
        inputLock.releaseWriteLock();
    }

    public void removeInputSynapse(Synapse s) {
        inputLock.acquireWriteLock();
        inputSynapses.remove(s.getSynapseId());
        inputLock.releaseWriteLock();
    }

    public void addOutputSynapse(Synapse s) {
        outputLock.acquireWriteLock();
        outputSynapses.put(s.getPOutput().getId(), s);
        outputLock.releaseWriteLock();
    }

    public void removeOutputSynapse(Synapse s) {
        outputLock.acquireWriteLock();
        outputSynapses.remove(s.getPOutput().getId());
        outputLock.releaseWriteLock();
    }

    public Stream<Synapse> getInputSynapsesAsStream() {
        return getInputSynapses().stream();
    }

    public Collection<Synapse> getInputSynapses() {
        return inputSynapses.values();
    }

    public Stream<? extends Synapse> getOutputSynapsesAsStream() {
        return getOutputSynapses().stream();
    }

    public List<? extends Synapse> getOutputSynapsesByTriggerAndBSType(Trigger t, Scope bsType) {
        outputLock.acquireReadLock();
        List<? extends Synapse> os = getOutputSynapsesAsStream()
                .filter(s ->
                        s.getTrigger().match(t) &&
                                s.getRequired().getFrom() == bsType
                ).toList();
        outputLock.releaseReadLock();
        return os;
    }

    public Collection<? extends Synapse> getOutputSynapses() {
        return outputSynapses.values();
    }

    public Synapse getOutputSynapse(NeuronProvider n) {
        outputLock.acquireReadLock();
        Synapse syn = getOutputSynapsesAsStream()
                .filter(s -> s.getPOutput().getId() == n.getId())
                .findFirst()
                .orElse(null);
        outputLock.releaseReadLock();
        return syn;
    }

    public List<Synapse> getInputSynapsesStoredAtOutputSide() {
        inputLock.acquireReadLock();
        List<Synapse> syns = getInputSynapsesAsStream()
                .filter(s -> s.getStoredAt() == OUTPUT)
                .toList();
        inputLock.releaseReadLock();

        return syns;
    }

    public List<? extends Synapse> getOutputSynapsesStoredAtInputSide() {
        outputLock.acquireReadLock();
        List<? extends Synapse> syns = getOutputSynapsesAsStream()
                .filter(s -> s.getStoredAt() == INPUT)
                .toList();
        outputLock.releaseReadLock();
        return syns;
    }

    public Synapse getInputSynapse(NeuronProvider n) {
        inputLock.acquireReadLock();
        Synapse syn = selectInputSynapse(s ->
                s.getPInput().getId() == n.getId()
        );

        inputLock.releaseReadLock();
        return syn;
    }

    public Synapse getInputSynapseByType(TypeDefinition<SynapseTypeDefinition, Synapse> synapseType) {
        inputLock.acquireReadLock();
        Synapse is = getInputSynapsesByType(synapseType)
                .findAny()
                .orElse(null);
        inputLock.releaseReadLock();
        return is;
    }

    public Stream<Synapse> getInputSynapsesByType(TypeDefinition<SynapseTypeDefinition, Synapse> synapseType) {
        return getInputSynapsesAsStream()
                .filter(synapseType::isInstance);
    }

    public Synapse getOutputSynapseByType(TypeDefinition<SynapseTypeDefinition, Synapse> synapseType) {
        outputLock.acquireReadLock();
        Synapse os = getOutputSynapsesAsStream()
                .filter(synapseType::isInstance)
                .findAny()
                .orElse(null);
        outputLock.releaseReadLock();
        return os;
    }

    public Synapse selectInputSynapse(Predicate<? super Synapse> predicate) {
        inputLock.acquireReadLock();
        Synapse s = getInputSynapsesAsStream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
        inputLock.releaseReadLock();
        return s;
    }

    public void addPropagableRef(NeuronProvider np) {
        inputLock.acquireWriteLock();
        if(propagableRefs.add(np))
            np.increaseRefCount(PROPAGABLE_OUT);

        inputLock.releaseWriteLock();
    }

    public void removePropagableRef(NeuronProvider np) {
        inputLock.acquireWriteLock();
        if(propagableRefs.remove(np))
            np.decreaseRefCount(PROPAGABLE_OUT);

        inputLock.releaseWriteLock();
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    @Override
    public boolean equals(Object o) {
        return id.intValue() == ((NeuronProvider) o).id.intValue();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public int compareTo(NeuronProvider np) {
        return Long.compare(id, np.id);
    }

    public String toString() {
        if(this == MIN_NEURON) return "MIN_NEURON";
        if(this == MAX_NEURON) return "MAX_NEURON";

        return "p(" + (neuron != null ? neuron : id + ":" + "SUSPENDED") + ")";
    }

    public String toKeyString() {
        if(this == MIN_NEURON) return "MIN_NEURON";
        if(this == MAX_NEURON) return "MAX_NEURON";

        return "p(" + (neuron != null ? neuron.toKeyString() : id + ":" + "SUSPENDED") + ")";
    }
}