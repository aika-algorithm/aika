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

import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.DisjunctiveActivation;
import network.aika.elements.links.CategoryLink;
import network.aika.elements.links.DisjunctiveLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.DisjunctiveNeuron;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.ActivationTypeDefinition;
import network.aika.elements.typedef.LinkTypeDefinition;
import network.aika.elements.typedef.NeuronTypeDefinition;
import network.aika.elements.typedef.SynapseTypeDefinition;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.PRE_FEEDBACK;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_INPUT;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Transition.SAME_INPUT;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;
import static network.aika.enums.Trigger.NOT_FIRED;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public class InhibitoryDef implements TypeDefinition {

    private TypeModel typeModel;

    private ActivationTypeDefinition activation;
    private NeuronTypeDefinition neuron;
    private ActivationTypeDefinition categoryActivation;
    private NeuronTypeDefinition categoryNeuron;

    private LinkTypeDefinition link;
    private SynapseTypeDefinition synapse;
    private LinkTypeDefinition primaryLink;
    private SynapseTypeDefinition primarySynapse;
    private LinkTypeDefinition categoryInputLink;
    private SynapseTypeDefinition categoryInputSynapse;
    private LinkTypeDefinition categoryLink;
    private SynapseTypeDefinition categorySynapse;

    public InhibitoryDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {

        activation = new ActivationTypeDefinition(
                "InhibitoryActivation",
                DisjunctiveActivation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        neuron = new NeuronTypeDefinition(
                "InhibitoryNeuron",
                DisjunctiveNeuron.class
        )
                .setNeuronType(INHIBITORY)
                .setActivationType(activation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,100,255);");


        categoryActivation = new ActivationTypeDefinition(
                "InhibitoryCategoryActivation",
                CategoryActivation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        categoryNeuron = new NeuronTypeDefinition(
                "InhibitoryCategoryNeuron",
                CategoryNeuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(categoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,0,200);");


        link = new LinkTypeDefinition(
                "InhibitoryLink",
                DisjunctiveLink.class
        )
                .setInputDef(typeModel.bindingDef.getActivation())
                .setOutputDef(activation);

        synapse = new SynapseTypeDefinition(
                "InhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .setLinkType(link)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(INHIBITORY)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(100,100,255);");


        primaryLink = new LinkTypeDefinition(
                "PrimaryInhibitoryLink",
                DisjunctiveLink.class);

        primarySynapse = new SynapseTypeDefinition(
                "PrimaryInhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .setLinkType(primaryLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(INHIBITORY)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(70,70,210);");


        categoryLink = new LinkTypeDefinition(
                "InhibitoryCategoryLink",
                CategoryLink.class);

        categorySynapse = new SynapseTypeDefinition(
                "InhibitoryCategorySynapse",
                CategorySynapse.class
        )
                .setLinkType(categoryLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(INHIBITORY)
                .setOutputNeuronType(CATEGORY)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(NOT_FIRED)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(110,0,220);");


        categoryInputLink = new LinkTypeDefinition(
                "InhibitoryCategoryInputLink",
                DisjunctiveLink.class);

        categoryInputSynapse = new SynapseTypeDefinition(
                "InhibitoryCategoryInputSynapse",
                Synapse.class
        )
                .setLinkType(categoryInputLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(CATEGORY)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setOutputState(PRE_FEEDBACK)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setTrainingAllowed(false)
                .setInstanceSynapseType(categorySynapse)
                .setDebugStyle("fill-color: rgb(110,200,220); ");
    }


    public ActivationTypeDefinition getActivation() {
        return activation;
    }

    public NeuronTypeDefinition getNeuron() {
        return neuron;
    }

    public ActivationTypeDefinition getCategoryActivation() {
        return categoryActivation;
    }

    public NeuronTypeDefinition getCategoryNeuron() {
        return categoryNeuron;
    }

    public LinkTypeDefinition getLink() {
        return link;
    }

    public SynapseTypeDefinition getSynapse() {
        return synapse;
    }

    public LinkTypeDefinition getPrimaryLink() {
        return primaryLink;
    }

    public SynapseTypeDefinition getPrimarySynapse() {
        return primarySynapse;
    }

    public LinkTypeDefinition getCategoryInputLink() {
        return categoryInputLink;
    }

    public SynapseTypeDefinition getCategoryInputSynapse() {
        return categoryInputSynapse;
    }

    public LinkTypeDefinition getCategoryLink() {
        return categoryLink;
    }

    public SynapseTypeDefinition getCategorySynapse() {
        return categorySynapse;
    }

}
