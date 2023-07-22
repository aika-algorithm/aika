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

import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InputInhibitoryActivation;
import network.aika.fields.*;
import network.aika.elements.synapses.InputNegativeFeedbackSynapse;
import network.aika.visitor.binding.BindingVisitor;

import java.util.stream.Stream;

import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.mul;

/**
 * @author Lukas Molzberger
 */
public class InputNegativeFeedbackLink extends FeedbackLink<InputNegativeFeedbackSynapse, InputInhibitoryActivation> {

    private Field weightUpdate;

    private Multiplication innerWeightedInput;

    public InputNegativeFeedbackLink(InputNegativeFeedbackSynapse s, InputInhibitoryActivation input, BindingActivation output) {
        super(s, input, output);

        if(input == null)
            return;

        InputInhibitoryActivation.connectFields(
                input.getAllInhibitoryLinks(),
                Stream.of(this)
        );
    }

    @Override
    protected void initInputValue() {
        inputValue = new MaxField(this, "max-input-value");
    }

    @Override
    public Field getOutputNet() {
        return getOutput().getNet();
    }

    @Override
    protected void connectInputValue() {
    }

    @Override
    protected Multiplication initWeightedInput() {
        innerWeightedInput = super.initWeightedInput();
        return mul(
                this,
                "annealing * iAct(" + getInputKeyString() + ").value * weight",
                getThought().getAnnealing(),
                innerWeightedInput
        );
    }

    @Override
    public void bindingVisit(BindingVisitor v, int depth) {
        // don't allow negative feedback links to create new links; i.d. do nothing
    }

    @Override
    public void connectWeightUpdate() {
        weightUpdate = mul(
                this,
                "weight update",
                getInputIsFired(),
                getOutput().getNegUpdateValue()
        );

        linkAndConnect(
                weightUpdate,
                synapse.getWeight()
        );
    }

    @Override
    public void disconnect() {
        super.disconnect();

        innerWeightedInput.disconnectAndUnlinkInputs(false);

        if(weightUpdate != null)
            weightUpdate.disconnectAndUnlinkOutputs(false);
    }
}
