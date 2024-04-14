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
package network.aika.elements.neurons;

import network.aika.Model;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.Synapse;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class CategoryNeuron extends DisjunctiveNeuron<CategoryNeuron, CategoryActivation> {

    public CategoryNeuron(NeuronProvider np) {
        super(np);
    }

    public CategoryNeuron(Model m, RefType rt) {
        super(m, rt);
    }

    @Override
    public Synapse makeAbstract() {
        throw new UnsupportedOperationException();
    }

    public Synapse getOutgoingCategoryInputSynapse() {
        return getOutputSynapseByType(CategoryInputSynapse.class);
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public Synapse getCategoryInputSynapse() {
        return getOutputSynapseByType(CategoryInputSynapse.class);
    }

    @Override
    public CategorySynapse getCategoryOutputSynapse() {
        throw new UnsupportedOperationException();
    }
}
