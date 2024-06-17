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
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.*;

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
                Activation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        neuron = new NeuronTypeDefinition(
                "InhibitoryNeuron",
                Neuron.class
        )
                .setNeuronType(INHIBITORY)
                .setActivationType(activation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setTrainingAllowed(false);


        categoryActivation = new ActivationTypeDefinition(
                "InhibitoryCategoryActivation",
                Activation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        categoryNeuron = new NeuronTypeDefinition(
                "InhibitoryCategoryNeuron",
                Neuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(categoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT)
                .setTrainingAllowed(false);


        link = new LinkTypeDefinition(
                "InhibitoryLink",
                DisjunctiveLink.class
        )
                .setInputDef(typeModel.binding.getActivation())
                .setOutputDef(activation);

        synapse = new SynapseTypeDefinition(
                "InhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .setLinkType(link)
                .setInputSlotType(typeModel.disjunctive.getSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctive.getSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(INHIBITORY)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT);


        primaryLink = new LinkTypeDefinition(
                "PrimaryInhibitoryLink",
                DisjunctiveLink.class);

        primarySynapse = new SynapseTypeDefinition(
                "PrimaryInhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .setLinkType(primaryLink)
                .setInputSlotType(typeModel.disjunctive.getSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctive.getSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(INHIBITORY)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT);


        categoryLink = new LinkTypeDefinition(
                "InhibitoryCategoryLink",
                Link.class);

        categorySynapse = new SynapseTypeDefinition(
                "InhibitoryCategorySynapse",
                Synapse.class
        )
                .setLinkType(categoryLink)
                .setInputSlotType(typeModel.disjunctive.getSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctive.getSynapseOutputSlot())
                .setInputNeuronType(INHIBITORY)
                .setOutputNeuronType(CATEGORY)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(NOT_FIRED)
                .setStoredAt(INPUT);


        categoryInputLink = new LinkTypeDefinition(
                "InhibitoryCategoryInputLink",
                DisjunctiveLink.class);

        categoryInputSynapse = new SynapseTypeDefinition(
                "InhibitoryCategoryInputSynapse",
                Synapse.class
        )
                .setLinkType(categoryInputLink)
                .setInputSlotType(typeModel.disjunctive.getSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctive.getSynapseOutputSlot())
                .setInputNeuronType(CATEGORY)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setOutputState(PRE_FEEDBACK)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setTrainingAllowed(false)
                .setInstanceSynapseType(categorySynapse);

        TemplateRelationDefinition templateRelationDef = new TemplateRelationDefinition()
                .setAbstractSynapseType(categoryInputSynapse)
                .setInstanceSynapseType(categorySynapse);

        neuron.setTemplateRelation(templateRelationDef);

        TemplateRelationDefinition categoryTemplateRelationDef = new TemplateRelationDefinition()
                .setAbstractSynapseType(categorySynapse)
                .setInstanceSynapseType(categoryInputSynapse);

        categoryNeuron.setTemplateRelation(categoryTemplateRelationDef);
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
