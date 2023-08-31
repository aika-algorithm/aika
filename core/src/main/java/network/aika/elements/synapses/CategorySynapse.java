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
package network.aika.elements.synapses;

import network.aika.enums.Scope;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.links.CategoryLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.Neuron;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class CategorySynapse<S extends CategorySynapse, I extends Neuron, IA extends Activation<?>> extends DisjunctiveSynapse<
        S,
        I,
        CategoryNeuron,
        CategoryLink<S, IA>,
        IA,
        CategoryActivation
        >
{

    @Override
    public void link() {
        input.addOutputSynapse(this);
    }

    @Override
    protected void setInitialWeight(Synapse templateSyn) {
        weight.setInitialValue(
                getConfig().getInitialCategorySynapseWeight()
        );
    }
}
