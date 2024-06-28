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
package network.aika.model;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.DisjunctiveLink;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.slots.DisjunctiveSynapseSlot;
import network.aika.elements.typedef.*;

import static network.aika.fields.Multiplication.mul;


/**
 *
 * @author Lukas Molzberger
 */
public class DisjunctiveDef {

    NeuronDef superType;

    ActivationDefinition activation;
    NeuronDefinition neuron;

    SynapseSlotDefinition inputSlot;
    SynapseSlotDefinition outputSlot;

    LinkDefinition link;
    SynapseDefinition synapse;

    public DisjunctiveDef(NeuronDef superType) {
        this.superType = superType;
        this.superType.disjunctiveDef = this;
    }

    public void init() {
        activation = new ActivationDefinition(
                "DisjunctiveActivation",
                Activation.class
        );

        neuron = new NeuronDefinition(
                "DisjunctiveNeuron",
                Neuron.class
        );

        inputSlot = new SynapseSlotDefinition(
                "DisjunctiveSynapseInputSlot",
                DisjunctiveSynapseSlot.class
        );

        outputSlot = new SynapseSlotDefinition(
                "DisjunctiveSynapseOutputSlot",
                DisjunctiveSynapseSlot.class
        );

        link = new LinkDefinition(
                "DisjunctiveLink",
                DisjunctiveLink.class)
                .setSynapse(synapse)
                .setInput(superType.conjunctiveDef.activation)
                .setOutput(activation);

        mul(link, "weight update")
                .in(0, (o, p) -> o.getInputIsFired())
                .in(1, (o, p) -> o.getOutput(p).getUpdateValue())
                .out(null, (o, p) -> o.getSynapse(p).getWeight());

        synapse = new SynapseDefinition(
                "DisjunctiveSynapse",
                DisjunctiveSynapse.class
        );
    }

    public ActivationDefinition getActivation() {
        return activation;
    }

    public NeuronDefinition getNeuron() {
        return neuron;
    }

    public SynapseSlotDefinition getInputSlot() {
        return inputSlot;
    }

    public SynapseSlotDefinition getOutputSlot() {
        return outputSlot;
    }


    public LinkDefinition getLink() {
        return link;
    }

    public SynapseDefinition getSynapse() {
        return synapse;
    }

}
