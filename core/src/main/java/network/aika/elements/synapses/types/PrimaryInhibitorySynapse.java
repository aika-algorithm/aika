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

import network.aika.elements.activations.types.InhibitoryActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.types.PrimaryInhibitoryLink;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.direction.DirectionEnum;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.INHIBITORY;
import static network.aika.enums.Transition.SAME_INPUT;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = BINDING,
        outputType = INHIBITORY,
        transition = SAME_INPUT,
        required = SAME_INPUT,
        trigger = FIRED_PRE_FEEDBACK,
        storedAt = DirectionEnum.OUTPUT
)
public class PrimaryInhibitorySynapse extends DisjunctiveSynapse<
        PrimaryInhibitorySynapse,
        PatternNeuron,
        InhibitoryNeuron,
        PrimaryInhibitoryLink,
        PatternActivation,
        InhibitoryActivation
        > {

    @Override
    public PrimaryInhibitoryLink createLink(PatternActivation input, InhibitoryActivation output) {
        return new PrimaryInhibitoryLink(this, input, output);
    }
}
