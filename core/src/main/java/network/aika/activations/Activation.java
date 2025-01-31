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
package network.aika.activations;

import network.aika.Model;
import network.aika.Document;
import network.aika.Element;
import network.aika.ModelProvider;
import network.aika.bindingsignal.BindingSignal;
import network.aika.neurons.Synapse;
import network.aika.bindingsignal.BSType;
import network.aika.neurons.Neuron;
import network.aika.type.Obj;
import network.aika.type.relations.Relation;
import network.aika.typedefs.*;
import network.aika.fields.field.FieldOutput;
import network.aika.type.ObjImpl;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import network.aika.queue.Timestamp;
import network.aika.queue.steps.Fired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static network.aika.misc.direction.Direction.INPUT;
import static network.aika.misc.direction.Direction.OUTPUT;
import static network.aika.queue.Timestamp.NOT_SET;
import static network.aika.typedefs.ActivationDefinition.NEURON;
import static network.aika.typedefs.ActivationDefinition.SELF;

/**
 * @author Lukas Molzberger
 */
public abstract class Activation extends ObjImpl implements Element, ModelProvider, QueueProvider, Comparable<Activation> {

    public static final Comparator<Activation> ID_COMPARATOR = Comparator.comparingInt(Activation::getId);

    protected final Integer id;

    protected Neuron neuron;
    protected Document doc;

    protected Map<BSType, BindingSignal> bindingSignals;

    protected Activation parent;

    protected Timestamp created = NOT_SET;
    protected Timestamp fired = NOT_SET;
    protected Fired firedStep = new Fired(this);

    protected NavigableMap<Integer, Link> outputLinks = new TreeMap<>();

    public Activation(
            ActivationDefinition t,
            Activation parent,
            Integer id,
            Neuron n,
            Document doc,
            Map<BSType, BindingSignal> bindingSignals
    ) {
        super(t);
        this.parent = parent;
        this.id = id;
        this.neuron = n;
        this.doc = doc;
        this.bindingSignals = bindingSignals;

        doc.addActivation(this);
        neuron.updateLastUsed(doc.getId());

        setCreated(doc.getCurrentTimestamp());
    }

    @Override
    public Stream<Obj> followManyRelation(Relation rel) {
        if(rel == ActivationDefinition.INPUT)
            return getInputLinks().map(o -> o);
        else if(rel == OUTPUT)
            return getOutputLinks().map(o -> o);
        else
            throw new RuntimeException("Invalid Relation");
    }

    @Override
    public Obj followSingleRelation(Relation rel) {
        if(rel == SELF)
            return this;
        else if(rel == NEURON)
            return neuron;
        else
            throw new RuntimeException("Invalid Relation");
    }

    public ActivationKey getKey() {
        return new ActivationKey(neuron.getId(), id);
    }

    public Activation getParent() {
        return parent;
    }

    public void addOutputLink(Link l) {
        Activation oAct = l.getOutput();
        assert outputLinks.get(oAct.getId()) == null;
        outputLinks.put(oAct.getId(), l);
    }

    public abstract void addInputLink(Link l);

    public BindingSignal getBindingSignal(BSType s) {
        return bindingSignals.get(s);
    }

    public Map<BSType, BindingSignal> getBindingSignals() {
        return bindingSignals;
    }

    public boolean hasConflictingBindingSignals(Map<BSType, BindingSignal> targetBindingSignals) {
        return targetBindingSignals
                .entrySet()
                .stream()
                .anyMatch(e ->
                    isConflictingBindingSignal(e.getKey(), e.getValue())
                );
    }

    public boolean isConflictingBindingSignal(BSType s, BindingSignal targetBS) {
        BindingSignal bs = bindingSignals.get(s);
        return bs != null && targetBS != bs;
    }

    public boolean hasNewBindingSignals(Map<BSType, BindingSignal> targetBindingSignals) {
        return !bindingSignals.keySet()
                .containsAll(targetBindingSignals.keySet());
    }

    public Activation branch(Map<BSType, BindingSignal> bindingSignals) {
        // TODO: Check: Is it necessary to remove the parents binding-signals beforehand?
        bindingSignals = new HashMap<>(bindingSignals);
        getBindingSignals()
                .keySet()
                .forEach(bindingSignals::remove);

        return neuron.createActivation(
                this,
                getDocument(),
                bindingSignals
        );
    }

    public void linkOutgoing() {
        neuron.wakeupPropagable();

        neuron.getOutputSynapses()
                .stream()
                .filter(s ->
                        ((SynapseDefinition)s.getType()).isOutgoingLinkingCandidate(getBindingSignals().keySet())
                )
                .forEach(this::linkOutgoing);
    }

    void linkOutgoing(Synapse targetSyn) {
        Set<Activation> targets = collectLinkingTargets(
                targetSyn.getOutput(getModel())
        );

        targets.forEach(targetAct ->
                targetSyn.createLink(
                        this,
                        targetAct
                )
        );

        if(targets.isEmpty() && targetSyn.isPropagable())
            propagate(targetSyn);
    }

    public void propagate(Synapse targetSyn) {
        Map<BSType, BindingSignal> bindingSignals = targetSyn.transitionForward(getBindingSignals());
        Activation oAct = targetSyn.getOutput(getModel()).createActivation(null, getDocument(), bindingSignals);

        targetSyn.createLink(this, bindingSignals, oAct);

        oAct.linkIncoming(this);
    }

    public abstract void linkIncoming(Activation excludedInputAct);

    Set<Activation> collectLinkingTargets(Neuron n) {
        return getBindingSignals()
                .values()
                .stream()
                .flatMap(bs -> bs.getActivations(n))
                .collect(Collectors.toSet());
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

    @Override
    public Timestamp getFired() {
        return fired;
    }

    public void setFired() {
        fired = doc.getCurrentTimestamp();
    }

    public void setFired(Timestamp f) {
        fired = f;
    }

    public void updateFiredStep(FieldOutput net) {
        if(!net.exceedsThreshold() || fired != NOT_SET)
            return;

        if(firedStep.isQueued())
            doc.removeStep(firedStep);

        firedStep.updateNet(net.getUpdatedValue());
        doc.addStep(firedStep);
    }

    @Override
    public Queue getQueue() {
        return doc;
    }

    public Neuron getNeuron() {
        return neuron;
    }

    public Document getDocument() {
        return doc;
    }

    @Override
    public Model getModel() {
        return getNeuron().getModel();
    }

    public Link getCorrespondingInputLink(Link l) {
        return null;
    }

    public Link getCorrespondingOutputLink(Link l) {
        return null;
    }

    public Stream<Link> getInputLinks(LinkDefinition linkDefinition) {
        return getInputLinks();
    }

    public abstract Stream<Link> getInputLinks();

    public Stream<Link> getOutputLinks(LinkDefinition linkDefinition) {
        return getOutputLinks();
    }

    public Stream<Link> getOutputLinks() {
        return outputLinks.values()
                .stream();
    }

    public Link getOutputLink(Neuron n) {
        return outputLinks.get(n.getId());
    }

    public Stream<Link> getOutputLinks(Synapse s) {
        return getOutputLinks()
                .filter(l -> l.getSynapse() == s);
    }

    @Override
    public int compareTo(Activation act) {
        return ID_COMPARATOR.compare(this, act);
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
        return type.getName() + " " + toKeyString();
    }

    @Override
    public String toKeyString() {
        return "id:" + getId() + " n:[" + getNeuron().toKeyString() + "]";
    }
}
