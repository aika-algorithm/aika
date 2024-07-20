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

import network.aika.fields.ActivationFunction;
import network.aika.Model;
import network.aika.Document;
import network.aika.elements.ModelProvider;
import network.aika.elements.PreActivation;
import network.aika.elements.NeuronType;
import network.aika.elements.activations.bsslots.BSSlotDefinition;
import network.aika.elements.typedef.NeuronDefinition;
import network.aika.elements.typedef.SynapseDefinition;
import network.aika.elements.typedef.TypeDefinition;
import network.aika.elements.typedef.Type;
import network.aika.enums.Scope;
import network.aika.enums.Trigger;
import network.aika.exceptions.NeuronExistsTwiceException;
import network.aika.fields.*;
import network.aika.elements.activations.Activation;
import network.aika.elements.Element;
import network.aika.elements.synapses.Synapse;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import network.aika.queue.Timestamp;
import network.aika.queue.steps.Save;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static network.aika.elements.neurons.RefType.*;
import static network.aika.queue.Timestamp.MAX;
import static network.aika.queue.Timestamp.MIN;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Neuron extends Type<NeuronDefinition, Neuron> implements Element, ModelProvider, QueueProvider, Writable {

    protected static final Logger LOG = LoggerFactory.getLogger(Neuron.class);

    protected static final String CATEGORY_LABEL = " Category";

    private int synapseIdCounter = 0;

    private volatile boolean modified;

    private NeuronProvider provider;

    private String label;

    private Writable customData;

    protected boolean allowTraining = true;

    private boolean instantiable = true;

    protected InitParams initParams;

    private Set<NeuronProvider> propagable = new HashSet<>();

    private final HashMap<Long, PreActivation> activations = new HashMap<>();

    private boolean isStale;

    public Neuron(NeuronProvider np) {
        provider = np;
    }

    public Neuron(Model m, RefType rt) {
        provider = new NeuronProvider(m, this, rt);
        setModified();
    }

    public NeuronType getType() {
        return typeDef.getType();
    }

    public Stream<BSSlotDefinition> getBindingSignalSlots() {
        return Arrays.stream(typeDef.getBindingSignalSlots());
    }

    public Long getId() {
        return provider.getId();
    }

    public void updatePropagable(NeuronProvider np, boolean isPropagable) {
        if (isPropagable)
            addPropagable(np);
        else
            removePropagable(np);
    }

    private void addPropagable(NeuronProvider np) {
        provider.outputLock.acquireWriteLock();
        if(propagable.add(np))
            np.increaseRefCount(PROPAGABLE_IN);

        provider.outputLock.releaseWriteLock();
        np.addPropagableRef(provider);
    }

    private void removePropagable(NeuronProvider np) {
        provider.outputLock.acquireWriteLock();
        if(propagable.remove(np))
            np.decreaseRefCount(PROPAGABLE_IN);

        provider.outputLock.releaseWriteLock();
        np.removePropagableRef(provider);
    }

    public void wakeupPropagable() {
        provider.outputLock.acquireReadLock();
        List<NeuronProvider> p = propagable
                .stream()
                .toList();
        provider.outputLock.releaseReadLock();

        p.forEach(NeuronProvider::getNeuron);
    }

    public Collection<NeuronProvider> getPropagable() {
        return propagable;
    }

    public void setSynapseIdCounter(int synapseIdCounter) {
        this.synapseIdCounter = synapseIdCounter;
    }

    public int getNewSynapseId() {
        return synapseIdCounter++;
    }

    public void register(Activation act) {
        Document doc = act.getDocument();
        PreActivation npd = getOrCreatePreActivation(doc);
        npd.addActivation(act);
        provider.updateLastUsed(doc.getId());
    }

    public boolean inUse() {
        synchronized (activations) {
            return !activations.isEmpty();
        }
    }

    public PreActivation getOrCreatePreActivation(Document doc) {
        PreActivation preAct;
        synchronized (activations) {
            preAct = activations
                    .computeIfAbsent(
                            doc.getId(),
                            docId -> new PreActivation(doc, this)
                    );
        }
        return preAct;
    }

    public Stream<PreActivation> getPreActivations() {
        synchronized (activations) {
            return activations.values()
                    .stream()
                    .filter(Objects::nonNull);
        }
    }

    public PreActivation getPreActivation(Document doc) {
        synchronized (activations) {
            if (doc == null)
                return null;

            return activations.get(doc.getId());
        }
    }

    public void removePreActivation(Document doc) {
        synchronized (activations) {
            PreActivation removedPreAct = activations.remove(doc.getId());
            removedPreAct.disconnect();
        }
    }

    public SortedSet<Activation> getActivations(Document doc) {
        PreActivation preAct = getPreActivation(doc);

        return preAct != null ?
                preAct.getActivations() :
                Collections.emptyNavigableSet();
    }

    public Neuron instantiateTemplate() {
        Neuron n;
        try {
            n = getClass()
                    .getConstructor(Model.class, RefType.class)
                    .newInstance(getModel(), TEMPLATE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        n.setModified();
        n.initFromTemplate(this);
        return n;
    }

    protected void initFromTemplate(Neuron templateN) {
/*        bias.setInitialValue(
                templateN.getBias().getUpdatedValue()
        );
*/
        if(templateN.initParams != null) {
            InitParams ip = templateN.initParams;
            setTargetNet(ip.targetNet);
        }
/*
        Synapse cis = templateN.getCategoryInputSynapse();
        if(cis == null)
            throw new MissingInputCategoryNeuron(templateN);

        createCategorySynapse()
                .setWeight(cis.getInitialInstanceWeight())
                .link(this, cis.getInput());*/
    }

    public Neuron setInstantiable(boolean instantiable) {
        this.instantiable = instantiable;

        return this;
    }

    public boolean isInstantiable() {
        return instantiable;
    }


    public final boolean isTrainingAllowed() {
        return typeDef.isTrainingAllowed();
    }

    public final Activation createActivation(Document doc) {
        return typeDef.getActivationType()
                .instantiate(doc.createActivationId(), doc, this);
    }

    public abstract void addInactiveLinks(Activation act);

    public final ActivationFunction getActivationFunction() {
        return typeDef.getActivationFunction();
    }

    public void count(Activation act) {
    }

    public void setAllowTraining(boolean allowTraining) {
        this.allowTraining = allowTraining;
    }

    public NeuronProvider getProvider() {
        return provider;
    }

    public void setProvider(NeuronProvider p) {
        this.provider = p;
    }

    public void delete() {
        LOG.info("Delete Neuron: " + this);
        provider.inputLock.acquireReadLock();
        provider.getInputSynapses()
                .forEach(Synapse::unlinkInput);
        provider.inputLock.releaseReadLock();

        provider.outputLock.acquireReadLock();
        provider.getOutputSynapses()
                .forEach(Synapse::unlinkOutput);
        provider.outputLock.releaseReadLock();
    }

    public Writable getCustomData() {
        return customData;
    }

    public void setCustomData(Writable customData) {
        this.customData = customData;
    }

    @Override
    public Model getModel() {
        if(provider == null)
            return null;

        return provider.getModel();
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

    public void suspend() {
        for (Synapse s : provider.getInputSynapsesStoredAtOutputSide()) {
            NeuronProvider in = s.getPInput();

            in.decreaseRefCount(SYNAPSE_IN);
            provider.decreaseRefCount(SYNAPSE_OUT);

            provider.removeInputSynapse(s);
            in.removeOutputSynapse(s);
        }
        for (Synapse s : provider.getOutputSynapsesStoredAtInputSide()) {
            NeuronProvider out = s.getPOutput();

            out.decreaseRefCount(SYNAPSE_OUT);
            provider.decreaseRefCount(SYNAPSE_IN);

            provider.removeOutputSynapse(s);
            out.removeInputSynapse(s);
        }

        for(NeuronProvider np: propagable) {
            np.decreaseRefCount(PROPAGABLE_IN);

            np.removePropagableRef(provider);
        }
        propagable = null;

        isStale = true;
    }

    public void reactivate(Model m) {
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(getClass().getCanonicalName());

        out.writeBoolean(label != null);
        if(label != null)
            out.writeUTF(label);

        for (Synapse s : getProvider().getInputSynapsesStoredAtOutputSide()) {
            out.writeBoolean(true);
            s.write(out);
        }
        out.writeBoolean(false);

        for (Synapse s : getProvider().getOutputSynapsesStoredAtInputSide()) {
            out.writeBoolean(true);
            s.write(out);
        }
        out.writeBoolean(false);

        for (NeuronProvider np: propagable) {
            out.writeBoolean(true);
            out.writeLong(np.getId());
        }
        out.writeBoolean(false);

        out.writeBoolean(customData != null);
        if(customData != null)
            customData.write(out);

        out.writeBoolean(instantiable);
        out.writeInt(synapseIdCounter);

        out.writeBoolean(initParams != null);
        if(initParams != null)
            initParams.write(out);

        out.writeBoolean(getProvider().isPersistent());
    }

    public static Neuron read(DataInput in, NeuronProvider np) throws Exception {
        String neuronClazz = in.readUTF();
        Model m = np.getModel();
        Neuron n = m.createNeuronByClass(neuronClazz, np);
        n.readFields(in, m);
        return n;
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        if(in.readBoolean())
            label = in.readUTF();

        while (in.readBoolean()) {
            Synapse syn = Synapse.read(in, m);
            syn.link();
            assert provider == syn.getPOutput();
        }

        while (in.readBoolean()) {
            Synapse syn = Synapse.read(in, m);
            syn.link();
            assert provider == syn.getPInput();
        }

        while (in.readBoolean()) {
            NeuronProvider np = m.lookupNeuronProvider(in.readLong(), null);
            addPropagable(np);
        }

        if(in.readBoolean()) {
            customData = m.getCustomDataInstanceSupplier().get();
            customData.readFields(in, m);
        }

        instantiable = in.readBoolean();
        synapseIdCounter = in.readInt();

        if(in.readBoolean())
            initParams = InitParams.read(in, m);

        if(in.readBoolean())
            setPersistent(true);
    }

    public Stream<Synapse> getInputSynapsesAsStream() {
        return provider.getInputSynapsesAsStream();
    }

    public Collection<Synapse> getInputSynapses() {
        return provider.getInputSynapses();
    }

    public Stream<? extends Synapse> getOutputSynapsesAsStream() {
        return provider.getOutputSynapsesAsStream();
    }

    public List<? extends Synapse> getOutputSynapsesByTriggerAndBSType(Trigger t, Scope bsType) {
        return provider.getOutputSynapsesByTriggerAndBSType(t, bsType);
    }

    public Collection<? extends Synapse> getOutputSynapses() {
        return provider.getOutputSynapses();
    }

    public Synapse getOutputSynapse(NeuronProvider n) {
        return provider.getOutputSynapse(n);
    }

    public List<Synapse> getInputSynapsesStoredAtOutputSide() {
        return provider.getInputSynapsesStoredAtOutputSide();
    }

    public List<? extends Synapse> getOutputSynapsesStoredAtInputSide() {
        return provider.getOutputSynapsesStoredAtInputSide();
    }

    public Synapse getInputSynapse(NeuronProvider n) {
        return provider.getInputSynapse(n);
    }

    public Synapse getInputSynapseByType(TypeDefinition<SynapseDefinition, Synapse> synapseType) {
        return provider.getInputSynapseByType(synapseType);
    }

    public Stream<Synapse> getInputSynapsesByType(TypeDefinition<SynapseDefinition, Synapse> synapseType) {
        return provider.getInputSynapsesByType(synapseType);
    }

    public Synapse getOutputSynapseByType(TypeDefinition<SynapseDefinition, Synapse> synapseType) {
        return provider.getOutputSynapseByType(synapseType);
    }

    public Synapse selectInputSynapse(Predicate<? super Synapse> predicate) {
        return provider.selectInputSynapse(predicate);
    }

    public void addPropagableRef(NeuronProvider np) {
        provider.addPropagableRef(np);
    }

    public void removePropagableRef(NeuronProvider np) {
        provider.removePropagableRef(np);
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

    public String getLabel() {
        return label;
    }

    public Neuron  setLabel(String label) {
        this.label = label;
        return this;
    }

    public Neuron  setTargetNet(double targetNet) {
        if(initParams == null) {
            initParams = new InitParams();
        }
        initParams.targetNet = targetNet;

        return this;
    }

    public Neuron setPersistent(boolean persistent) {
        getProvider().setPersistent(persistent);
        return this;
    }

    public void verifyNeuronExistsOnlyOnce() {
        if(getProvider().getNeuron() != this)
            throw new NeuronExistsTwiceException(getId());
    }

    public double getTargetValue() {
        Double tNet = getTargetNet();
        if(tNet == null)
            return 1.0;

        return getActivationFunction().f(tNet);
    }

    public Double getTargetNet() {
        if(initParams == null)
            return null;

        return initParams.targetNet;
    }

    public String toKeyString() {
        return (provider != null ? getId() : "[no provider]") + ":" + (getLabel() != null ? getLabel() : "--");
    }

    public String toString() {
        return getClass().getSimpleName() + " " + toKeyString();
    }
}
