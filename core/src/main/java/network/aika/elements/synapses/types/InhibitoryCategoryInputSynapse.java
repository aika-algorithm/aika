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

import network.aika.Model;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.types.InhibitoryActivation;
import network.aika.elements.links.types.InhibitoryCategoryInputLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.direction.Direction;
import network.aika.enums.direction.DirectionEnum;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.Type.*;
import static network.aika.elements.activations.StateType.PRE_FEEDBACK;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 * The Inhibitory Neuron Synapse is an inner synapse between two binding neurons of the same pattern.
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = INHIBITORY,
        outputType = BINDING,
        transition = INPUT_INPUT,
        required = INPUT_INPUT,
        outputState = PRE_FEEDBACK,
        trigger = FIRED_PRE_FEEDBACK,
        storedAt = DirectionEnum.OUTPUT,
        trainingAllowed = false
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
    private double initialCategorySynapseWeight;

    @Override
    public void link() {
        input.addOutputSynapse(this);
        output.addInputSynapse(this);
    }

    @Override
    public InhibitoryCategoryInputLink createLink(CategoryActivation input, InhibitoryActivation output) {
        return new InhibitoryCategoryInputLink(this, input, output);
    }

    @Override
    public void setInitialCategorySynapseWeight(double initialCategorySynapseWeight) {
        this.initialCategorySynapseWeight = initialCategorySynapseWeight;
    }

    @Override
    public double getInitialCategorySynapseWeight() {
        return initialCategorySynapseWeight;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeDouble(initialCategorySynapseWeight);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        initialCategorySynapseWeight = in.readDouble();
    }
}
