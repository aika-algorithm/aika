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

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.bsslots.BindingSignalSlot;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.slots.SynapseInputSlot;
import network.aika.elements.synapses.slots.SynapseOutputSlot;
import network.aika.enums.Scope;
import network.aika.fields.*;
import network.aika.fields.link.FieldLink;
import network.aika.visitor.Visitor;

import static network.aika.enums.Scope.SAME;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.fields.link.AbstractFieldLink.updateConnected;
import static network.aika.fields.link.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.*;
import static network.aika.visitor.operator.BindingSignalCollector.retrieveBindingSignal;
import static network.aika.visitor.operator.SubsumesOperator.subsumes;


/**
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveLink<S extends ConjunctiveSynapse, IA extends Activation<?>, OA extends ConjunctiveActivation<?>>
        extends Link<S, IA, OA, SynapseInputSlot, SynapseOutputSlot> {

    protected FieldLink inputSlotFL;

    private FieldOutput weightUpdatePosCase;
    private FieldOutput weightUpdateNegCase;
    private FieldOutput biasUpdateNegCase;


    public ConjunctiveLink(S s, IA input, OA output) {
        super(s, input, output);
    }

    @Override
    public void onOutputBindingSignalChange(Scope bsType, PatternActivation nBS, boolean state) {
        if(input == null)
            return;

        Scope inputBSType = INPUT.transition(bsType, synapse.getTransition());
        if(inputBSType == null)
            return;

        PatternActivation bs = retrieveBindingSignal(input, inputBSType);

        if(bs == null)
            return;

        updateConnected(
                inputSlotFL,
                state && (
                        subsumes(SAME, nBS, bs) || subsumes(SAME, bs, nBS)
                ),
                true
        );
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
    public void visit(Visitor v, Scope s, int depth) {
       if(input == null)
            return;

        if(isActive() || output.getBindingSignalSlot(s).isFeedback())
            v.next(this, s, depth);
    }

    @Override
    protected void initWeightInput() {
        super.initWeightInput();

        if (synInputSlot != null)
            inputSlotFL = linkAndConnect(
                    synOutputSlot.getOutputNet(),
                    this,
                    synInputSlot.getInputField()
            );

        if(synapse.isOptional())
            synapse.initBiasInput(output);
    }

    public void initFromTemplate(Link template) {
        super.initFromTemplate(template);

        synapse.initBiasInput(output);
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
