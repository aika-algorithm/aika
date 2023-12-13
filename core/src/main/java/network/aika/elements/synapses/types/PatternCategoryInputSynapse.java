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

import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.links.types.PatternCategoryInputLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.InstantiationFeedbackSynapse;
import network.aika.elements.synapses.SynapseType;

import static network.aika.elements.Type.PATTERN;
import static network.aika.elements.activations.StateType.WITH_FEEDBACK;
import static network.aika.enums.Transition.SAME_SAME;

/**
 * The Same Pattern Binding Neuron Synapse is an inner synapse between two binding neurons of the same pattern.
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = PATTERN,
        outputType = PATTERN,
        transition = SAME_SAME,
        required = SAME_SAME,
        feedbackMode = WITH_FEEDBACK
)
public class PatternCategoryInputSynapse
        extends InstantiationFeedbackSynapse<
                    PatternCategoryInputSynapse,
                    CategoryNeuron,
                    PatternNeuron,
                    PatternCategoryInputLink,
                    CategoryActivation,
                    PatternActivation
                >
        implements CategoryInputSynapse<PatternCategoryInputSynapse> {

    private double initialCategorySynapseWeight;

    @Override
    public PatternCategoryInputLink createLink(CategoryActivation input, PatternActivation output) {
        return new PatternCategoryInputLink(this, input, output);
    }

    @Override
    public boolean isTrainingAllowed() {
        return false;
    }

    @Override
    public void setInitialCategorySynapseWeight(double initialCategorySynapseWeight) {
        this.initialCategorySynapseWeight = initialCategorySynapseWeight;
    }

    @Override
    public double getInitialInstanceWeight() {
        return initialCategorySynapseWeight;
    }
}
