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
package network.aika.elements.synapses.inhibitoryloop;

import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InhibitoryActivation;
import network.aika.elements.links.inhibitoryloop.InhibitoryLink;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.InhibitoryNeuron;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.SynapseType;

import static network.aika.elements.Type.*;
import static network.aika.enums.Transition.INPUT;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        synapseTypeId = 12,
        inputType = BINDING,
        outputType = INHIBITORY,
        transition = INPUT
)
public class InhibitorySynapse extends DisjunctiveSynapse<
        InhibitorySynapse,
        BindingNeuron,
        InhibitoryNeuron,
        InhibitoryLink,
        BindingActivation,
        InhibitoryActivation
        > {
    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return !latent;
    }

    @Override
    public InhibitoryLink createLink(BindingActivation input, InhibitoryActivation output) {
        return new InhibitoryLink(this, input, output);
    }

    @Override
    public InhibitorySynapse instantiateTemplate(BindingNeuron input, InhibitoryNeuron output) {
        InhibitorySynapse s = new InhibitorySynapse();
        s.initFromTemplate(input, output, this);
        return s;
    }
}
