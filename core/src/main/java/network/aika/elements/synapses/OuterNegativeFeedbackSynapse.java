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
package network.aika.elements.synapses;

import network.aika.enums.Scope;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.OuterInhibitoryActivation;
import network.aika.elements.links.OuterNegativeFeedbackLink;
import network.aika.elements.neurons.OuterInhibitoryNeuron;

/**
 *
 * @author Lukas Molzberger
 */
public class OuterNegativeFeedbackSynapse extends FeedbackSynapse<
        OuterNegativeFeedbackSynapse,
        OuterInhibitoryNeuron,
        OuterNegativeFeedbackLink,
        OuterInhibitoryActivation
        >
{
    public OuterNegativeFeedbackSynapse() {
        super(Scope.INPUT);
    }

    @Override
    public void initDummyLink(BindingActivation oAct) {
    }

    @Override
    public OuterNegativeFeedbackLink checkExistingLink(OuterInhibitoryActivation iAct, BindingActivation oAct) {
        return (OuterNegativeFeedbackLink) oAct.getInputLink(iAct);
    }

    @Override
    protected void checkWeight() {
        if(!isNegative())
            delete();
    }

    @Override
    public OuterNegativeFeedbackLink createLink(OuterInhibitoryActivation input, BindingActivation output) {
        return new OuterNegativeFeedbackLink(this, input, output);
    }

    @Override
    public void linkAndPropagateOut(OuterInhibitoryActivation act) {
        getOutput()
                .linkOutgoing(this, act);
    }

    @Override
    public boolean checkLinkingEvent(Activation act) {
        return true;
    }

    @Override
    public double getPropagatePreNet(OuterInhibitoryActivation iAct) {
        return weight.getValue();
    }
}