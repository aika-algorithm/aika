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
import network.aika.fielddefs.FieldDefinition;

import static network.aika.fielddefs.FieldLinkDefinition.link;
import static network.aika.fielddefs.Operators.mul;

/**
 *
 * @author Lukas Molzberger
 */
public class DisjunctiveDef {

    NeuronDef superType;

    ActivationTypeDefinition activation;
    NeuronTypeDefinition neuron;

    SynapseSlotTypeDefinition synapseInputSlot;
    SynapseSlotTypeDefinition synapseOutputSlot;

    LinkTypeDefinition link;
    SynapseTypeDefinition synapse;

    FieldDefinition weightUpdate;

    public DisjunctiveDef(NeuronDef superType) {
        this.superType = superType;
        this.superType.disjunctiveDef = this;
    }

    public void init() {
        activation = new ActivationTypeDefinition(
                "DisjunctiveActivation",
                Activation.class
        );

        neuron = new NeuronTypeDefinition(
                "DisjunctiveNeuron",
                Neuron.class
        );

        synapseInputSlot = new SynapseSlotTypeDefinition(
                "DisjunctiveSynapseInputSlot",
                DisjunctiveSynapseSlot.class
        );

        synapseOutputSlot = new SynapseSlotTypeDefinition(
                "DisjunctiveSynapseOutputSlot",
                DisjunctiveSynapseSlot.class
        );

        link = new LinkTypeDefinition(
                "DisjunctiveLink",
                DisjunctiveLink.class)
                .setSynapseDef(synapse)
                .setInputDef(superType.conjunctiveDef.activation)
                .setOutputDef(activation);

        weightUpdate = mul(
                link,
                "weight update",
                superType.getLink().inputIsFired,
                link.getOutputDef().updateValue
        );
        link(
                weightUpdate,
                superType.synapse.weight
        );

        synapse = new SynapseTypeDefinition(
                "DisjunctiveSynapse",
                DisjunctiveSynapse.class
        );
    }

    public ActivationTypeDefinition getActivation() {
        return activation;
    }

    public NeuronTypeDefinition getNeuron() {
        return neuron;
    }

    public SynapseSlotTypeDefinition getSynapseInputSlot() {
        return synapseInputSlot;
    }

    public SynapseSlotTypeDefinition getSynapseOutputSlot() {
        return synapseOutputSlot;
    }


    public LinkTypeDefinition getLink() {
        return link;
    }

    public SynapseTypeDefinition getSynapse() {
        return synapse;
    }

}
