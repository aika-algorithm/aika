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
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.elements.typedef.*;
import network.aika.enums.direction.Direction;

import static network.aika.elements.typedef.FieldTags.*;
import static network.aika.fielddefs.link.FieldLinkTypeDefinition.argLink;
import static network.aika.fielddefs.link.FieldLinkTypeDefinition.varLink;
import static network.aika.fields.IdentityFunction.identity;
import static network.aika.fields.Multiplication.mul;

/**
 *
 * @author Lukas Molzberger
 */
public class DisjunctiveDef extends TypeDefinitionBase {

    ActivationDefinition activation;
    NeuronDefinition neuron;

    SynapseSlotDefinition inputSlot;
    SynapseSlotDefinition outputSlot;

    LinkDefinition link;
    SynapseDefinition synapse;

    public DisjunctiveDef(TypeModel typeModel, NeuronDef superType) {
        super(typeModel, superType);
    }

    public void initNodes() {
        activation = new ActivationDefinition(
                getTypeModel(),
                "DisjunctiveActivation",
                Activation.class
        )
                .addParent(superType.getActivation());

        neuron = new NeuronDefinition(
                getTypeModel(),
                "DisjunctiveNeuron",
                Neuron.class
        )
                .addParent(superType.getNeuron())
                .setActivation(activation);
    }

    public void initRelations() {
        inputSlot = new SynapseSlotDefinition(
                getTypeModel(),
                "DisjunctiveSynapseInputSlot",
                SynapseSlot.class
        )
                .addParent(superType.getInputSlot())
                .setDirection(Direction.INPUT);

        outputSlot = new SynapseSlotDefinition(
                getTypeModel(),
                "DisjunctiveSynapseOutputSlot",
                SynapseSlot.class
        )
                .addParent(superType.getOutputSlot())
                .setDirection(Direction.OUTPUT);

        link = new LinkDefinition(
                getTypeModel(),
                "DisjunctiveLink",
                DisjunctiveLink.class)
                .addParent(superType.getLink())
                .setInputSlot(inputSlot)
                .setOutputSlot(outputSlot)
                .setInput(typeModel.conjunctive.activation)
                .setOutput(activation);

        synapse = new SynapseDefinition(
                getTypeModel(),
                "DisjunctiveSynapse",
                DisjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(link);

        identity(inputSlot, SLOT);

        identity(outputSlot, SLOT);

        mul(link, WEIGHT_UPDATE)
                .in(o -> o.getFieldOutput(INPUT_IS_FIRED), INPUT_IS_FIRED, argLink(0))
                .in(o -> o.getOutput().getFieldOutput(UPDATE_VALUE), UPDATE_VALUE, argLink(1))
                .out(o -> o.getSynapse().getFieldInput(WEIGHT), WEIGHT, varLink());
    }

    @Override
    public TypeModel getTypeModel() {
        return superType.getTypeModel();
    }

    @Override
    public ActivationDefinition getActivation() {
        return activation;
    }

    @Override
    public NeuronDefinition getNeuron() {
        return neuron;
    }

    @Override
    public SynapseSlotDefinition getInputSlot() {
        return inputSlot;
    }

    @Override
    public SynapseSlotDefinition getOutputSlot() {
        return outputSlot;
    }

    @Override
    public LinkDefinition getLink() {
        return link;
    }

    @Override
    public SynapseDefinition getSynapse() {
        return synapse;
    }

}
