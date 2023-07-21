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

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InputInhibitoryActivation;
import network.aika.elements.activations.SameInhibitoryActivation;
import network.aika.elements.links.InputNegativeFeedbackLink;
import network.aika.elements.links.SameNegativeFeedbackLink;
import network.aika.elements.neurons.InputInhibitoryNeuron;
import network.aika.elements.neurons.SameInhibitoryNeuron;
import network.aika.enums.Scope;

/**
 *
 * @author Lukas Molzberger
 */
public class SameNegativeFeedbackSynapse extends FeedbackSynapse<
        SameNegativeFeedbackSynapse,
        SameInhibitoryNeuron,
        SameNegativeFeedbackLink,
        SameInhibitoryActivation
        >
{
    public SameNegativeFeedbackSynapse() {
        super(Scope.INPUT);
    }

    @Override
    protected void checkWeight() {
        if(!isNegative())
            delete();
    }

    @Override
    public SameNegativeFeedbackLink createLink(SameInhibitoryActivation input, BindingActivation output) {
        return new SameNegativeFeedbackLink(this, input, output);
    }

    @Override
    public void linkAndPropagateOut(SameInhibitoryActivation act) {
        getOutput()
                .linkOutgoing(this, act);
    }

    @Override
    public SameNegativeFeedbackSynapse setWeight(double w) {
        weight.receiveUpdate(false, w);
        return this;
    }

    @Override
    public boolean checkLinkingEvent(Activation act) {
        return true;
    }

    @Override
    public double getPropagatePreNet(SameInhibitoryActivation iAct) {
        return weight.getValue();
    }
}
