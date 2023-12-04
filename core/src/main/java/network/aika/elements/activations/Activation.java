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
import network.aika.elements.LinkKey;
import network.aika.elements.Timestamp;
import network.aika.elements.Type;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.CategoryInputLink;
import network.aika.elements.links.CategoryLink;
import network.aika.elements.links.Link;
import network.aika.ActivationFunction;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.enums.Scope;
import network.aika.queue.steps.Counting;
import network.aika.text.TextReference;
import network.aika.text.Range;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.fields.*;
import network.aika.elements.synapses.Synapse;

import java.util.*;
import java.util.stream.Stream;

import static network.aika.debugger.EventType.*;
import static network.aika.elements.LinkKey.getFromLinkKey;
import static network.aika.elements.LinkKey.getToLinkKey;
import static network.aika.elements.Timestamp.NOT_SET;
import static network.aika.queue.Phase.PRE_ANNEAL;
import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.*;
import static network.aika.queue.Phase.*;
import static network.aika.text.TextReference.join;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public abstract class Activation<N extends Neuron> implements Element, Comparable<Activation> {

    public static final Comparator<Activation> ID_COMPARATOR = Comparator.comparingInt(Activation::getId);

    protected final int id;
    protected N neuron;
    protected Document doc;

    protected Timestamp created = NOT_SET;
    protected Timestamp fired = NOT_SET;

    protected FieldFunction value;

    protected SumField net;

    protected SumField netPreAnneal;

    protected FieldFunction netOuterGradient;
    protected SumField gradient;

    protected Field updateValue;

    protected FieldOutput negUpdateValue;

    protected NavigableMap<LinkKey, Link> inputLinks;
    protected NavigableMap<LinkKey, Link> outputLinks;

    protected NavigableMap<Long, SynapseInputSlot> outputSlots;

    public boolean instantiationIsQueued;
    protected boolean isNewInstance;

    protected TextReference textReference;

    protected BindingSignalSlot[] bindingSignalSlots = new BindingSignalSlot[3];


    public Activation(int id, Document doc, N n) {
        this.id = id;
        this.neuron = n;
        this.doc = doc;
        setCreated(doc.getCurrentTimestamp());

        inputLinks = new TreeMap<>();
        outputLinks = new TreeMap<>();

        initNet();

        initValue();

        netPreAnneal.setQueued(doc, PRE_ANNEAL);

        initBindingSignalSlots();

        gradient = new SumField(this, "gradient", TOLERANCE)
                .setQueued(doc, TRAINING);

        if (getModel().getConfig().isTrainingEnabled() && neuron.isTrainingAllowed()) {
            connectGradientFields();
            connectWeightUpdate();
        }

        initInactiveLinks();

        doc.register(this);
        neuron.register(this);

        doc.onElementEvent(CREATE, this);
    }

    public Type getType() {
        return neuron.getType();
    }


    protected void connectWeightUpdate() {
    }

    public BindingSignalSlot getBindingSignalSlot(Scope t) {
        return bindingSignalSlots[t.ordinal()];
    }

    public Stream<BindingSignalSlot> getBindingSignalSlots() {
        return Arrays.stream(bindingSignalSlots)
                .filter(Objects::nonNull);
    }

    public SynapseInputSlot registerOutputSlot(Synapse syn) {
        if(outputSlots == null)
            outputSlots = new TreeMap<>();

        return outputSlots.computeIfAbsent(syn.getOutput().getId(), nId ->
                new SynapseInputSlot(syn, "in-slot-" + nId, TOLERANCE)
        );
    }

    protected void initNet() {
        net = new SumField(this, "net", null);

        linkAndConnect(getNeuron().getBias(), net)
                .setPropagateUpdates(false);

        initNetPreAnneal();
    }

    protected void initValue() {
        value = func(
                this,
                "value = f(net)",
                TOLERANCE,
                net,
                x -> getActivationFunction().f(x)
        );
        value.setQueued(doc, INFERENCE);
    }

    protected void initNetPreAnneal() {
        netPreAnneal = new SumField(this, "netPreAnneal", TOLERANCE);
        linkAndConnect(net, netPreAnneal);

        netPreAnneal.addListener(
                "disconnect listener",
                (fl, u) ->
                        netPreAnneal.disconnectAndUnlinkInputs(false),
                true
        );
    }

    protected void initBindingSignalSlots() {
        Stream<Scope> bsSlots = neuron.getBindingSignalSlots();
        bsSlots.forEach(bsSlot ->
                bindingSignalSlots[bsSlot.ordinal()] = new BindingSignalSlot(this, bsSlot, isFeedback(bsSlot))
        );

        value.addListener("onFired", (fl, u) -> {
                    if(fl.getInput().exceedsThreshold() && fired == NOT_SET) {
                        fired = doc.getCurrentTimestamp();

                        getBindingSignalSlots()
                                .forEach(BindingSignalSlot::onFired);

                        Counting.add(this);
                    }
                }
        );
    }

    protected boolean isFeedback(Scope bsSlot) {
        return false;
    }

    public void propagateBindingSignal(Scope t, PatternActivation bs, boolean state) {
        getOutputLinks()
                .forEach(l ->
                        l.propagateBindingSignal(bs, t, state)
                );
    }

    protected void initInactiveLinks() {
    }

    public PatternActivation getBindingSignal(Scope t) {
        BindingSignalSlot slot = getBindingSignalSlot(t);
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

    public void setNet(double v) {
        net.setValue(v);
    }

    protected void connectGradientFields() {
        netOuterGradient =
                func(
                        this,
                        "f'(netPreAnneal)",
                        TOLERANCE,
                        netPreAnneal,
                        x -> getNeuron().getActivationFunction().outerGrad(x)
        );
    }

    public FieldFunction getNetOuterGradient() {
        return netOuterGradient;
    }

    public SumField getGradient() {
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

    public FieldOutput getValue() {
        return value;
    }

    public FieldOutput getNextRoundValue() {
        return value;
    }

    public SumField getNet() {
        return net;
    }

    public SumField getNetPreAnneal() {
        return netPreAnneal;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp ts) {
        this.created = ts;
    }

    public Timestamp getFired() {
        return fired;
    }

    public boolean isFired() {
        return isTrue(value);
    }

    public Document getDocument() {
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
        outputLinks.values()
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

    public N getNeuron() {
        return neuron;
    }

    public void setNeuron(N n) {
        this.neuron = n;
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

    public Link getInputLink(Activation iAct, int synapseId) {
        return inputLinks.get(new LinkKey(iAct, synapseId));
    }

    public Link getInputLink(Activation iAct, Synapse syn) {
        return inputLinks.get(
                new LinkKey(
                        syn.getInput().getId(),
                        iAct != null ? iAct.getId() : null,
                        syn.getSynapseId())
        );
    }

    public <IL extends Link> Optional<IL> getInputLinkByType(Class<IL> linkType) {
        return getInputLinksByType(linkType)
                .findAny();
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

    public Stream<Link> getInputLinks(Neuron n) {
        return inputLinks
                .subMap(
                        getFromLinkKey(n.getId()),
                        true,
                        getToLinkKey(n.getId()),
                        true
                ).values()
                .stream();
    }

    public Stream<Link> getOutputLinks(Neuron n) {
        return outputLinks
                .subMap(
                        getFromLinkKey(n.getId()),
                        true,
                        getToLinkKey(n.getId()),
                        true
                ).values()
                .stream();
    }

    public Stream<Link> getInputLinks(Synapse s) {
        return getInputLinks(s.getInput())
                .filter(l -> l.getSynapse() == s);
    }

    public Stream<Link> getOutputLinks(Synapse s) {
        return getOutputLinks(s.getOutput())
                .filter(l -> l.getSynapse() == s);
    }

    public void linkInputs() {
        inputLinks
                .values()
                .forEach(Link::linkInput);
    }

    public void linkOutputs() {
        outputLinks
                .values()
                .forEach(Link::linkOutput);
    }

    public void linkOutputLink(Link l) {
        Link el = outputLinks.put(l.getOutputLinkKey(), l);
        assert el == null;
    }

    public void linkInputLink(Link l) {
        Link el = inputLinks.put(l.getInputLinkKey(), l);
        assert el == null;
    }

    public void link() {
        linkInputs();
        linkOutputs();
    }

    @Override
    public void disconnect() {
        net.disconnectAndUnlinkInputs(false);

        if(updateValue != null)
            updateValue.disconnectAndUnlinkOutputs(false);

        if(negUpdateValue != null)
            negUpdateValue.disconnectAndUnlinkOutputs(false);

        getInputLinks().forEach(l ->
                l.disconnect()
        );
    }

    public Stream<Link> getInputLinks() {
        return new ArrayList<>(inputLinks.values())
                .stream();
    }

    public Stream<Link> getOutputLinks() {
        return new ArrayList<>(outputLinks.values())
                .stream();
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
        CategoryInputLink cil = getActiveCategoryInputLink();
        if(cil == null || cil.getInput() == null)
            return Stream.empty();

        return cil.getInput()
                .getCategoryInputs();
    }

    public boolean isActiveTemplateInstance() {
        return isNewInstance ||
                isTrue(value);
    }

    public CategoryInputLink getActiveCategoryInputLink() {
        return getInputLinksByType(CategoryInputLink.class)
                .filter(l -> l.getInput() != null)
                .findFirst()
                .orElse(null);
    }

    public Activation<N> resolveAbstractInputActivation() {
        return isInstantiable() ?
                getActiveTemplateInstance() :
                this;
    }

    public Activation getActiveTemplateInstance() {
        CategoryInputLink l = getActiveCategoryInputLink();
        return l != null ?
                l.getInput().getActiveTemplateInstance() :
                null;
    }

    public boolean isInstantiable() {
        return !neuron.isTemplateOnly() && neuron.isAbstract();
    }

    public Activation<N> instantiateTemplateNode() {
        if(neuron.isTemplateOnly())
            return null;

        N n = (N) neuron.instantiateTemplate();

        Activation<N> ti = n.createActivation(getDocument());

        ti.textReference = textReference;
        ti.isNewInstance = true;
        ti.fired = fired;

        doc.onElementEvent(TOKEN_POSITION, ti);

        linkTemplateAndInstance(ti);
        instantiateTemplateEdges(ti);

        if(doc.getInstantiationCallback() != null)
            doc.getInstantiationCallback().onInstantiation(this, ti);

        return ti;
    }

    private void linkTemplateAndInstance(Activation<N> ti) {
        CategoryInputLink cl = getActiveCategoryInputLink();
        if(cl == null)
            cl = createCategoryInputLink();

        cl.instantiateTemplate(cl.getInput(), ti, (Link) cl);
    }

    private CategoryInputLink createCategoryInputLink() {
        CategoryInputSynapse catSyn = getNeuron().getCategoryInputSynapse();
        if(catSyn == null)
            return null;

        CategoryActivation catAct = catSyn.getInput().createActivation(doc);

        Synapse s = ((Synapse)catSyn);
        return (CategoryInputLink) s.createAndInitLink(catAct, this);
    }

    public void instantiateTemplateEdges(Activation<N> instanceAct) {
        getInputLinks()
                .filter(l -> l.getInput() != null)
                .forEach(l ->
                        l.instantiateTemplate(
                                l.getInput().resolveAbstractInputActivation(),
                                instanceAct
                        )
                );

        instanceAct.initFromTemplate(this);

        getOutputLinks()
                .filter(l -> !(l instanceof CategoryLink))

                .filter(l -> l.getOutput().isFired())
                .forEach(l ->
                        l.instantiateTemplate(
                                instanceAct,
                                l.getOutput().resolveAbstractInputActivation()
                        )
                );
    }

    public void initFromTemplate(Activation templateAct) {
        fired = templateAct.fired;
        doc.onElementEvent(UPDATE, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activation)) return false;
        Activation<?> that = (Activation<?>) o;
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
