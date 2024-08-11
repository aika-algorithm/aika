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

import static network.aika.fields.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.NON_FEEDBACK;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_INPUT;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Transition.SAME_INPUT;
import static network.aika.enums.Trigger.FIRED_NON_FEEDBACK;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public class InhibitoryDef extends TypeDefinitionBase {

    private DisjunctiveDef superType;

    private CategoryDef categoryDef;

    private ActivationDefinition activation;
    private NeuronDefinition neuron;
    private ActivationDefinition categoryActivation;
    private NeuronDefinition categoryNeuron;

    private LinkDefinition link;
    private SynapseDefinition synapse;
    private LinkDefinition primaryLink;
    private SynapseDefinition primarySynapse;
    private LinkDefinition categoryInputLink;
    private SynapseDefinition categoryInputSynapse;
    private LinkDefinition categoryLink;
    private SynapseDefinition categorySynapse;

    public InhibitoryDef(TypeModel typeModel, DisjunctiveDef superType, CategoryDef categoryDef) {
        super(typeModel);

        this.superType = superType;
        this.categoryDef = categoryDef;
    }


    public void initNodes() {

        activation = new ActivationDefinition(
                getTypeModel(),
                "InhibitoryActivation",
                Activation.class
        )
                .addParent(superType.getActivation())
                .addStateType(getTypeModel().neuron.getNonFeedbackState());

        neuron = new NeuronDefinition(
                getTypeModel(),
                "InhibitoryNeuron",
                Neuron.class
        )
                .addParent(superType.getNeuron())
                .setNeuronType(INHIBITORY)
                .setActivation(activation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setTrainingAllowed(false);


        categoryActivation = new ActivationDefinition(
                getTypeModel(),
                "InhibitoryCategoryActivation",
                Activation.class
        )
                .addStateType(getTypeModel().neuron.getNonFeedbackState())
                .addParent(categoryDef.getActivation());

        categoryNeuron = new NeuronDefinition(
                getTypeModel(),
                "InhibitoryCategoryNeuron",
                Neuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivation(categoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT)
                .setTrainingAllowed(false)
                .addParent(categoryDef.getNeuron());
    }

    public void initRelations() {
        link = new LinkDefinition(
                getTypeModel(),
                "InhibitoryLink",
                DisjunctiveLink.class
        )
                .setInput(getTypeModel().binding.getActivation())
                .setOutput(activation)
                .setInputSlot(getTypeModel().disjunctive.getInputSlot())
                .setOutputSlot(getTypeModel().disjunctive.getOutputSlot());

        synapse = new SynapseDefinition(
                getTypeModel(),
                "InhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(link)
                .setInput(getTypeModel().binding.getNeuron())
                .setOutput(neuron)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setStoredAt(INPUT);


        primaryLink = new LinkDefinition(
                getTypeModel(),
                "PrimaryInhibitoryLink",
                DisjunctiveLink.class)
                .addParent(superType.getLink())
                .setInputSlot(getTypeModel().disjunctive.getInputSlot())
                .setOutputSlot(getTypeModel().disjunctive.getOutputSlot());

        primarySynapse = new SynapseDefinition(
                getTypeModel(),
                "PrimaryInhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(primaryLink)
                .setInput(getTypeModel().pattern.getNeuron())
                .setOutput(neuron)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setStoredAt(OUTPUT);


        categoryLink = new LinkDefinition(
                getTypeModel(),
                "InhibitoryCategoryLink",
                Link.class)
                .addParent(categoryDef.getLink())
                .setInputSlot(getTypeModel().disjunctive.getInputSlot())
                .setOutputSlot(getTypeModel().disjunctive.getOutputSlot());

        categorySynapse = new SynapseDefinition(
                getTypeModel(),
                "InhibitoryCategorySynapse",
                Synapse.class
        )
                .addParent(categoryDef.getSynapse())
                .setLink(categoryLink)
                .setInput(neuron)
                .setOutput(categoryNeuron)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setStoredAt(INPUT);


        categoryInputLink = new LinkDefinition(
                getTypeModel(),
                "InhibitoryCategoryInputLink",
                DisjunctiveLink.class)
                .addParent(superType.getLink())
                .setInputSlot(getTypeModel().disjunctive.getInputSlot())
                .setOutputSlot(getTypeModel().disjunctive.getOutputSlot());

        categoryInputSynapse = new SynapseDefinition(
                getTypeModel(),
                "InhibitoryCategoryInputSynapse",
                Synapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(categoryInputLink)
                .setInput(categoryNeuron)
                .setOutput(getTypeModel().binding.getNeuron())
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setOutputState(NON_FEEDBACK)
                .setTrigger(FIRED_NON_FEEDBACK)
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

    public TypeModel getTypeModel() {
        return superType.getTypeModel();
    }


    public ActivationDefinition getActivation() {
        return activation;
    }

    public NeuronDefinition getNeuron() {
        return neuron;
    }

    public ActivationDefinition getCategoryActivation() {
        return categoryActivation;
    }

    public NeuronDefinition getCategoryNeuron() {
        return categoryNeuron;
    }

    public LinkDefinition getLink() {
        return link;
    }

    public SynapseDefinition getSynapse() {
        return synapse;
    }

    public LinkDefinition getPrimaryLink() {
        return primaryLink;
    }

    public SynapseDefinition getPrimarySynapse() {
        return primarySynapse;
    }

    public LinkDefinition getCategoryInputLink() {
        return categoryInputLink;
    }

    public SynapseDefinition getCategoryInputSynapse() {
        return categoryInputSynapse;
    }

    public LinkDefinition getCategoryLink() {
        return categoryLink;
    }

    public SynapseDefinition getCategorySynapse() {
        return categorySynapse;
    }

}
