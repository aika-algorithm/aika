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
package network.aika.elements.typedef.model;

import network.aika.elements.activations.DisjunctiveActivation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.links.DisjunctiveLink;
import network.aika.elements.neurons.DisjunctiveNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.slots.DisjunctiveSynapseSlot;
import network.aika.elements.typedef.*;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.IdentityFunction;

import static network.aika.fielddefs.FieldLinkDefinition.link;
import static network.aika.fielddefs.Operators.mul;

/**
 *
 * @author Lukas Molzberger
 */
public class DisjunctiveDef {

    private TypeModel typeModel;

    private ActivationTypeDefinition disjunctiveActivation;
    private NeuronTypeDefinition disjunctiveNeuron;

    private SynapseSlotTypeDefinition disjunctiveSynapseInputSlot;
    private SynapseSlotTypeDefinition disjunctiveSynapseOutputSlot;

    private LinkTypeDefinition disjunctiveLink;
    private SynapseTypeDefinition disjunctiveSynapse;

    private FieldDefinition weightUpdate;

    public DisjunctiveDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {
        disjunctiveActivation = new ActivationTypeDefinition(
                "DisjunctiveActivation",
                DisjunctiveActivation.class
        );

        disjunctiveNeuron = new NeuronTypeDefinition(
                "DisjunctiveNeuron",
                DisjunctiveNeuron.class
        );

        disjunctiveSynapseInputSlot = new SynapseSlotTypeDefinition(
                "DisjunctiveSynapseInputSlot",
                DisjunctiveSynapseSlot.class
        );

        disjunctiveSynapseOutputSlot = new SynapseSlotTypeDefinition(
                "DisjunctiveSynapseOutputSlot",
                DisjunctiveSynapseSlot.class
        );

        disjunctiveLink = new LinkTypeDefinition(
                "DisjunctiveLink",
                DisjunctiveLink.class);

        weightUpdate = mul(
                this,
                "weight update",
                typeModel.neuron.getLink().inputIsFired,
                getOutput().getUpdateValue()
        );
        link(
                weightUpdate,
                typeModel.neuron.getSynapse().weight
        );

        disjunctiveSynapse = new SynapseTypeDefinition(
                "DisjunctiveSynapse",
                DisjunctiveSynapse.class
        );
    }

    public ActivationTypeDefinition getDisjunctiveActivation() {
        return disjunctiveActivation;
    }

    public NeuronTypeDefinition getDisjunctiveNeuron() {
        return disjunctiveNeuron;
    }

    public SynapseSlotTypeDefinition getDisjunctiveSynapseInputSlot() {
        return disjunctiveSynapseInputSlot;
    }

    public SynapseSlotTypeDefinition getDisjunctiveSynapseOutputSlot() {
        return disjunctiveSynapseOutputSlot;
    }


    public LinkTypeDefinition getDisjunctiveLink() {
        return disjunctiveLink;
    }

    public SynapseTypeDefinition getDisjunctiveSynapse() {
        return disjunctiveSynapse;
    }

}
