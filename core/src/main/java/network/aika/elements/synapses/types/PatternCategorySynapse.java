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
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.types.PatternCategoryLink;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.SynapseType;

import static network.aika.elements.Type.PATTERN;
import static network.aika.enums.Transition.SAME_SAME;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = PATTERN,
        outputType = PATTERN,
        transition = SAME_SAME,
        required = SAME_SAME
)
public class PatternCategorySynapse extends CategorySynapse<PatternCategorySynapse, PatternNeuron, PatternActivation> {

    @Override
    public PatternCategoryLink createLink(PatternActivation input, CategoryActivation output) {
        return new PatternCategoryLink(this, input, output);
    }
}
