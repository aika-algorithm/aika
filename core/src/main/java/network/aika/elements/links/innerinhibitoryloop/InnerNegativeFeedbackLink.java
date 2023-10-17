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
package network.aika.elements.links.innerinhibitoryloop;

import network.aika.elements.Type;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.links.FeedbackLink;
import network.aika.elements.synapses.innerinhibitoryloop.InnerNegativeFeedbackSynapse;
import network.aika.fields.Field;
import network.aika.fields.FieldLink;
import network.aika.fields.MaxField;
import network.aika.visitor.Visitor;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.INNER_INHIBITORY;
import static network.aika.elements.activations.InnerInhibitoryActivation.getBindingActivation;
import static network.aika.elements.activations.InnerInhibitoryActivation.updateConnected;


/**
 * @author Lukas Molzberger
 */
public class InnerNegativeFeedbackLink extends FeedbackLink<InnerNegativeFeedbackSynapse, InnerInhibitoryActivation, BindingActivation> {

    public InnerNegativeFeedbackLink(InnerNegativeFeedbackSynapse s, InnerInhibitoryActivation input, BindingActivation output) {
        super(s, input, output);
    }

    @Override
    public void propagateRanges() {
    }

    @Override
    public Type getInputType() {
        return INNER_INHIBITORY;
    }

    @Override
    public Type getOutputType() {
        return BINDING;
    }

    @Override
    protected void initInputValue() {
        super.initInputValue();
        inputValue.setInitialValue(1.0);
    }

    @Override
    public Field getWeightedOutput() {
        return getOutput().getNet();
    }

    @Override
    protected void connectInputValue() {
        if(inputValue.getInputs().get(0) == null)
            inputValue.setValue(0.0);

        FieldLink fl = FieldLink.link(input.getValue(), 0, inputValue);
        MaxField inhibNet = (MaxField) input.getNet();
        BindingActivation selectBindingAct = getBindingActivation(inhibNet.getSelectedInput());

        updateConnected(fl, selectBindingAct, output, true);
    }

    @Override
    public void bindingVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void patternVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void patternCatVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void outerInhibVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void innerSelfRefVisit(Visitor v, int state, int depth) {
    }

    @Override
    public void outerSelfRefVisit(Visitor v, int state, int depth) {
    }
}
