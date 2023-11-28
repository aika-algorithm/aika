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

import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.links.types.BindingCategoryInputLink;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.elements.synapses.PositiveFeedbackSynapse;

import static network.aika.elements.Type.BINDING;
import static network.aika.enums.Transition.*;

/**
 * The Same Pattern Binding Neuron Synapse is an inner synapse between two binding neurons of the same pattern.
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = BINDING,
        outputType = BINDING,
        transition = CATEGORY,
        forbidden = SAME,
        required = {CATEGORY, INPUT}
)
public class BindingCategoryInputSynapse extends PositiveFeedbackSynapse<
        BindingCategoryInputSynapse,
        CategoryNeuron,
        BindingNeuron,
        BindingCategoryInputLink,
        CategoryActivation,
        BindingActivation
        > implements CategoryInputSynapse<BindingCategoryInputSynapse>
{
    private double initialCategorySynapseWeight;

    @Override
    public BindingCategoryInputLink createLink(CategoryActivation input, BindingActivation output) {
        return new BindingCategoryInputLink(this, input, output);
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
