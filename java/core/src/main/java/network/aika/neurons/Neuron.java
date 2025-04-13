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
package network.aika.neurons;

import network.aika.bindingsignal.BindingSignal;
import network.aika.bindingsignal.BSType;
import network.aika.misc.utils.ReadWriteLock;
import network.aika.type.Obj;
import network.aika.type.Type;
import network.aika.Model;
import network.aika.Document;
import network.aika.ModelProvider;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.typedefs.NeuronDefinition;
import network.aika.typedefs.SynapseDefinition;
import network.aika.activations.Activation;
import network.aika.Element;
import network.aika.type.ObjImpl;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import network.aika.queue.Timestamp;
import network.aika.queue.steps.Save;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static network.aika.misc.direction.Direction.INPUT;
import static network.aika.misc.direction.Direction.OUTPUT;
import static network.aika.neurons.RefType.*;
import static network.aika.queue.Timestamp.MAX;
import static network.aika.queue.Timestamp.MIN;
import static network.aika.typedefs.NeuronDefinition.ACTIVATION;
import static network.aika.typedefs.NeuronDefinition.SELF;

/**
 *
 * @author Lukas Molzberger
 */
public class Neuron extends ObjImpl implements Element, ModelProvider, QueueProvider, Comparable<Neuron> {

    protected static final Logger LOG = LoggerFactory.getLogger(Neuron.class);

    public static final Neuron MIN_NEURON = new Neuron(Long.MIN_VALUE);
    public static final Neuron MAX_NEURON = new Neuron(Long.MAX_VALUE);

    private Model model;
    private final Long id;

    private int synapseIdCounter = 0;
    protected final ReadWriteLock inputLock = new ReadWriteLock();
    private final Map<Integer, Synapse> inputSynapses = new HashMap<>();

    protected final ReadWriteLock outputLock = new ReadWriteLock();
    private final Map<Long, Synapse> outputSynapses = new HashMap<>();
    private final Map<Long, NeuronReference> propagable = new HashMap<>();

    private int refCount;
    private final int[] refCountByType = new int[RefType.values().length];

    private volatile long lastUsed;
    private volatile boolean modified;


    private Neuron(long id) {
        super(null);
        this.id = id;
    }

    public Neuron(NeuronDefinition type, Model model, Long id) {
        super(type);
        assert model != null;
        this.id = id;
        this.model = model;
    }

    public Neuron(NeuronDefinition type, Model model) {
        this(type, model, model.createNeuronId());
    }

    @Override
    public Stream<Obj> followManyRelation(Relation rel) {
        if(rel == NeuronDefinition.INPUT)
            return getInputSynapsesAsStream().map(o -> o);
        else if(rel == NeuronDefinition.OUTPUT)
            return getOutputSynapsesAsStream().map(o -> o);
        else if(rel == NeuronDefinition.ACTIVATION)
            return null;
        else
            throw new RuntimeException("Invalid Relation");
    }

    @Override
    public Obj followSingleRelation(Relation rel) {
        if(rel == SELF)
            return this;
        else
            throw new RuntimeException("Invalid Relation");
    }

    public Long getId() {
        return id;
    }

    public void updatePropagable(Neuron n, boolean isPropagable) {
        if (isPropagable)
            addPropagable(n);
        else
            removePropagable(n);
    }

    private void addPropagable(Neuron n) {
        outputLock.acquireWriteLock();

        assert !propagable.containsKey(n.getId());
        propagable.put(n.getId(),
                new NeuronReference(n, PROPAGABLE)
        );

        outputLock.releaseWriteLock();
    }

    private void removePropagable(Neuron n) {
        outputLock.acquireWriteLock();

        NeuronReference nRef = propagable.remove(n.getId());
        nRef.suspendNeuron();

        outputLock.releaseWriteLock();
    }

    public void wakeupPropagable() {
        outputLock.acquireReadLock();
        List<NeuronReference> p = propagable.values()
                .stream()
                .toList();
        outputLock.releaseReadLock();

        p.forEach(nr ->
                nr.getNeuron(model)
        );
    }

    public Collection<NeuronReference> getPropagable() {
        return propagable.values();
    }

    public int getNewSynapseId() {
        return synapseIdCounter++;
    }


    public final Activation createActivation(Activation parent, Document doc, Map<BSType, BindingSignal> bindingSignals) {
        return ((NeuronDefinition)this.getType()).getActivation()
                .instantiate(doc.createActivationId(), parent, this, doc, bindingSignals);
    }

    public void delete() {
        LOG.info("Delete Neuron: " + this);
        inputLock.acquireReadLock();
        getInputSynapses()
                .forEach(s -> s.unlinkInput(model));
        inputLock.releaseReadLock();

        outputLock.acquireReadLock();
        getOutputSynapses()
                .forEach(s -> s.unlinkOutput(model));
        outputLock.releaseReadLock();
    }

    @Override
    public Model getModel() {
        return model;
    }

    public void setModified() {
        if (!modified)
            Save.add(this);

        modified = true;
    }

    public void resetModified() {
        this.modified = false;
    }

    public boolean isModified() {
        return modified;
    }

