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
import network.aika.Thought;
import network.aika.elements.Element;
import network.aika.elements.LinkKey;
import network.aika.elements.Timestamp;
import network.aika.elements.links.CategoryInputLink;
import network.aika.elements.links.CategoryLink;
import network.aika.elements.links.Link;
import network.aika.ActivationFunction;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.text.TextReference;
import network.aika.text.Range;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.fields.*;
import network.aika.elements.synapses.Synapse;
import network.aika.queue.activation.Counting;
import network.aika.queue.activation.LinkingOut;
import network.aika.visitor.Visitor;

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
    protected Thought thought;

    protected Timestamp created = NOT_SET;
    protected Timestamp fired = NOT_SET;

    protected FieldOutput value;

    protected SumField net;

    protected SumField netPreAnneal;

    protected FieldFunction netOuterGradient;
    protected SumField gradient;

    protected Field updateValue;

    protected FieldOutput negUpdateValue;

    protected NavigableMap<LinkKey, Link> inputLinks;
    protected NavigableMap<LinkKey, Link> outputLinks;

    public boolean instantiationIsQueued;
    protected boolean isNewInstance;

    protected TextReference textReference;

    public Activation(int id, Thought t, N n) {
        this.id = id;
        this.neuron = n;
        this.thought = t;
        setCreated(t.getCurrentTimestamp());

        inputLinks = new TreeMap<>();
        outputLinks = new TreeMap<>();

        initNet();
        net.setQueued(thought, INFERENCE);
        netPreAnneal.setQueued(thought, PRE_ANNEAL);

        initOnFiredListener();

        value = func(
                this,
                "value = f(net)",
                TOLERANCE,
                net,
                x -> getActivationFunction().f(x)
        );

        gradient = new SumField(this, "gradient", TOLERANCE)
                .setQueued(thought, TRAINING);

        if (getModel().getConfig().isTrainingEnabled() && neuron.isTrainingAllowed()) {
            connectGradientFields();
            connectWeightUpdate();
        }

        initInactiveLinks();

        thought.register(this);
        neuron.register(this);

        thought.onElementEvent(CREATE, this);
    }

    public LinkKey getLinkKey() {
        return new LinkKey(
                neuron.getId(),
                id
        );
    }

    protected void connectWeightUpdate() {

    }

    protected void initNet() {
        net = new SumField(this, "net", null);

        linkAndConnect(getNeuron().getBias(), getDefaultNet())
                .setPropagateUpdates(false);

        initNetPreAnneal();
    }

    protected void initNetPreAnneal() {
        netPreAnneal = new SumField(this, "netPreAnneal", TOLERANCE);
        linkAndConnect(net, netPreAnneal);

        netPreAnneal.addListener(
                "disconnect listener",
                (fl, nr, u) ->
                        netPreAnneal.disconnectAndUnlinkInputs(false),
                true
        );
    }

    protected void initOnFiredListener() {
        net.addListener("onFired", (fl, nr, u) -> {
                    if(fl.getInput().exceedsThreshold() && fired == NOT_SET) {
                        fired = thought.getCurrentTimestamp();
                        LinkingOut.add(this, false);
                        Counting.add(this);
                    }
                }
        );
    }

    public Field getDefaultNet() {
        return net;
    }

    protected void initInactiveLinks() {
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

    public void bindingVisit(Visitor v, Link lastLink, int depth) {
        v.next(this, lastLink, depth);
    }

    public void patternVisit(Visitor v, Link lastLink, int depth) {
        v.next(this, lastLink, depth);
    }

    public void inhibVisit(Visitor v, Link lastLink, int depth) {
        v.next(this, lastLink, depth);
    }

    public void patternCatVisit(Visitor v, Link lastLink, int depth) {
        v.next(this, lastLink, depth);
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

    public boolean isInput() {
        return false;
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
        return isTrue(net, 0.0);
    }

    public Thought getThought() {
        return thought;
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
        thought.onElementEvent(TOKEN_POSITION, this);
    }

    protected void registerPosRange(TextReference oldTextReference, TextReference newTextReference) {
    }

    protected void propagateRanges() {
        outputLinks.values()
                .forEach(Link::propagateRanges);
    }

    public Range getAbsoluteCharRange() {
        if(textReference == null)
            return null;
        Range r = textReference.getCharRange();
        return r.getAbsoluteRange(thought.getCharRange());
    }

    @Override
    public int compareTo(Activation act) {
        return ID_COMPARATOR.compare(this, act);
    }

    public String getLabel() {
        return getNeuron().getLabel();
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

    public Link getInputLink(Activation iAct) {
        return inputLinks.get(iAct.getLinkKey());
    }

    public Link getInputDummyLink(Neuron n) {
        return inputLinks.get(new LinkKey(n.getId(), null));
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
        CategoryActivation cAct = getCategoryActivation();
        return cAct != null ? cAct.getTemplate() : null;
    }

    public CategoryActivation getCategoryActivation() {
        return getOutputLinksByType(CategoryLink.class)
                .map(l -> (CategoryActivation) l.getOutput())
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public Stream<Activation> getTemplateInstances() {
        CategoryInputLink cil = getCategoryInputLink();
        if(cil == null || cil.getInput() == null)
            return Stream.empty();

        return cil.getInput()
                .getCategoryInputs();
    }

    public boolean isActiveTemplateInstance() {
        return isNewInstance || isTrue(net, 0.5);
    }

    public CategoryInputLink getCategoryInputLink() {
        return getInputLinksByType(CategoryInputLink.class)
                .findFirst()
                .orElse(null);
    }

    public Activation getActiveTemplateInstance() {
        return getInputLinksByType(CategoryInputLink.class)
                .map(CategoryInputLink::getInput)
                .filter(Objects::nonNull)
                .findFirst()
                .map(CategoryActivation::getActiveTemplateInstance)
                .orElse(null);
    }

    public Activation<N> resolveAbstractInputActivation() {
        return isInstantiable() ?
                getActiveTemplateInstance() :
                this;
    }

    public boolean isInstantiable() {
        return !neuron.isTemplateOnly() && neuron.isAbstract();
    }

    public Activation<N> instantiateTemplateNode() {
        if(neuron.isTemplateOnly())
            return null;

        N n = (N) neuron.instantiateTemplate();

        Activation<N> ti = n.createActivation(getThought());

        ti.textReference = textReference;
        ti.isNewInstance = true;
        ti.fired = fired;

        thought.onElementEvent(TOKEN_POSITION, ti);

        linkTemplateAndInstance(ti);

        instantiateTemplateEdges(ti);

        if(thought.getInstantiationCallback() != null)
            thought.getInstantiationCallback().onInstantiation(this, ti);

        return ti;
    }

    private void linkTemplateAndInstance(Activation<N> ti) {
        CategoryInputLink cl = getCategoryInputLink();
        if(cl == null)
            cl = createCategoryInputLink();

        cl.instantiateTemplate(cl.getInput(), ti, (Link) cl);
    }

    private CategoryInputLink createCategoryInputLink() {
        CategoryInputSynapse catSyn = getNeuron().getCategoryInputSynapse();
        if(catSyn == null)
            return null;

        CategoryActivation catAct = catSyn.getInput().createActivation(thought);

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

        instanceAct.initFromTemplate();

        getOutputLinks()
                .filter(l -> !l.getOutput().isInstantiable())
                .forEach(l ->
                        l.instantiateTemplate(
                                instanceAct,
                                l.getOutput().resolveAbstractInputActivation()
                        )
                );
    }

    public void initFromTemplate() {
        fired = getTemplate().fired;
        thought.onElementEvent(UPDATE, this);
    }

    public String toString() {
        return getClass().getSimpleName() + " " + toKeyString();
    }

    public String toKeyString() {
        return "id:" + getId() + " n:[" + getNeuron().toKeyString() + "]";
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
}
