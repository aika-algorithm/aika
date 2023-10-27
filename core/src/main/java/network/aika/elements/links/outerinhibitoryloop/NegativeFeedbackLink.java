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
package network.aika.elements.links.outerinhibitoryloop;

import network.aika.elements.Type;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InhibitoryActivation;
import network.aika.elements.links.FeedbackLink;
import network.aika.fields.*;
import network.aika.elements.synapses.inhibitoryloop.NegativeFeedbackSynapse;
import network.aika.visitor.Visitor;

import java.util.stream.Stream;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.OUTER_INHIBITORY;
import static network.aika.fields.Fields.mul;

/**
 * @author Lukas Molzberger
 */
public class NegativeFeedbackLink extends FeedbackLink<NegativeFeedbackSynapse, InhibitoryActivation, BindingActivation> {

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
        return OUTER_INHIBITORY;
    }

    @Override
    public Type getOutputType() {
        return BINDING;
    }

    @Override
    protected void initInputValue() {
        inputValue = new MaxField(this, "max-input-value");
    }

    @Override
    public Field getWeightedOutput() {
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
                getDocument().getAnnealing(),
                innerWeightedInput
        );
    }

    @Override
    public void bindingVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void patternVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void inhibVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void patternCatVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void innerSelfRefVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void outerSelfRefVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void disconnect() {
        super.disconnect();

        innerWeightedInput.disconnectAndUnlinkInputs(false);
    }
}
