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
package network.aika.elements.synapses.outerinhibitoryloop;

import network.aika.elements.Type;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.OuterInhibitoryActivation;
import network.aika.elements.links.outerinhibitoryloop.OuterInhibitoryCategoryLink;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.Scope;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.OUTER_INHIBITORY;
import static network.aika.enums.Scope.INPUT;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        synapseTypeId = 17,
        inputType = OUTER_INHIBITORY,
        outputType = OUTER_INHIBITORY,
        scope = INPUT
)
public class OuterInhibitoryCategorySynapse extends CategorySynapse<OuterInhibitoryCategorySynapse, Neuron, OuterInhibitoryActivation> {

    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return !latent;
    }

    @Override
    public OuterInhibitoryCategoryLink createLink(OuterInhibitoryActivation input, CategoryActivation output) {
        return new OuterInhibitoryCategoryLink(this, input, output);
    }
}
