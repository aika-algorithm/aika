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

import network.aika.debugger.EventType;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingSignalSlot;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.enums.direction.Direction;
import network.aika.fields.*;
import network.aika.visitor.Visitor;

import static network.aika.fields.AbstractFieldLink.updateConnected;
import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.*;


/**
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveLink<S extends ConjunctiveSynapse, IA extends Activation<?>, OA extends ConjunctiveActivation<?>>
        extends Link<S, IA, OA> {

    protected SynapseInputSlot synInputSlot;
    protected FieldLink inputSlotFL;
    protected SynapseOutputSlot synOutputSlot;

    private FieldOutput weightUpdatePosCase;
    private FieldOutput weightUpdateNegCase;
    private FieldOutput biasUpdateNegCase;


    public ConjunctiveLink(S s, IA input, OA output) {
        super(s, input, output);
    }

    @Override
    public void link() {
        super.link();

        if (input != null) {
            synInputSlot = input.registerOutputSlot(this);

            output.registerBindingSignalSlot(this);
        }

        synOutputSlot = output.registerInputSlot(synapse);

        BindingSignalSlot slot = output.getBSSlot(synapse.getTransition());
        if(slot != null)
            slot.addListener((t, oBS, nBS, state) -> {
                PatternActivation bs = retrieveBindingSignals().get(t);
                updateConnected(inputSlotFL, state && nBS == bs, true);
            });
    }

    @Override
    protected void checkConnectInputValueLink() {
        if (input == null)
            super.checkConnectInputValueLink();
    }

    public SynapseInputSlot getSynInputSlot() {
        return synInputSlot;
    }

    public SynapseOutputSlot getSynOutputSlot() {
        return synOutputSlot;
    }

    public void updateLinkState(Direction dir, boolean state) {
        if(dir == Direction.INPUT) {
            updateConnected(getInputValueLink(), state, true);
            //updateConnected(outputSlotFL, state, true);

            retrieveAndConnectBindingSignals(output, state);
        } else {
          //  updateConnected(inputSlotFL, state, true);
        }

        boolean oppositeState = dir == Direction.INPUT ?
                isOutputSideActive() :
                isInputSideActive();

        if(state == oppositeState)
            getDocument().onElementEvent(EventType.UPDATE, this);
    }

    public boolean isInputSideActive() {
        if(input == null)
            return true;

        return synInputSlot != null && synInputSlot.getSelectedLink() == this;
    }

    public boolean isOutputSideActive() {
        return synOutputSlot != null && synOutputSlot.getSelectedLink() == this;
    }

    @Override
    public void visit(Visitor v, int state, int depth) {
       if(input == null)
            return;

        if(isActive())
            v.next(this, state, depth);
    }

    @Override
    protected void initWeightInput() {
        super.initWeightInput();

        if (synInputSlot != null)
            inputSlotFL = linkAndConnect(output.getNet(), synInputSlot);

        if(synapse.isOptional())
            synapse.initBiasInput(output);
    }

    public void initFromTemplate(Link template) {
        super.initFromTemplate(template);
        synapse.initBiasInput(output);
    }

    protected void initWeightedOutput() {
        linkAndConnect(weightedInput, synOutputSlot);
    }

    @Override
    public void connectWeightUpdate() {
        weightUpdatePosCase = mul(
                this,
                "weight update (pos case)",
                getInputValue(),
                getOutput().getUpdateValue(),
                synapse.getWeight()
        );

        weightUpdateNegCase = scale(
                this,
                "weight update (neg case)",
                -1.0,
                mul(
                        this,
                        "weight update (neg case)",
                        getNegInputIsFired(),
                        getOutput().getNegUpdateValue()
                ),
                synapse.getWeight()
        );

        biasUpdateNegCase = mul(
                this,
                "bias update (neg case)",
                getNegInputIsFired(),
                getOutput().getNegUpdateValue(),
                getSynapse().getSynapseBias()
        );
    }

    @Override
    public void disconnect() {
        super.disconnect();

        if(weightUpdatePosCase != null)
            weightUpdatePosCase.disconnectAndUnlinkOutputs(false);
        if(weightUpdateNegCase != null)
            weightUpdateNegCase.disconnectAndUnlinkOutputs(false);
        if(biasUpdateNegCase != null)
            biasUpdateNegCase.disconnectAndUnlinkOutputs(false);
    }

    public FieldOutput getWeightUpdatePosCase() {
        return weightUpdatePosCase;
    }

    public FieldOutput getWeightUpdateNegCase() {
        return weightUpdateNegCase;
    }

    public FieldOutput getBiasUpdateNegCase() {
        return biasUpdateNegCase;
    }
}
