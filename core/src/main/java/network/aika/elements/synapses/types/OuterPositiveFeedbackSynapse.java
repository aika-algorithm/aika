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
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.types.OuterPositiveFeedbackLink;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.SynapseType;
import network.aika.elements.synapses.PositiveFeedbackSynapse;
import network.aika.elements.synapses.slots.AnnealingSynapseOutputSlot;
import network.aika.elements.synapses.slots.AnnealingType;
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.enums.direction.DirectionEnum;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.PATTERN;
import static network.aika.elements.activations.StateType.OUTER_FEEDBACK;
import static network.aika.enums.Transition.*;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = PATTERN,
        outputType = BINDING,
        transition = SAME_INPUT,
        required = INPUT_SAME,
        trigger = FIRED_PRE_FEEDBACK,
        outputState = OUTER_FEEDBACK,
        storedAt = DirectionEnum.OUTPUT
)
public class OuterPositiveFeedbackSynapse extends PositiveFeedbackSynapse<
        OuterPositiveFeedbackSynapse,
        PatternNeuron,
        BindingNeuron,
        OuterPositiveFeedbackLink,
        PatternActivation,
        BindingActivation
        >
{
    @Override
    public OuterPositiveFeedbackLink createLink(PatternActivation input, BindingActivation output) {
        return new OuterPositiveFeedbackLink(this, input, output);
    }

    @Override
    public SynapseSlot createOutputSlot(BindingActivation oAct) {
        return new AnnealingSynapseOutputSlot(oAct, this, AnnealingType.OUTER_FEEDBACK);
    }
}
