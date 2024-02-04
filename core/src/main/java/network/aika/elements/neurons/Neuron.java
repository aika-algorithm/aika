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

import network.aika.ActivationFunction;
import network.aika.Model;
import network.aika.Document;
import network.aika.elements.PreActivation;
import network.aika.elements.Type;
import network.aika.elements.activations.bsslots.BSSlotDefinition;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.exceptions.MissingInputCategoryNeuron;
import network.aika.exceptions.NeuronExistsTwiceException;
import network.aika.fields.*;
import network.aika.elements.activations.Activation;
import network.aika.elements.Element;
import network.aika.elements.Timestamp;
import network.aika.elements.synapses.Synapse;
import network.aika.queue.steps.Save;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static network.aika.elements.neurons.NeuronTypeHolder.getHolder;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.elements.Timestamp.MAX;
import static network.aika.elements.Timestamp.MIN;
import static network.aika.queue.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Neuron<N extends Neuron, A extends Activation> implements Element, Writable {

    protected static final Logger log = LoggerFactory.getLogger(Neuron.class);

    protected static final String CATEGORY_LABEL = " Category";

    private final NeuronTypeHolder neuronType = getHolder(getClass());

    private int synapseIdCounter = 0;

    private volatile boolean modified;

    private NeuronProvider provider;

    private String label;

    private Writable customData;

    protected SumField bias = initBias();

    protected boolean allowTraining = true;

    private boolean notInstantiable;

    protected InitParams initParams;

    private Set<NeuronProvider> propagable = new HashSet<>();

    private final WeakHashMap<Long, WeakReference<PreActivation<A>>> activations = new WeakHashMap<>();

    public Neuron(NeuronProvider np) {
        provider = np;
        setModified();
        setBias(0.0);
    }

    public Neuron(Model m) {
        provider = new NeuronProvider(m, this);
        setModified();
        setBias(0.0);
    }

    public Type getType() {
        return neuronType.getType();
    }

    public Stream<BSSlotDefinition> getBindingSignalSlots() {
        return Arrays.stream(neuronType.getBindingSignalSlots());
    }

    public Long getId() {
        return provider.getId();
    }

    @Override
    public void disconnect() {
    }

    public void updatePropagable(NeuronProvider np, boolean isPropagable) {
        if(isPropagable) {
            addPropagable(np);
        } else {
            removePropagable(np);
        }
    }

    private void addPropagable(NeuronProvider np) {
        propagable.add(np);
        np.addPropagableRef(provider);
    }

    private void removePropagable(NeuronProvider np) {
        propagable.remove(np);
        np.removePropagableRef(provider);
    }

    public void wakeupPropagable() {
        propagable.forEach(NeuronProvider::getNeuron);
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

    public void register(A act) {
        Document doc = act.getDocument();
        PreActivation<A> npd = getOrCreatePreActivation(doc);
        npd.addActivation(act);
        provider.updateLastUsed(doc.getId());
    }

    public PreActivation<A> getOrCreatePreActivation(Document doc) {
        PreActivation<A> npd;
        synchronized (activations) {
            WeakReference<PreActivation<A>> weakRef = activations
                    .computeIfAbsent(
                            doc.getId(),
                            n -> new WeakReference<>(
                                    new PreActivation<>(doc, provider)
                            )
                    );

            npd = weakRef.get();
        }
        return npd;
    }

    public Stream<PreActivation<A>> getPreActivations() {
        synchronized (activations) {
            return activations.values()
                    .stream()
                    .map(Reference::get)
                    .filter(Objects::nonNull);
        }
    }

    public PreActivation<A> getPreActivation(Document doc) {
        if(doc == null)
            return null;

        WeakReference<PreActivation<A>> weakRef = activations.get(doc.getId());
        return weakRef != null ?
                weakRef.get() :
                null;
    }

    public SortedSet<A> getActivations(Document doc) {
        PreActivation<A> preAct = getPreActivation(doc);

        return preAct != null ?
                preAct.getActivations() :
                Collections.emptyNavigableSet();
    }

    public <R extends N> R instantiateTemplate() {
        R n;
        try {
            n = (R) getClass()
                    .getConstructor(Model.class)
                    .newInstance(getModel());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        n.initFromTemplate(this);
        return n;
    }

    protected void initFromTemplate(Neuron templateN) {
        bias.setInitialValue(
                templateN.getBias().getUpdatedValue()
        );

        if(templateN.initParams != null) {
            InitParams ip = templateN.initParams;
            setTargetNet(ip.targetNet);
        }

        CategoryInputSynapse cis = templateN.getCategoryInputSynapse();
        if(cis == null)
            throw new MissingInputCategoryNeuron(templateN);

        createCategorySynapse()
                .setWeight(cis.getInitialInstanceWeight())
                .link(this, cis.getInput());
    }

    public N setNotInstantiable(boolean notInstantiable) {
        this.notInstantiable = notInstantiable;

        return (N) this;
    }

    public N setNotInstantiable(boolean notInstantiable, boolean includeSyns) {
        if (includeSyns)
            getInputSynapses().forEach(s ->
                    s.setNotInstantiable(notInstantiable)
            );

        return setNotInstantiable(notInstantiable);
    }

    public boolean isNotInstantiable() {
        return notInstantiable;
    }

    public abstract CategorySynapse createCategorySynapse();

    public boolean isAbstract() {
        return getCategoryInputSynapse() != null;
    }

    public abstract CategoryInputSynapse makeAbstract();

    public abstract CategoryInputSynapse getCategoryInputSynapse();

    public abstract CategorySynapse getCategoryOutputSynapse();

    public boolean isTrainingAllowed() {
        return true;
    }

    public abstract A createActivation(Document doc);

    public abstract void addInactiveLinks(Activation act);

    public abstract ActivationFunction getActivationFunction();

    public void count(A act) {
    }

    protected SumField initBias() {
        return (SumField) new SumField(this, "bias", TOLERANCE)
                .setQueued(getDocument(), TRAINING)
                .addListener("onBiasModified", (fl, u) ->
                        setModified()
                );
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

    public Stream<Synapse> getInputSynapsesAsStream() {
        return getInputSynapses().stream();
    }

    public Collection<Synapse> getInputSynapses() {
        return provider.inputSynapses.values();
    }

    public Stream<? extends Synapse> getOutputSynapsesAsStream() {
        return getOutputSynapses().stream();
    }

    public Collection<? extends Synapse> getOutputSynapses() {
        return provider.outputSynapses.values();
    }

    public Synapse getOutputSynapse(NeuronProvider n) {
        provider.lock.acquireReadLock();
        Synapse syn = getOutputSynapsesAsStream()
                .filter(s -> s.getPOutput().getId() == n.getId())
                .findFirst()
                .orElse(null);
        provider.lock.releaseReadLock();
        return syn;
    }

    public Synapse getInputSynapse(NeuronProvider n) {
        provider.lock.acquireReadLock();
        Synapse syn = selectInputSynapse(s ->
                s.getPInput().getId() == n.getId()
        );

        provider.lock.releaseReadLock();
        return syn;
    }

    public <IS extends Synapse> IS getInputSynapseByType(Class<IS> synapseType) {
        return getInputSynapsesByType(synapseType)
                .findAny()
                .orElse(null);
    }

    public <IS extends Synapse> Stream<IS> getInputSynapsesByType(Class<IS> synapseType) {
        return getProvider().getInputSynapses()
                .filter(synapseType::isInstance)
                .map(synapseType::cast);
    }

    public <OS> OS getOutputSynapseByType(Class<OS> synapseType) {
        return getProvider().getOutputSynapses()
                .filter(synapseType::isInstance)
                .map(synapseType::cast)
                .findAny()
                .orElse(null);
    }

    protected Synapse selectInputSynapse(Predicate<? super Synapse> predicate) {
        return getInputSynapsesAsStream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    public void addInputSynapse(Synapse s) {
        setModified();
    }

    public void removeInputSynapse(Synapse s) {
        setModified();
    }

    public void addOutputSynapse(Synapse s) {
        setModified();
    }

    public void removeOutputSynapse(Synapse s) {
        setModified();
    }

    public void delete() {
        log.info("Delete Neuron: " + this);
        provider.getInputSynapses().forEach(Synapse::unlinkInput);
        provider.getOutputSynapses().forEach(Synapse::unlinkOutput);
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

    public SumField getBias() {
        return bias;
    }

    public double getCurrentCompleteBias() {
        return getBias().getUpdatedValue();
    }

    public void suspend() {
        for (Synapse s : getInputSynapsesAsStream()
                .filter(s -> s.getStoredAt() == OUTPUT)
                .toList()
        ) {
            provider.removeInputSynapse(s);
            s.getPInput().removeOutputSynapse(s);
        }
        for (Synapse s : getOutputSynapsesAsStream()
                .filter(s -> s.getStoredAt() == INPUT)
                .toList()
        ) {
            provider.removeOutputSynapse(s);
            s.getPOutput().removeInputSynapse(s);
        }

        for(NeuronProvider np: propagable) {
            np.removePropagableRef(provider);
        }
    }

    public void reactivate(Model m) {
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(getClass().getCanonicalName());

        out.writeBoolean(label != null);
        if(label != null)
            out.writeUTF(label);

        bias.write(out);

        for (NeuronProvider np: propagable) {
            out.writeBoolean(true);
            out.writeLong(np.getId());
        }
        out.writeBoolean(false);

        for (Synapse s : getInputSynapses()) {
            if (s.getStoredAt() == OUTPUT) {
                out.writeBoolean(true);
                s.write(out);
            }
        }
        out.writeBoolean(false);

        for (Synapse s : getOutputSynapses()) {
            if (s.getStoredAt() == INPUT) {
                out.writeBoolean(true);
                s.write(out);
            }
        }
        out.writeBoolean(false);

        out.writeBoolean(customData != null);
        if(customData != null)
            customData.write(out);

        out.writeBoolean(notInstantiable);
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

        bias.readFields(in, m);

        while (in.readBoolean()) {
            NeuronProvider np = m.lookupNeuronProvider(in.readLong());
            addPropagable(np);
        }

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

        if(in.readBoolean()) {
            customData = m.getCustomDataInstanceSupplier().get();
            customData.readFields(in, m);
        }

        notInstantiable = in.readBoolean();
        synapseIdCounter = in.readInt();

        if(in.readBoolean())
            initParams = InitParams.read(in, m);

        if(in.readBoolean())
            setPersistent(true);
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
    public Document getDocument() {
        Model m = getModel();
        if(m == null)
            return null;

        return m.getCurrentDocument();
    }

    public String getLabel() {
        return label;
    }

    public <R extends N> R  setLabel(String label) {
        this.label = label;
        return (R) this;
    }

    public <R extends N> R setBias(double bias) {
        getBias().setValue(bias);
        return (R) this;
    }

    public <R extends N> R  setTargetNet(double targetNet) {
        if(initParams == null) {
            initParams = new InitParams();
        }
        initParams.targetNet = targetNet;

        return (R) this;
    }

    public <R extends N> R setPersistent(boolean persistent) {
        getProvider().setPersistent(persistent);
        return (R) this;
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
