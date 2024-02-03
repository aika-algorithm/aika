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

import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.synapses.SynapseType;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.types.InnerPositiveFeedbackLink;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.PositiveFeedbackSynapse;

import static network.aika.elements.Type.*;
import static network.aika.elements.activations.StateType.INNER_FEEDBACK;
import static network.aika.enums.Trigger.NOT_FIRED;
import static network.aika.enums.Transition.SAME_SAME;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = PATTERN,
        outputType = BINDING,
        transition = SAME_SAME,
        required = SAME_SAME,
        trigger = NOT_FIRED,
        stateType = INNER_FEEDBACK,
        propagateRange = false
)
public class InnerPositiveFeedbackSynapse extends PositiveFeedbackSynapse<
        InnerPositiveFeedbackSynapse,
        PatternNeuron,
        BindingNeuron,
        InnerPositiveFeedbackLink,
        PatternActivation,
        BindingActivation
        >
{
    public InnerPositiveFeedbackLink createLink(PatternActivation input, BindingActivation output) {
        return new InnerPositiveFeedbackLink(this, input, output);
    }
}
