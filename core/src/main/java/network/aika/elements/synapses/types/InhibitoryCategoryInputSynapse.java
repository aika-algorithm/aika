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
import network.aika.elements.links.types.InhibitoryCategoryInputLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.SynapseType;

import static network.aika.elements.Type.*;
import static network.aika.enums.Transition.CATEGORY;
import static network.aika.enums.Transition.INPUT;

/**
 * The Inhibitory Neuron Synapse is an inner synapse between two binding neurons of the same pattern.
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        synapseTypeId = 13,
        inputType = INHIBITORY,
        outputType = BINDING,
        transition = {INPUT, CATEGORY},
        required = {},
        forbidden = {}
)
public class InhibitoryCategoryInputSynapse extends DisjunctiveSynapse<
        InhibitoryCategoryInputSynapse,
        CategoryNeuron,
        InhibitoryNeuron,
        InhibitoryCategoryInputLink,
        CategoryActivation,
        InhibitoryActivation
        > implements CategoryInputSynapse<InhibitoryCategoryInputSynapse>
{
    public static int TYPE_ID = 13;

    private double initialCategorySynapseWeight;

    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return !latent;
    }

    @Override
    public InhibitoryCategoryInputLink createLink(CategoryActivation input, InhibitoryActivation output) {
        return new InhibitoryCategoryInputLink(this, input, output);
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
