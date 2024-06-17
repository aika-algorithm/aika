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
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.slots.ConjunctiveSynapseSlot;
import network.aika.elements.typedef.*;

/**
 *
 * @author Lukas Molzberger
 */
public class ConjunctiveDef {

    private NeuronDef superType;

    ActivationTypeDefinition activation;
    NeuronTypeDefinition neuron;

    SynapseSlotTypeDefinition synapseInputSlot;
    SynapseSlotTypeDefinition synapseOutputSlot;

    LinkTypeDefinition link;
    SynapseTypeDefinition synapse;


    public ConjunctiveDef(NeuronDef superType) {
        this.superType = superType;
        this.superType.conjunctiveDef = this;
    }

    public void init() {
        activation = new ActivationTypeDefinition(
                "ConjunctiveActivation",
                Activation.class
        );

        neuron = new NeuronTypeDefinition(
                "ConjunctiveNeuron",
                Neuron.class
        );

        synapseInputSlot = new SynapseSlotTypeDefinition(
                "ConjunctiveSynapseInputSlot",
                ConjunctiveSynapseSlot.class
        );

        synapseOutputSlot = new SynapseSlotTypeDefinition(
                "ConjunctiveSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        );

        link = new LinkTypeDefinition(
                "ConjunctiveLink",
                ConjunctiveLink.class);

        synapse = new SynapseTypeDefinition(
                "ConjunctiveSynapse",
                ConjunctiveSynapse.class
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
