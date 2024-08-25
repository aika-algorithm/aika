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
import network.aika.elements.ModelProvider;
import network.aika.elements.NeuronType;
import network.aika.elements.activations.bsslots.BSSlotDefinition;
import network.aika.elements.activations.bsslots.BindingSignalSlot;
import network.aika.elements.activations.bsslots.SingleBSSlot;
import network.aika.elements.links.Link;
import network.aika.fielddefs.Type;
import network.aika.fields.ActivationFunction;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.elements.typedef.*;
import network.aika.enums.Scope;
import network.aika.fields.ObjImpl;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import network.aika.queue.Timestamp;
import network.aika.text.TextReference;
import network.aika.Range;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static network.aika.elements.activations.StateType.NON_FEEDBACK;
import static network.aika.elements.activations.StateType.INNER_FEEDBACK;
import static network.aika.elements.neurons.RefType.TEMPLATE;
import static network.aika.elements.typedef.FieldTags.BIAS;
import static network.aika.elements.typedef.FieldTags.NET;
import static network.aika.queue.Timestamp.NOT_SET;
import static network.aika.text.TextReference.join;
import static network.aika.utils.StringUtils.depthToSpace;

/**
 * @author Lukas Molzberger
 */
public class Activation extends ObjImpl<ActivationDefinition, Activation> implements Element, ModelProvider, QueueProvider, Comparable<Activation> {

    public static final Comparator<Activation> ID_COMPARATOR = Comparator.comparingInt(Activation::getId);

    protected final Integer id;
    protected Neuron neuron;
    protected Document doc;

    protected Timestamp created = NOT_SET;

    protected State[] states;

    protected NavigableMap<Integer, SynapseSlot> inputSlots = new TreeMap<>();
    protected NavigableMap<Long, SynapseSlot> outputSlots = new TreeMap<>();

    public boolean instantiationIsQueued;
    protected boolean isNewInstance;

    protected TextReference textReference;

    protected BindingSignalSlot[] bindingSignalSlots = new BindingSignalSlot[2];

    protected HashMap<Integer, Integer> templateSynIdMap;

    public Activation(ActivationDefinition t, Integer id, Document doc, Neuron n) {
        this.type = t;
        this.id = id;
        this.neuron = n;
        this.doc = doc;

        neuron.register(this);
        doc.register(this);

        initStates();

        initBindingSignalSlots();

        setCreated(doc.getCurrentTimestamp());
    }


    public NeuronType getNeuronType() {
        return neuron.getNeuronType();
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

    protected void initBindingSignalSlots() {
        Stream<BSSlotDefinition> bsSlots = neuron.getBindingSignalSlots();
        bsSlots.forEach(slotDef ->
                bindingSignalSlots[slotDef.getScope().ordinal()] = BindingSignalSlot.create(this, slotDef)
        );
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
                syn.createInputSlot(this)
        );
    }

    public SynapseSlot registerInputSlot(Synapse syn) {
        return inputSlots.computeIfAbsent(syn.getSynapseId(), nId ->
            syn.createOutputSlot(this)
        );
    }

    protected void initStates() {
        StateDefinition[] stateDefs = this.getType().getStates();
        states = new State[stateDefs.length];

        Stream.of(stateDefs)
                .forEach(sd ->
                        states[sd.getStateType().ordinal()] = sd.instantiate(this)
                );
    }

    public void propagateBindingSignal(Scope t, Activation bs, boolean state) {
        getOutputLinks()
                .forEach(l ->
                        l.propagateBindingSignal(bs, t, state)
                );
    }

    public Activation getBindingSignal(Scope t) {
        SingleBSSlot slot = (SingleBSSlot) getBindingSignalSlot(t);

        return slot != null ?
                slot.getBindingSignal() :
                null;
    }

    public boolean isNewInstance() {
        return isNewInstance;
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

    public State getState(StateType st) {
        if(st.ordinal() >= states.length)
            return states[states.length - 1];

        return states[st.ordinal()];
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
        return getFired(NON_FEEDBACK);
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

    @Override
    public boolean isNextRound() {
        return false;
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

    public Optional<Link> getInputLinkByType(Type<LinkDefinition, Link> linkType) {
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

    public SynapseSlot getInputSlotBySynapseType(Type<SynapseDefinition, Synapse> synType) {
        return getInputSlots()
                .filter(s -> synType.isInstance(s.getSynapse()))
                .findFirst()
                .orElse(null);
    }

    public  Stream<SynapseSlot> getInputSlotsByType(Type<SynapseSlotDefinition, SynapseSlot> slotType) {
        return getInputSlots()
                .filter(slotType::isInstance);
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

    public Stream<SynapseSlot> getOutputSlotsByType(Type<SynapseSlotDefinition, SynapseSlot> slotType) {
        return getOutputSlots()
                .filter(slotType::isInstance);
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

    public Stream<Link> getInputLinksByType(Type<LinkDefinition, Link> linkType) {
        return getInputLinks()
                .filter(linkType::isInstance);
    }

    public Stream<Link> getOutputLinksByType(Type<LinkDefinition, Link> linkType) {
        return getOutputLinks()
                .filter(linkType::isInstance);
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

    public boolean isActiveTemplateInstance() {
        return isNewInstance ||
                isFired(INNER_FEEDBACK);
    }

    public Activation resolveAbstractInputActivation(boolean isSynapseInstantiable) {
        return isSynapseInstantiable && neuron.isInstantiable() ?
                getActiveTemplateInstance() :
                this;
    }

    public Activation getActiveTemplateInstance() {
        return null;
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

    protected void linkTemplateAndInstance(Activation ti) {
/*        Link cil = getActiveCategoryInputLink();
        if(cil == null)
            cil = createCategoryInputLink();

        CategoryActivation cAct = (CategoryActivation) cil.getOutput();
        cAct.instantiateCategoryLink(ti);*/
    }


    public void instantiateTemplateEdges(Activation instanceAct) {
        getInputLinks()
                .filter(l -> l.getSynapse().isOutputSideInstantiable())
                .filter(l -> l.getInput() != null)
                .filter(l -> l.getInput().isFired(INNER_FEEDBACK)) // Should this condition rely on the annealing value instead of instanceof?
                .forEach(l ->
                        l.instantiateTemplate(
                                l.getInput().resolveAbstractInputActivation(
                                        l.getSynapse().isInputSideInstantiable()
                                ),
                                instanceAct
                        )
                );

        instanceAct.initFromTemplate(this);

        getOutputLinks()
                .filter(l -> l.getSynapse().isInputSideInstantiable())
//                .filter(l -> !(l instanceof CategoryLink))
                .filter(l -> l.getOutput().isFired(INNER_FEEDBACK))
                .forEach(l ->
                        l.instantiateTemplate(
                                instanceAct,
                                l.getOutput().resolveAbstractInputActivation(
                                        l.getSynapse().isOutputSideInstantiable()
                                )
                        )
                );
    }

    public void initFromTemplate(Activation templateAct) {
        setFired(INNER_FEEDBACK, templateAct.getFired(INNER_FEEDBACK));
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

    @Override
    public String dumpObject(int depth) {
        return super.dumpObject(depth) + "\n" +
                dumpStates(depth) +
                getInputLinks()
                .map(f -> f.dumpObject(depth + 2))
                .collect(Collectors.joining("\n"));
    }

    public String dumpStates(int depth) {
        return Arrays.stream(states)
                .map(f -> f.dumpObject(depth + 2))
                .collect(Collectors.joining("\n"));
    }

    public Activation setNet(StateType stateType, double net) {
        State s = getState(stateType);
        s.getField(NET).setValue(net);
        return this;
    }
}
