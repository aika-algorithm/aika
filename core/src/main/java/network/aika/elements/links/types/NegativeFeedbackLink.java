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
package network.aika.elements.links.types;

import network.aika.elements.Type;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.InhibitoryActivation;
import network.aika.elements.links.Link;
import network.aika.enums.Scope;
import network.aika.fields.*;
import network.aika.elements.synapses.types.NegativeFeedbackSynapse;
import network.aika.visitor.Visitor;

import java.util.stream.Stream;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.INHIBITORY;
import static network.aika.fields.Fields.mul;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class NegativeFeedbackLink extends Link<NegativeFeedbackSynapse, InhibitoryActivation, BindingActivation> {

    private Multiplication innerWeightedInput;

    public NegativeFeedbackLink(NegativeFeedbackSynapse s, InhibitoryActivation input, BindingActivation output) {
        super(s, input, output);

        if(input == null)
            return;

        InhibitoryActivation.connectFields(
                input.getAllInhibitoryLinks(),
                Stream.of(this)
        );
    }

    @Override
    public void propagateRanges() {
    }

    @Override
    public Type getInputType() {
        return INHIBITORY;
    }

    @Override
    public Type getOutputType() {
        return BINDING;
    }

    @Override
    protected void initInputValue() {
        inputValue = new MaxField(this, "max-input-value", TOLERANCE);
    }

    @Override
    protected void connectInputValue() {
    }

    @Override
    protected void initWeightedInput() {
        super.initWeightedInput();
        innerWeightedInput = weightedInput;
        weightedInput = mul(
                this,
                "annealing * iAct(" + getInputKeyString() + ").value * weight",
                getDocument().getAnnealing(),
                innerWeightedInput
        );
    }

    @Override
    public void connectWeightUpdate() {
        // TODO!
    }

    @Override
    public void visit(Visitor v, Scope s, int depth) {
    }

    @Override
    public void disconnect() {
        super.disconnect();

        innerWeightedInput.disconnectAndUnlinkInputs(false);
    }
}
