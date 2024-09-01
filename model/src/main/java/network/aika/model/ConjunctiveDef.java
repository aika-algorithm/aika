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

import static network.aika.elements.activations.StateType.NON_FEEDBACK;
import static network.aika.elements.typedef.FieldTags.*;
import static network.aika.fielddefs.inputs.ArgInputs.argLink;
import static network.aika.fielddefs.inputs.VariableInputs.varLink;
import static network.aika.fields.IdentityFunction.identity;
import static network.aika.fields.SumField.sum;
import static network.aika.queue.Phase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class ConjunctiveDef extends TypeDefinitionBase {

    ActivationDefinition activation;
    NeuronDefinition neuron;

    SynapseSlotDefinition inputSlot;
    SynapseSlotDefinition outputSlot;

    LinkDefinition link;
    SynapseDefinition synapse;


    public ConjunctiveDef(TypeModel typeModel, NeuronDef superType) {
        super(typeModel, superType);
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
                .addParent(superType.getInputSlot())
                .setDirection(Direction.INPUT);

        outputSlot = new SynapseSlotDefinition(
                getTypeModel(),
                "ConjunctiveSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        )
                .addParent(superType.getOutputSlot())
                .setDirection(Direction.OUTPUT);

        link = new LinkDefinition(
                getTypeModel(),
                "ConjunctiveLink",
                ConjunctiveLink.class)
                .addParent(superType.getLink())
                .setInputSlot(inputSlot)
                .setOutputSlot(outputSlot)
                .setOutput(activation);

        synapse = new SynapseDefinition(
                getTypeModel(),
                "ConjunctiveSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(link)
                .setOutput(neuron);

        sum(synapse, SYNAPSE_BIAS)
                .setQueued(TRAINING);

        identity(link, SYNAPSE_BIAS)
                .in((o, p) -> o.getSynapse(p).getFieldOutput(SYNAPSE_BIAS), argLink(0))
                .out((o, p) -> o.getOutput(p).getState(p, NON_FEEDBACK).getField(NET), varLink());
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
