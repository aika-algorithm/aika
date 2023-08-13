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

import network.aika.Thought;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.CategorySynapse;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class CategoryNeuron extends DisjunctiveNeuron<CategoryActivation> {

    public CategoryNeuron() {
    }

    public CategoryInputSynapse getOutgoingCategoryInputSynapse() {
        return getOutputSynapseByType(CategoryInputSynapse.class);
    }

    @Override
    public boolean isTrainingAllowed() {
        return false;
    }

    @Override
    public CategoryActivation createActivation(Thought t) {
        return new CategoryActivation(t.createActivationId(), t, this);
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
    public CategoryInputSynapse getCategoryInputSynapse() {
        return getOutputSynapseByType(CategoryInputSynapse.class);
    }

    @Override
    public CategorySynapse getCategoryOutputSynapse() {
        throw new UnsupportedOperationException();
    }
}
