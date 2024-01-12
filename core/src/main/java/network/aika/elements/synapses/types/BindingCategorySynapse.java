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
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.links.types.BindingCategoryLink;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.SynapseType;

import static network.aika.elements.Type.BINDING;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Transition.SAME_SAME;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = BINDING,
        outputType = BINDING,
        transition = {INPUT_INPUT, SAME_SAME},
        required = INPUT_INPUT,
        trigger = FIRED_PRE_FEEDBACK
)
public class BindingCategorySynapse extends CategorySynapse<BindingCategorySynapse, Neuron, BindingActivation> {

    @Override
    public BindingCategoryLink createLink(BindingActivation input, CategoryActivation output) {
        return new BindingCategoryLink(this, input, output);
    }
}
