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

import static network.aika.elements.typedef.FieldTags.*;
import static network.aika.fielddefs.inputs.ArgInputs.argLink;
import static network.aika.fielddefs.inputs.VariableInputs.varLink;
import static network.aika.fields.Multiplication.mul;

/**
 *
 * @author Lukas Molzberger
 */
public class DisjunctiveDef extends TypeDefinitionBase {

    NeuronDef superType;

    ActivationDefinition activation;
    NeuronDefinition neuron;

    SynapseSlotDefinition inputSlot;
    SynapseSlotDefinition outputSlot;

    LinkDefinition link;
    SynapseDefinition synapse;

    public DisjunctiveDef(TypeModel typeModel, NeuronDef superType) {
        super(typeModel);
        this.superType = superType;
        this.superType.disjunctiveDef = this;
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
                DisjunctiveSynapseSlot.class
        );

        outputSlot = new SynapseSlotDefinition(
                getTypeModel(),
                "DisjunctiveSynapseOutputSlot",
                DisjunctiveSynapseSlot.class
        );

        link = new LinkDefinition(
                getTypeModel(),
                "DisjunctiveLink",
                DisjunctiveLink.class)
                .addParent(superType.getLink())
                .setInput(superType.conjunctiveDef.activation)
                .setOutput(activation);

        synapse = new SynapseDefinition(
                getTypeModel(),
                "DisjunctiveSynapse",
                DisjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(link);

        mul(link, WEIGHT_UPDATE)
                .in((o, p) -> o.getFieldOutput(INPUT_IS_FIRED), argLink(0))
                .in((o, p) -> o.getOutput(p).getFieldOutput(UPDATE_VALUE), argLink(1))
                .out((o, p) -> o.getSynapse(p).getField(WEIGHT), varLink());
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
