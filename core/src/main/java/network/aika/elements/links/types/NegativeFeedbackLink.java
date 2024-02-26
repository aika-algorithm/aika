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

import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.InhibitoryActivation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.enums.Scope;
import network.aika.fields.*;
import network.aika.elements.synapses.types.NegativeFeedbackSynapse;
import network.aika.visitor.Visitor;

import java.util.stream.Stream;

import static network.aika.fields.Fields.*;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class NegativeFeedbackLink extends ConjunctiveLink<NegativeFeedbackSynapse, InhibitoryActivation, BindingActivation> {

    public NegativeFeedbackLink(NegativeFeedbackSynapse s, InhibitoryActivation input, BindingActivation output) {
        super(s, input, output);
    }

    @Override
    protected void initInputValue() {
        inputValue = new MaxField(this, "max-input-value", TOLERANCE);

        if(input != null) {
            InhibitoryActivation.connectFields(
                    input.getAllInhibitoryLinks(),
                    Stream.of(this)
            );
        }
    }

    @Override
    protected void connectInputValue() {
    }

    @Override
    protected void initWeightedInput() {
        weightedInput = mul(
                this,
                "iAct(" + getInputKeyString() + ").value * s.weight",
                invert(this, "inverted-input-value", inputValue), true,
                synapse.getWeightForAnnealing(), false
        );
    }

    @Override
    public void connectWeightUpdate() {
        // TODO!
    }

    @Override
    public void visit(Visitor v, Scope s, int depth) {
    }
}
