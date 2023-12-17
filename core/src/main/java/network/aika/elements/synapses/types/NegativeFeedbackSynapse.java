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
package network.aika.elements.synapses.types;

import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.InhibitoryActivation;
import network.aika.elements.links.types.NegativeFeedbackLink;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.direction.Direction;

import static network.aika.elements.Type.*;
import static network.aika.elements.activations.StateType.WITH_FEEDBACK;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = INHIBITORY,
        outputType = BINDING,
        transition = INPUT_INPUT,
        required = INPUT_INPUT,
        trigger = FIRED_PRE_FEEDBACK,
        feedbackMode = WITH_FEEDBACK
)
public class NegativeFeedbackSynapse extends Synapse<
        NegativeFeedbackSynapse,
        InhibitoryNeuron,
        BindingNeuron,
        NegativeFeedbackLink,
        InhibitoryActivation,
        BindingActivation
        >
{
    @Override
    public double[] getSumOfLowerWeights() {
        return SULW_ZERO;
    }

    @Override
    protected void checkWeight() {
        if(!isNegative())
            delete();
    }

    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return false;
    }

    @Override
    public NegativeFeedbackLink createLink(InhibitoryActivation input, BindingActivation output) {
        return new NegativeFeedbackLink(this, input, output);
    }

    @Override
    public Direction getStoredAt() {
        return OUTPUT;
    }

    @Override
    public void setModified() {
        BindingNeuron no = getOutput();
        if(no != null)
            no.setModified();
    }
}
