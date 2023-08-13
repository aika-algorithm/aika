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
import network.aika.elements.links.PatternCategoryInputLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.Neuron;

/**
 * The Same Pattern Binding Neuron Synapse is an inner synapse between two binding neurons of the same pattern.
 *
 * @author Lukas Molzberger
 */
public class PatternCategoryInputSynapse extends DisjunctiveSynapse<
        PatternCategoryInputSynapse,
        CategoryNeuron,
        Neuron<Activation>,
        PatternCategoryInputLink,
        CategoryActivation,
        Activation
        > implements CategoryInputSynapse {

    public PatternCategoryInputSynapse() {
        super(Scope.SAME);
    }

    @Override
    public PatternCategoryInputLink createLink(CategoryActivation input, Activation output) {
        return new PatternCategoryInputLink(this, input, output);
    }

    @Override
    public boolean isTrainingAllowed() {
        return false;
    }
}
