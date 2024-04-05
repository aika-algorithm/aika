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

import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.slots.ConjunctiveSynapseSlot;
import network.aika.elements.typedef.*;

/**
 *
 * @author Lukas Molzberger
 */
public class ConjunctiveDef {

    private TypeModel typeModel;

    private ActivationTypeDefinition conjunctiveActivation;
    private NeuronTypeDefinition conjunctiveNeuron;

    private SynapseSlotTypeDefinition conjunctiveSynapseInputSlot;
    private SynapseSlotTypeDefinition conjunctiveSynapseOutputSlot;

    private LinkTypeDefinition conjunctiveLink;
    private SynapseTypeDefinition conjunctiveSynapse;


    public ConjunctiveDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {
        conjunctiveActivation = new ActivationTypeDefinition(
                "ConjunctiveActivation",
                ConjunctiveActivation.class
        );

        conjunctiveNeuron = new NeuronTypeDefinition(
                "ConjunctiveNeuron",
                ConjunctiveNeuron.class
        );

        conjunctiveSynapseInputSlot = new SynapseSlotTypeDefinition(
                "ConjunctiveSynapseInputSlot",
                ConjunctiveSynapseSlot.class
        );

        conjunctiveSynapseOutputSlot = new SynapseSlotTypeDefinition(
                "ConjunctiveSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        );

        conjunctiveLink = new LinkTypeDefinition(
                "InputObjectLink",
                ConjunctiveLink.class);

        conjunctiveSynapse = new SynapseTypeDefinition(
                "InputObjectSynapse",
                ConjunctiveSynapse.class
        );
    }


    public ActivationTypeDefinition getConjunctiveActivation() {
        return conjunctiveActivation;
    }

    public NeuronTypeDefinition getConjunctiveNeuron() {
        return conjunctiveNeuron;
    }

    public SynapseSlotTypeDefinition getConjunctiveSynapseInputSlot() {
        return conjunctiveSynapseInputSlot;
    }

    public SynapseSlotTypeDefinition getConjunctiveSynapseOutputSlot() {
        return conjunctiveSynapseOutputSlot;
    }

    public LinkTypeDefinition getConjunctiveLink() {
        return conjunctiveLink;
    }

    public SynapseTypeDefinition getConjunctiveSynapse() {
        return conjunctiveSynapse;
    }

}
