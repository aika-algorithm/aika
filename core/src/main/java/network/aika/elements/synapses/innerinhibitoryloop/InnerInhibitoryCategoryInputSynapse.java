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
package network.aika.elements.synapses.innerinhibitoryloop;

import network.aika.elements.Type;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.links.innerinhibitoryloop.InnerInhibitoryCategoryInputLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.InnerInhibitoryNeuron;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.enums.Scope;

import static network.aika.elements.Type.*;

/**
 * The Inhibitory Neuron Synapse is an inner synapse between two binding neurons of the same pattern.
 *
 * @author Lukas Molzberger
 */
public class InnerInhibitoryCategoryInputSynapse extends DisjunctiveSynapse<
        InnerInhibitoryCategoryInputSynapse,
        CategoryNeuron,
        InnerInhibitoryNeuron,
        InnerInhibitoryCategoryInputLink,
        CategoryActivation,
        InnerInhibitoryActivation
        > implements CategoryInputSynapse<InnerInhibitoryCategoryInputSynapse>
{
    private double initialCategorySynapseWeight;

    @Override
    public Type getInputType() {
        return INNER_INHIBITORY;
    }

    @Override
    public Type getOutputType() {
        return BINDING;
    }

    @Override
    public Scope getScope() {
        return Scope.SAME;
    }

    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return !latent;
    }

    @Override
    public InnerInhibitoryCategoryInputLink createLink(CategoryActivation input, InnerInhibitoryActivation output) {
        return new InnerInhibitoryCategoryInputLink(this, input, output);
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
