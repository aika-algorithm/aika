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
package network.aika.elements.activations;

import network.aika.Model;
import network.aika.Document;
import network.aika.elements.Element;
import network.aika.elements.NeuronType;
import network.aika.elements.activations.bsslots.BSSlotDefinition;
import network.aika.elements.activations.bsslots.BindingSignalSlot;
import network.aika.elements.activations.bsslots.SingleBSSlot;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.CategoryInputLink;
import network.aika.elements.links.CategoryLink;
import network.aika.elements.links.Link;
import network.aika.ActivationFunction;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.elements.typedef.ActivationTypeDefinition;
import network.aika.elements.typedef.Type;
import network.aika.enums.Scope;
import network.aika.queue.Queue;
import network.aika.queue.Timestamp;
import network.aika.queue.steps.InactiveLinks;
import network.aika.text.TextReference;
import network.aika.Range;
import network.aika.fields.*;

import java.util.*;
import java.util.stream.Stream;

import static network.aika.debugger.EventType.*;
import static network.aika.elements.activations.StateType.PRE_FEEDBACK;
import static network.aika.elements.activations.StateType.INNER_FEEDBACK;
import static network.aika.elements.neurons.RefType.TEMPLATE;
import static network.aika.fields.link.FieldLink.linkAndConnect;
import static network.aika.queue.Phase.*;
import static network.aika.queue.Timestamp.NOT_SET;
import static network.aika.text.TextReference.join;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public abstract class Activation implements Type<ActivationTypeDefinition, Activation>, Element, Comparable<Activation> {

    public static final Comparator<Activation> ID_COMPARATOR = Comparator.comparingInt(Activation::getId);

    protected final int id;
    protected Neuron neuron;
    protected Document doc;

    private ActivationTypeDefinition activationType;

    protected Timestamp created = NOT_SET;

    protected State[] states = new State[numberOfStates()];

    protected FieldFunction netOuterGradient;
    protected Field gradient;

    protected Field updateValue;

    protected FieldOutput negUpdateValue;

    protected NavigableMap<Integer, SynapseSlot> inputSlots = new TreeMap<>();
    protected NavigableMap<Long, SynapseSlot> outputSlots = new TreeMap<>();

    public boolean instantiationIsQueued;
    protected boolean isNewInstance;

    protected TextReference textReference;

    protected BindingSignalSlot[] bindingSignalSlots = new BindingSignalSlot[2];

    protected HashMap<Integer, Integer> templateSynIdMap;

    public Activation(int id, Document doc, Neuron n) {
        this.id = id;
        this.neuron = n;
        this.doc = doc;

        neuron.register(this);
        doc.register(this);

        setCreated(doc.getCurrentTimestamp());

        initNet();

        initBiases();

        initBindingSignalSlots();

        gradient = new SumField(this, "gradient", TOLERANCE)
                .setQueued(getQueue(), TRAINING, false);

        if (getConfig().isTrainingEnabled() && neuron.isTrainingAllowed()) {
            connectGradientFields();
            connectWeightUpdate();
            InactiveLinks.add(this);
        }

        doc.onElementEvent(CREATE, this);
    }


    public NeuronType getType() {
        return neuron.getType();
    }

    public void setTypeDefinition(ActivationTypeDefinition typeDef) {
        activationType = typeDef;
    }

    protected void connectWeightUpdate() {
    }

    public void registerTemplateInstanceSynapse(int templateSynId, int instanceSynId) {
        if(templateSynIdMap == null)
            templateSynIdMap = new HashMap<>();

        templateSynIdMap.put(templateSynId, instanceSynId);
    }

    public Integer getInstanceSynapseId(int templateSynId) {
        if(templateSynIdMap == null)
            return null;

        return templateSynIdMap.get(templateSynId);
    }

    public BindingSignalSlot getBindingSignalSlot(Scope t) {
        return bindingSignalSlots[t.ordinal()];
    }

    public Stream<BindingSignalSlot> getBindingSignalSlots() {
        return Arrays.stream(bindingSignalSlots)
                .filter(Objects::nonNull);
    }

    public SynapseSlot registerOutputSlot(Synapse syn) {
        return outputSlots.computeIfAbsent(syn.getOutput().getId(), nId ->
                syn.createAndInitInputSlot(this)
        );
    }

    public SynapseSlot registerInputSlot(Synapse syn) {
        return inputSlots.computeIfAbsent(syn.getSynapseId(), nId ->
            syn.createAndInitOutputSlot(this)
        );
    }

    protected void initNet() {
        Stream.of(activationType.getStateTypes())
                .forEach(sd -> states[sd.getType().ordinal()] = sd.instantiate(this));
    }

    protected final int numberOfStates() {
        return activationType.getStateTypes().length;
    }

    protected void initBiases() {
        linkAndConnect(getNeuron().getBias(), getNet(PRE_FEEDBACK))
                .setPropagateUpdates(false);
    }

    protected void initBindingSignalSlots() {
        Stream<BSSlotDefinition> bsSlots = neuron.getBindingSignalSlots();
        bsSlots.forEach(slotDef ->
                bindingSignalSlots[slotDef.getScope().ordinal()] = BindingSignalSlot.create(this, slotDef)
        );
    }

    public void propagateBindingSignal(Scope t, PatternActivation bs, boolean state) {
        getOutputLinks()
                .forEach(l ->
                        l.propagateBindingSignal(bs, t, state)
                );
    }

    public PatternActivation getBindingSignal(Scope t) {
        SingleBSSlot slot = (SingleBSSlot) getBindingSignalSlot(t);

        return slot != null ?
                slot.getBindingSignal() :
                null;
    }

    public boolean isNewInstance() {
        return isNewInstance;
    }

    public boolean isAbstract() {
        return neuron.isAbstract();
    }

    protected void connectGradientFields() {
        netOuterGradient =
                func(
                        this,
                        "f'(net)",
                        TOLERANCE,
                        getNet(PRE_FEEDBACK),
                        x -> getNeuron().getActivationFunction().outerGrad(x)
        );
    }

    public FieldFunction getNetOuterGradient() {
        return netOuterGradient;
    }

    public Field getGradient() {
        return gradient;
    }

    public Field getUpdateValue() {
        return updateValue;
    }

    public FieldOutput getNegUpdateValue() {
        return negUpdateValue;
    }

    public int getId() {
        return id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp ts) {
        this.created = ts;
    }

    public Field getValue() {
        return getValue(PRE_FEEDBACK);
    }

    public State getState(StateType st) {
        if(st.ordinal() >= states.length)
            return states[states.length - 1];

        return states[st.ordinal()];
    }

    public Field getValue(StateType st) {
        if(st == null)
            return getValue();

        return getState(st).value;
    }

    public void setNet(StateType st, double v) {
        getNet(st).setValue(v);
    }

    public SumField getNet(StateType st) {
        return getState(st).net;
    }

    public void setFired(StateType st, Timestamp fired) {
        getState(st).fired = fired;
    }

    public Timestamp getFired(StateType st) {
        if(st == null)
            return NOT_SET;

        return getState(st).fired;
    }

    @Override
    public Timestamp getFired() {
        return getFired(PRE_FEEDBACK);
    }

    public boolean isFired(StateType st) {
        return getState(st).isFired();
    }

    public Document getDocument() {
        return doc;
    }

    @Override
    public Queue getQueue() {
        return doc;
    }

    public TextReference getTextReference() {
        return textReference;
    }

    public void updateRanges(TextReference tr) {
        TextReference newTextReference = join(textReference, tr);

        if (textReference == null || !textReference.equals(newTextReference)) {
            registerPosRange(textReference, newTextReference);
            this.textReference = newTextReference;

            propagateRanges();
        }
        doc.onElementEvent(TOKEN_POSITION, this);
    }

    protected void registerPosRange(TextReference oldTextReference, TextReference newTextReference) {
        getNeuron().getOrCreatePreActivation(doc)
                .updateTextReference(
                        this,
                        oldTextReference,
                        newTextReference
                );
    }

    protected void propagateRanges() {
        getOutputLinks()
                .forEach(Link::propagateRanges);
    }

    public Range getAbsoluteCharRange() {
        if(textReference == null)
            return null;
        Range r = textReference.getCharRange();
        return r.getAbsoluteRange(doc.getCharRange());
    }

    @Override
    public int compareTo(Activation act) {
        return ID_COMPARATOR.compare(this, act);
    }

    public String getLabel() {
        return getNeuron().getLabel();
    }

    public boolean checkVisited(long v) {
        return false;
    }

    public Neuron getNeuron() {
        return neuron;
    }

    public void setNeuron(Neuron n) {
        this.neuron = n;
    }

    public State[] getStates() {
        return states;
    }

    public ActivationFunction getActivationFunction() {
        return neuron.getActivationFunction();
    }

    @Override
    public Model getModel() {
        return neuron.getModel();
    }

    public NeuronProvider getNeuronProvider() {
        return neuron.getProvider();
    }

    public boolean checkPrimary() {
        return true;
    }

    public <IL extends Link> Optional<IL> getInputLinkByType(Class<IL> linkType) {
        return getInputLinksByType(linkType)
                .findAny();
    }

    public Stream<Link> getInputLinks() {
        return getInputSlots()
                .flatMap(sl -> sl.getLinks())
                .toList().stream();
    }

    public Stream<SynapseSlot> getInputSlots() {
        return inputSlots.values()
                .stream();
    }

    public SynapseSlot getInputSlotBySynapseType(Class<? extends Synapse> synType) {
        return getInputSlots()
                .filter(s -> synType.isInstance(s.getSynapse()))
                .findFirst()
                .orElse(null);
    }

    public <IS extends SynapseSlot> Stream<IS> getInputSlotsByType(Class<IS> slotType) {
        return getInputSlots()
                .filter(slotType::isInstance)
                .map(slotType::cast);
    }

    public Stream<Link> getOutputLinks() {
        return getOutputSlots()
                .flatMap(sl -> sl.getLinks())
                .toList().stream();
    }

    public Stream<SynapseSlot> getOutputSlots() {
        return outputSlots.values()
                .stream();
    }

    public <OS extends SynapseSlot> Stream<OS> getOutputSlotsByType(Class<OS> slotType) {
        return getOutputSlots()
                .filter(slotType::isInstance)
                .map(slotType::cast);
    }

    public Link getInputLink(Activation iAct, int synapseId) {
        SynapseSlot synSlot = inputSlots.get(synapseId);
        return synSlot != null ?
                synSlot.getLink(iAct) :
                null;
    }

    public Stream<Link> getInputLinks(Synapse s) {
        SynapseSlot synSlot = inputSlots.get(s.getSynapseId());
        return synSlot != null ?
                synSlot.getLinks() :
                Stream.empty();
    }

    public <IL> Stream<IL> getInputLinksByType(Class<IL> linkType) {
        return getInputLinks()
                .filter(linkType::isInstance)
                .map(linkType::cast);
    }

    public <OL> Stream<OL> getOutputLinksByType(Class<OL> linkType) {
        return getOutputLinks()
                .filter(linkType::isInstance)
                .map(linkType::cast);
    }

    public Stream<Link> getOutputLinks(Neuron n) {
        SynapseSlot synSlot = outputSlots.get(n.getId());
        return synSlot != null ?
                synSlot.getLinks() :
                Stream.empty();
    }

    public Stream<Link> getOutputLinks(Synapse s) {
        return getOutputLinks(s.getOutput())
                .filter(l -> l.getSynapse() == s);
    }

    @Override
    public void disconnect() {
        for(State s: states)
            s.disconnect();

        if(updateValue != null)
            updateValue.disconnectAndUnlinkOutputs(false);

        if(negUpdateValue != null)
            negUpdateValue.disconnectAndUnlinkOutputs(false);

        getInputSlots().forEach(SynapseSlot::disconnect);
    }

    public Activation getTemplate() {
        return getCategoryActivations()
                .map(CategoryActivation::getTemplate)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public Stream<CategoryActivation> getCategoryActivations() {
        return getOutputLinksByType(CategoryLink.class)
                .map(l -> (CategoryActivation) l.getOutput())
                .filter(Objects::nonNull);
    }

    public Stream<Activation> getTemplateInstances() {
        Link cil = getActiveCategoryInputLink();
        if(cil == null || cil.getInput() == null)
            return Stream.empty();

        return cil.getInput()
                .getCategoryInputs();
    }

    public boolean isActiveTemplateInstance() {
        return isNewInstance ||
                isFired(INNER_FEEDBACK);
    }

    public abstract Link getActiveCategoryInputLink();

    public Activation resolveAbstractInputActivation() {
        return isInstantiable() ?
                getActiveTemplateInstance() :
                this;
    }

    public Activation getActiveTemplateInstance() {
        Link l = getActiveCategoryInputLink();
        return l != null && l.getInput() != null ?
                l.getInput().getActiveTemplateInstance() :
                null;
    }

    public boolean isInstantiable() {
        return neuron.isInstantiable() && neuron.isAbstract();
    }

    public Activation instantiateTemplateNode() {
        if(!neuron.isInstantiable())
            return null;

        Neuron n = null;
        if(doc.getInstantiationCallback() != null) {
            n = doc.getInstantiationCallback().resolveInstance(neuron, doc);
        }

        boolean newNeuronInstance = false;
        if(n == null) {
            n = neuron.instantiateTemplate();
            newNeuronInstance = true;
        }

        Activation ti = n.createActivation(getDocument());

        ti.textReference = textReference;
        ti.isNewInstance = true;
        ti.setFired(INNER_FEEDBACK, getFired(INNER_FEEDBACK));

        doc.onElementEvent(TOKEN_POSITION, ti);

        linkTemplateAndInstance(ti);
        instantiateTemplateEdges(ti);

        if(newNeuronInstance && doc.getInstantiationCallback() != null)
            doc.getInstantiationCallback().onInstantiation(this, ti);

        if(ti != null && ti.getLabel() != null)
            getModel().registerLabel(ti.getNeuron(), neuron);

        if(newNeuronInstance)
            n.getProvider().decreaseRefCount(TEMPLATE);

        return ti;
    }

    private void linkTemplateAndInstance(Activation ti) {
        Link cl = getActiveCategoryInputLink();
        if(cl == null)
            cl = createCategoryInputLink();

        cl.instantiateTemplate(cl.getInput(), ti);
    }

    public Link createCategoryInputLink() {
        Synapse cis = getNeuron().getCategoryInputSynapse();
        if(cis == null)
            return null;

        Activation catAct = cis.getInput().createActivation(doc);
        return cis.createAndInitLink(catAct, this);
    }

    public void instantiateTemplateEdges(Activation instanceAct) {
        getInputLinks()
                .filter(l -> l.getInput() != null)
                .filter(l -> l.getInput().isFired(INNER_FEEDBACK))
                .forEach(l ->
                        l.instantiateTemplate(
                                l.getInput().resolveAbstractInputActivation(),
                                instanceAct
                        )
                );

        instanceAct.initFromTemplate(this);

        getOutputLinks()
                .filter(l -> !(l instanceof CategoryLink))
                .filter(l -> l.getOutput().isFired(INNER_FEEDBACK))
                .forEach(l ->
                        l.instantiateTemplate(
                                instanceAct,
                                l.getOutput().resolveAbstractInputActivation()
                        )
                );
    }

    public void initFromTemplate(Activation templateAct) {
        setFired(INNER_FEEDBACK, templateAct.getFired(INNER_FEEDBACK));
        doc.onElementEvent(UPDATE, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activation)) return false;
        Activation that = (Activation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + toKeyString();
    }

    public String toKeyString() {
        return "id:" + getId() + " n:[" + getNeuron().toKeyString() + "]";
    }
}
