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

import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.types.InhibitoryActivation;
import network.aika.elements.links.types.InhibitoryCategoryLink;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.SynapseType;

import static network.aika.elements.Type.INHIBITORY;
import static network.aika.enums.Transition.*;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = INHIBITORY,
        outputType = INHIBITORY,
        transition = CATEGORY,
        forbidden = SAME
)
public class InhibitoryCategorySynapse extends CategorySynapse<InhibitoryCategorySynapse, Neuron, InhibitoryActivation> {

    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return !latent;
    }

    @Override
    public InhibitoryCategoryLink createLink(InhibitoryActivation input, CategoryActivation output) {
        return new InhibitoryCategoryLink(this, input, output);
    }
}