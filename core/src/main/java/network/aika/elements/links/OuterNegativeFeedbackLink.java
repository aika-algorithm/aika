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
import network.aika.elements.activations.OuterInhibitoryActivation;
import network.aika.fields.*;
import network.aika.elements.synapses.OuterNegativeFeedbackSynapse;
import network.aika.visitor.binding.BindingVisitor;
import network.aika.visitor.inhibitory.InhibitoryVisitor;

import java.util.stream.Stream;

import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.mul;

/**
 * @author Lukas Molzberger
 */
public class OuterNegativeFeedbackLink extends FeedbackLink<OuterNegativeFeedbackSynapse, OuterInhibitoryActivation> {

    private Multiplication innerWeightedInput;

    public OuterNegativeFeedbackLink(OuterNegativeFeedbackSynapse s, OuterInhibitoryActivation input, BindingActivation output) {
        super(s, input, output);

        if(input == null)
            return;

        OuterInhibitoryActivation.connectFields(
                input.getAllInhibitoryLinks(),
                Stream.of(this)
        );
    }

    @Override
    protected void initInputValue() {
        inputValue = new MaxField(this, "max-input-value");
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
    public void inhibVisit(InhibitoryVisitor v, int depth) {
    }

    @Override
    public void disconnect() {
        super.disconnect();

        innerWeightedInput.disconnectAndUnlinkInputs(false);
    }
}