    public Synapse getSynapseBySynId(Integer synId) {
        if(synId == null)
            return null;

        return inputSynapses.get(synId);
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
        outputSynapses.put(s.getOutputRef().getId(), s);
        outputLock.releaseWriteLock();
    }

    public void removeOutputSynapse(Synapse s) {
        outputLock.acquireWriteLock();
        outputSynapses.remove(s.getOutputRef().getId());
        outputLock.releaseWriteLock();
    }

    public Stream<Synapse> getInputSynapsesAsStream() {
        return getInputSynapses().stream();
    }

    public Collection<Synapse> getInputSynapses() {
        return inputSynapses.values();
    }

    public Stream<Synapse> getOutputSynapsesAsStream() {
        return getOutputSynapses().stream();
    }

    public Collection<Synapse> getOutputSynapses() {
        return outputSynapses.values();
    }

    public Synapse getOutputSynapse(Neuron n) {
        outputLock.acquireReadLock();
        Synapse syn = getOutputSynapsesAsStream()
                .filter(s -> s.getOutputRef().getId() == n.getId())
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

    public Synapse getInputSynapse(Neuron n) {
        inputLock.acquireReadLock();
        Synapse syn = selectInputSynapse(s ->
                s.getInputRef().getId() == n.getId()
        );

        inputLock.releaseReadLock();
        return syn;
    }

    public Synapse getInputSynapseByType(Type synapseType) {
        inputLock.acquireReadLock();
        Synapse is = getInputSynapsesByType(synapseType)
                .findAny()
                .orElse(null);
        inputLock.releaseReadLock();
        return is;
    }

    public Stream<Synapse> getInputSynapsesByType(Type synapseType) {
        return getInputSynapsesAsStream()
                .filter(synapseType::isInstanceOf);
    }

    public Synapse getOutputSynapseByType(Type synapseType) {
        outputLock.acquireReadLock();
        Synapse os = getOutputSynapsesByType(synapseType)
                .findAny()
                .orElse(null);
        outputLock.releaseReadLock();
        return os;
    }

    public Stream<Synapse> getOutputSynapsesByType(Type synapseType) {
        return getOutputSynapsesAsStream()
                .filter(synapseType::isInstanceOf);
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

    @Override
    public Timestamp getCreated() {
        return MIN;
    }

    @Override
    public Timestamp getFired() {
        return MAX;
    }

    @Override
    public Queue getQueue() {
        return getModel();
    }

    void increaseRefCount(RefType rt) {
        synchronized (refCountByType) {
            refCount++;
            refCountByType[rt.ordinal()]++;
        }
    }

    void decreaseRefCount(RefType rt) {
        synchronized (refCountByType) {
            refCount--;
            refCountByType[rt.ordinal()]--;

            assert refCount >= 0;
            assert refCountByType[rt.ordinal()] >= 0;
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

    public long getLastUsed() {
        return lastUsed;
    }

    public void updateLastUsed(long docId) {
        lastUsed = Math.max(lastUsed, docId);
    }

    public void save() {

    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(getClass().getCanonicalName());
        super.write(out);

        for (Synapse s : getInputSynapsesStoredAtOutputSide()) {
            out.writeBoolean(true);
            s.write(out);
        }
        out.writeBoolean(false);

        for (Synapse s : getOutputSynapsesStoredAtInputSide()) {
            out.writeBoolean(true);
            s.write(out);
        }
        out.writeBoolean(false);

        for (NeuronReference np: propagable.values()) {
            out.writeBoolean(true);
            out.writeLong(np.getId());
        }
        out.writeBoolean(false);

        out.writeInt(synapseIdCounter);
    }

    public static Neuron read(DataInput in, TypeRegistry tr) throws Exception {
        short neuronTypeId = in.readShort();
        NeuronDefinition neuronDefinition = (NeuronDefinition) tr.getType(neuronTypeId);
        Neuron n = neuronDefinition.instantiate((Model)tr);
        n.readFields(in, tr);
        return n;
    }

    @Override
    public void readFields(DataInput in, TypeRegistry tr) throws IOException {
        super.readFields(in, tr);

        while (in.readBoolean()) {
            Synapse syn = Synapse.read(in, tr);
            syn.link((Model)tr);
        }

        while (in.readBoolean()) {
            Synapse syn = Synapse.read(in, tr);
            syn.link((Model)tr);
        }

        while (in.readBoolean()) {
            NeuronReference nRef = new NeuronReference(in.readLong(), PROPAGABLE);
            propagable.put(nRef.getId(), nRef);
        }

        synapseIdCounter = in.readInt();
    }

    @Override
    public boolean equals(Object o) {
        return id.intValue() == ((Neuron) o).id.intValue();
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public int compareTo(Neuron n) {
        return Long.compare(id, n.id);
    }

    @Override
    public String toString() {
        return type.getName() + " " + toKeyString();
    }

    @Override
    public String toKeyString() {
        if(this == MIN_NEURON) return "MIN_NEURON";
        if(this == MAX_NEURON) return "MAX_NEURON";

        return "" + getId();
    }
}
