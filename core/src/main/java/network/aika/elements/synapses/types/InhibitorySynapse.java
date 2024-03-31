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
import network.aika.elements.links.types.InhibitoryLink;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.direction.DirectionEnum;

import static network.aika.elements.NeuronType.*;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = BINDING,
        outputType = INHIBITORY,
        transition = INPUT_INPUT,
        required = INPUT_INPUT,
        trigger = FIRED_PRE_FEEDBACK,
        storedAt = DirectionEnum.INPUT
)
public class InhibitorySynapse extends DisjunctiveSynapse<
        InhibitorySynapse,
        BindingNeuron,
        InhibitoryNeuron,
        InhibitoryLink,
        BindingActivation,
        InhibitoryActivation
        > {
}
