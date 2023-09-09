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
import network.aika.exceptions.NeuronSerializationException;
import network.aika.suspension.SuspensionMode;
import network.aika.utils.ReadWriteLock;

import java.io.*;
import java.util.HashMap;
import java.util.stream.Stream;

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

    HashMap<Integer, Synapse> inputSynapsesBySynId = new HashMap<>();
    HashMap<Long, Synapse> inputSynapses = new HashMap<>();
    HashMap<Long, Synapse> outputSynapses = new HashMap<>();

    protected final ReadWriteLock lock = new ReadWriteLock();

    private boolean persistent;
    private volatile long lastUsed;
    private boolean isRegistered;

    public NeuronProvider(long id) {
        this.id = id;
    }

    public NeuronProvider(Model model, long id) {
        this(id);
        assert model != null;
        this.model = model;
        model.register(this);
    }

    public NeuronProvider(Model model, Neuron n) {
        this(model, model.createNeuronId());
        assert model != null && n != null;

        neuron = n;
        checkRegister();
    }

    public <N extends Neuron> N getNeuron() {
        if (neuron == null)
            reactivate();

        return (N) neuron;
    }

    public void setNeuron(Neuron<?, ?> n) {
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

    public boolean isSuspended() {
        return neuron == null;
    }

    public Neuron getIfNotSuspended() {
        return neuron;
    }

    public Synapse getSynapseBySynId(int synId) {
        return inputSynapsesBySynId.get(synId);
    }

    public Stream<Synapse> getInputSynapses() {
        return inputSynapses.values().stream();
    }

    public Stream<Synapse> getOutputSynapses() {
        return outputSynapses.values().stream();
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void updateLastUsed(long thoughtId) {
        lastUsed = Math.max(lastUsed, thoughtId);
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;

        if(persistent)
            checkRegister();
        else
            checkUnregister();
    }

    public synchronized void suspend(SuspensionMode sm) {
        if(neuron == null) return;
        assert model.getSuspensionCallback() != null;

        if(persistent || !model.canBeSuspended(lastUsed)) {
            if(sm == SuspensionMode.SAVE)
                save();
            return;
        }

        if(sm == SuspensionMode.SAVE)
            save();

        neuron.suspend();
        neuron = null;

        checkUnregister();
    }

    public void save() {
        if(!neuron.isModified())
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
            n = Neuron.read(dis, model);
        } catch (Exception e) {
            throw new NeuronSerializationException(id, e);
        }
        n.setProvider(this);
        n.reactivate(model);
        neuron = n;
        checkRegister();
    }

    public void addInputSynapse(Synapse s) {
        lock.acquireWriteLock();
        inputSynapsesBySynId.put(s.getSynapseId(), s);
        inputSynapses.put(s.getPInput().getId(), s);
        if(neuron != null)
            neuron.addInputSynapse(s);

        checkRegister();
        lock.releaseWriteLock();
    }

    public void removeInputSynapse(Synapse s) {
        lock.acquireWriteLock();
        inputSynapsesBySynId.remove(s.getSynapseId());
        inputSynapses.remove(s.getPInput().getId());
        if(neuron != null)
            neuron.removeInputSynapse(s);

        checkUnregister();
        lock.releaseWriteLock();
    }

    public void addOutputSynapse(Synapse s) {
        lock.acquireWriteLock();
        outputSynapses.put(s.getPOutput().getId(), s);
        if(neuron != null)
            neuron.addOutputSynapse(s);

        checkRegister();
        lock.releaseWriteLock();
    }

    public void removeOutputSynapse(Synapse s) {
        lock.acquireWriteLock();
        outputSynapses.remove(s.getPOutput().getId());
        if(neuron != null)
            neuron.removeOutputSynapse(s);

        checkUnregister();
        lock.releaseWriteLock();
    }

    private void checkRegister() {
        if(!isRegistered && isReferenced()) {
            model.register(this);
            isRegistered = true;
        }
    }

    private void checkUnregister() {
        if(isRegistered && !isReferenced()) {
            model.unregister(this);
            isRegistered = false;
        }
    }

    private boolean isReferenced() {
        return persistent ||
                neuron != null ||
                !inputSynapses.isEmpty() ||
                !outputSynapses.isEmpty();
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