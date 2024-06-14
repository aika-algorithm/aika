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
package network.aika.elements.links;

import network.aika.Model;
import network.aika.Document;
import network.aika.elements.Element;
import network.aika.elements.ModelProvider;
import network.aika.elements.NeuronType;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.bsslots.BindingSignalSlot;
import network.aika.elements.activations.StateType;
import network.aika.elements.activations.bsslots.RegisterInputSlot;
import network.aika.elements.activations.bsslots.SingleBSSlot;
import network.aika.elements.relations.Relation;
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.elements.typedef.LinkTypeDefinition;
import network.aika.elements.typedef.Type;
import network.aika.enums.Scope;
import network.aika.enums.direction.Direction;
import network.aika.elements.synapses.Synapse;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import network.aika.queue.Timestamp;
import network.aika.queue.steps.LinkUpdate;
import network.aika.visitor.Visitor;

import java.util.stream.Stream;

import static network.aika.debugger.EventType.CREATE;
import static network.aika.elements.links.BSLinkEvent.ON_CREATE;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Link extends Type<LinkTypeDefinition, Link> implements Element, ModelProvider, QueueProvider {

    protected Synapse synapse;

    protected Activation input;
    protected Activation output;

    protected LinkUpdate inputStep = new LinkUpdate(this, INPUT);

    protected LinkUpdate outputStep = new LinkUpdate(this, OUTPUT);

    protected SynapseSlot synInputSlot;

    protected SynapseSlot synOutputSlot;


    public Link(Synapse s, Activation input, Activation output) {
        this.synapse = s;
        this.input = input;
        this.output = output;

        link();

        propagateRanges();
        getDocument().onElementEvent(CREATE, this);
    }

    public LinkUpdate getLinkUpdateStep(Direction dir) {
        return dir == INPUT ?
                inputStep :
                outputStep;
    }

    public void setState(Direction dir, boolean state) {
        getLinkUpdateStep(dir)
                .setTargetState(state);
    }

    public boolean isActive() {
        return isInputSideActive() && isOutputSideActive();
    }

    public boolean isInputSideActive() {
        return true;
    }

    public boolean isOutputSideActive() {
        return true;
    }

    public NeuronType getInputType() {
        return synapse.getInputType();
    }

    public NeuronType getOutputType() {
        return synapse.getOutputType();
    }

    public void onOutputBindingSignalUpdate(Scope bsType, Activation nBS, boolean state) {
    }

    public void visit(Visitor v, Scope s, int depth) {
        v.next(this, s, depth);
    }


    public void instantiateTemplate(Activation iAct, Activation oAct) {
        if(iAct == null || oAct == null)
            return;

        Synapse s = synapse.instantiateTemplate(
                iAct.getNeuron(),
                oAct.getNeuron()
        );

        Link l = oAct.getInputLink(iAct, s.getSynapseId());
        if(l != null)
            return;

        s.createLinkFromTemplate(iAct, oAct, this);
    }

    public abstract void connectWeightUpdate();

    public StateType inputState() {
        return getSynapse().getTypeDefinition().getTrigger().getType();
    }

    public StateType outputState() {
        return getSynapse().getTypeDefinition().outputState();
    }

    public void init() {
    }

    public void initFromTemplate(Link template) {
        output.registerTemplateInstanceSynapse(
                template.synapse.getSynapseId(),
                synapse.getSynapseId()
        );

        linkRelationFromTemplate(template);
    }

    protected void linkRelationFromTemplate(Link template) {
        Relation rel = synapse.getRelation();
        if(rel != null)
            rel.linkRelationFromTemplate(output, synapse, template);
    }

    public void link() {
        if(input != null)
            linkInput();

        if(output != null)
            linkOutput();
    }

    public void updateBindingSignals(BSLinkEvent e, boolean state) {
        if(input == null)
            return;

        input.getBindingSignalSlots()
                .filter(SingleBSSlot.class::isInstance)
                        .forEach(iBSSlot ->
                                propagateBindingSignal(
                                        e,
                                        (SingleBSSlot) iBSSlot,
                                        state
                                )
        );
    }

    private void propagateBindingSignal(BSLinkEvent e, SingleBSSlot iBSSlot, boolean state) {
        Stream<BindingSignalSlot> slots = synapse.transitionBindingSignal(output, iBSSlot.getType());

        slots
                .filter(oBSSlot -> oBSSlot.isFeedback() == e.isFeedback())
                .forEach(oBSSlot ->
                        oBSSlot.updateBindingSignal(
                                iBSSlot.getBindingSignal(),
                                state
                        )
                );
    }

    public void propagateBindingSignal(Activation bs, Scope is, boolean state) {
        Stream<BindingSignalSlot> slots = synapse.transitionBindingSignal(output, is);

        if(!isActive())
            slots = slots.filter(BindingSignalSlot::isFeedback);

        slots.forEach(oBSSlot ->
                oBSSlot.updateBindingSignal(bs, state)
        );
    }

    @Override
    public Timestamp getFired() {
        return input != null && isCausal() ?
                input.getFired() :
                output.getFired();
    }

    @Override
    public Timestamp getCreated() {
        return input != null && isCausal() ? input.getCreated() : output.getCreated();
    }

    public Synapse getSynapse() {
        return synapse;
    }

    public void setSynapse(Synapse synapse) {
        this.synapse = synapse;
    }

    public Activation getInput() {
        return input;
    }

    public Activation getOutput() {
        return output;
    }

    public boolean isCausal() {
        return input == null || isCausal(input, output);
    }

    public static boolean isCausal(Activation iAct, Activation oAct) {
        return iAct.getFired().compareTo(oAct.getFired()) < 0;
    }

    public void linkInput() {
        synInputSlot = input.registerOutputSlot(synapse);
        synInputSlot.addLink(this);
        updateBindingSignals(ON_CREATE, true);
    }

    public void linkOutput() {
        if(synapse.getTypeDefinition().getRegisterInputSlot() == RegisterInputSlot.ON_LINKING)
            synOutputSlot = output.registerInputSlot(synapse);

        synOutputSlot.addLink(this);
    }

    public SynapseSlot getSynInputSlot() {
        return synInputSlot;
    }

    public SynapseSlot getSynOutputSlot() {
        return synOutputSlot;
    }

    public void propagateRanges() {
        if(!synapse.isPropagateRange())
            return;

        if(input == null)
            return;

        if(input.getTextReference() != null)
            output.updateRanges(
                    input.getTextReference()
            );
    }

    public void checkPrimarySuppression() {
    }

    public Document getDocument() {
        return output.getDocument();
    }

    @Override
    public Queue getQueue() {
        return output.getDocument();
    }

    @Override
    public Model getModel() {
        return output.getModel();
    }

    protected String getInputKeyString() {
        return (input != null ? input.toKeyString() : "id:X n:[" + synapse.getInput() + "]");
    }

    protected String getOutputKeyString() {
        return (output != null ? output.toKeyString() : "id:X n:[" + synapse.getOutput() + "]");
    }

    public String toString() {
        return getClass().getSimpleName() +
                " in:[" + getInputKeyString() + "] " +
                "--> " +
                "out:[" + getOutputKeyString() + "]";
    }
}
