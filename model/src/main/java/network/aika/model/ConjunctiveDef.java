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
import network.aika.enums.direction.Direction;

import static network.aika.elements.typedef.FieldTags.BIAS;
import static network.aika.fields.SumField.sum;
import static network.aika.queue.Phase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class ConjunctiveDef extends TypeDefinitionBase {

    private NeuronDef superType;

    ActivationDefinition activation;
    NeuronDefinition neuron;

    SynapseSlotDefinition inputSlot;
    SynapseSlotDefinition outputSlot;

    LinkDefinition link;
    SynapseDefinition synapse;


    public ConjunctiveDef(TypeModel typeModel, NeuronDef superType) {
        super(typeModel);
        this.superType = superType;
    }

    public void initNodes() {
        activation = new ActivationDefinition(
                getTypeModel(),
                "ConjunctiveActivation",
                Activation.class
        )
                .addParent(superType.getActivation());

        neuron = new NeuronDefinition(
                getTypeModel(),
                "ConjunctiveNeuron",
                Neuron.class
        )
                .addParent(superType.getNeuron())
                .setActivation(activation);

        sum(neuron, BIAS)
                .setQueued(TRAINING);
    }


    public void initRelations() {
        inputSlot = new SynapseSlotDefinition(
                getTypeModel(),
                "ConjunctiveSynapseInputSlot",
                ConjunctiveSynapseSlot.class
        )
                .setDirection(Direction.INPUT);

        outputSlot = new SynapseSlotDefinition(
                getTypeModel(),
                "ConjunctiveSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        )
                .setDirection(Direction.OUTPUT);

        link = new LinkDefinition(
                getTypeModel(),
                "ConjunctiveLink",
                ConjunctiveLink.class)
                .addParent(superType.getLink())
                .setOutput(activation);

        synapse = new SynapseDefinition(
                getTypeModel(),
                "ConjunctiveSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(link)
                .setOutput(neuron);
    }

    public TypeModel getTypeModel() {
        return superType.getTypeModel();
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
