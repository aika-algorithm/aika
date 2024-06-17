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
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.slots.ConjunctiveSynapseSlot;
import network.aika.elements.typedef.*;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.Field;
import network.aika.fields.MaxField;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.*;
import static network.aika.elements.activations.StateType.OUTER_FEEDBACK;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.*;
import static network.aika.elements.activations.bsslots.RegisterInputSlot.ON_INIT;
import static network.aika.enums.Transition.*;
import static network.aika.enums.Trigger.*;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.fielddefs.FieldLinkDefinition.link;
import static network.aika.fielddefs.Operators.scale;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class BindingDef implements TypeDefinition {

    private TypeModel typeModel;

    private ActivationTypeDefinition activation;
    private NeuronTypeDefinition neuron;

    private LinkTypeDefinition categoryInputLink;
    private SynapseTypeDefinition categoryInputSynapse;

    private ActivationTypeDefinition categoryActivation;
    private NeuronTypeDefinition categoryNeuron;

    private ActivationTypeDefinition latentRelationActivation;
    private NeuronTypeDefinition latentRelationNeuron;


    private SynapseSlotTypeDefinition outerFeedbackAnnealingSynapseOutputSlot;


    private LinkTypeDefinition inputObjectLink;
    private SynapseTypeDefinition inputObjectSynapse;
    private LinkTypeDefinition sameObjectLink;
    private SynapseTypeDefinition sameObjectSynapse;
    private LinkTypeDefinition innerPositiveFeedbackLink;
    private SynapseTypeDefinition innerPositiveFeedbackSynapse;
    private LinkTypeDefinition outerPositiveFeedbackLink;
    private SynapseTypeDefinition outerPositiveFeedbackSynapse;
    private LinkTypeDefinition negativeFeedbackLink;
    private SynapseTypeDefinition negativeFeedbackSynapse;
    private LinkTypeDefinition relationInputLink;
    private SynapseTypeDefinition relationInputSynapse;

    private LinkTypeDefinition categoryLink;
    private SynapseTypeDefinition categorySynapse;


    FieldDefinition<Synapse, Field> negativeWeight;

    public BindingDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }


    public void init() {
        activation = new ActivationTypeDefinition(
                "BindingActivation",
                Activation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState())
                .addStateType(typeModel.states.getOuterFeedbackState())
                .addStateType(typeModel.states.getInnerFeedbackState())
                .addParent(typeModel.conjunctiveDef.getConjunctiveActivation());

        neuron = new NeuronTypeDefinition(
                "BindingNeuron",
                Neuron.class
        )
                .setNeuronType(BINDING)
                .setActivationType(activation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME_FEEDBACK)
                .addParent(typeModel.conjunctiveDef.getConjunctiveNeuron());


        outerFeedbackAnnealingSynapseOutputSlot = new SynapseSlotTypeDefinition(
                "OuterFeedbackAnnealingSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        )
                .addParent(typeModel.conjunctiveDef.getAnnealingSynapseOutputSlot());

        categoryActivation = new ActivationTypeDefinition(
                "BindingCategoryActivation",
                Activation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        categoryNeuron = new NeuronTypeDefinition(
                "BindingCategoryNeuron",
                Neuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(categoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME)
                .setTrainingAllowed(false);

        latentRelationActivation = new ActivationTypeDefinition(
                "LatentRelationActivation",
                Activation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState())
                .addParent(typeModel.conjunctiveDef.getConjunctiveActivation());

        latentRelationNeuron = new NeuronTypeDefinition(
                "LatentRelationNeuron",
                Neuron.class
        )
                .setNeuronType(BINDING)
                .setActivationType(latentRelationActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots()
                .addParent(typeModel.conjunctiveDef.getConjunctiveNeuron());



        inputObjectLink = new LinkTypeDefinition(
                "InputObjectLink",
                ConjunctiveLink.class
        )
                .setInputDef(typeModel.patternDef.getActivation())
                .setOutputDef(activation)
                .addParent(typeModel.conjunctiveDef.getConjunctiveLink());

        inputObjectSynapse = new SynapseTypeDefinition(
                "InputObjectSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(inputObjectLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(BINDING)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .addParent(typeModel.conjunctiveDef.getConjunctiveSynapse());


        sameObjectLink = new LinkTypeDefinition(
                "SameObjectLink",
                ConjunctiveLink.class
        )
                .setInputDef(activation)
                .setOutputDef(activation);

        sameObjectSynapse = new SynapseTypeDefinition(
                "SameObjectSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(sameObjectLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(BINDING)
                .setTransition(SAME_SAME)
                .setRequired(INPUT_INPUT)
                .setTrigger(PRIMARY_CHECKED_FIRED_OUTER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setPropagateRange(false);

        innerPositiveFeedbackLink = new LinkTypeDefinition(
                "InnerPositiveFeedbackLink",
                ConjunctiveLink.class)
                .setInputDef(typeModel.patternDef.getActivation())
                .setOutputDef(typeModel.bindingDef.getActivation());

        innerPositiveFeedbackSynapse = new SynapseTypeDefinition(
                "InnerPositiveFeedbackSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(innerPositiveFeedbackLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(BINDING)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setTrigger(NOT_FIRED)
                .setOutputState(INNER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setPropagateRange(false);


        outerPositiveFeedbackLink = new LinkTypeDefinition(
                "OuterPositiveFeedbackLink",
                ConjunctiveLink.class
        )
                .setInputDef(typeModel.patternDef.getActivation())
                .setOutputDef(typeModel.bindingDef.getActivation());

        outerPositiveFeedbackSynapse = new SynapseTypeDefinition(
                "OuterPositiveFeedbackSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(outerPositiveFeedbackLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(outerFeedbackAnnealingSynapseOutputSlot)
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(BINDING)
                .setTransition(SAME_INPUT)
                .setRequired(INPUT_SAME)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setOutputState(OUTER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setRegisterInputSlot(ON_INIT);

        negativeFeedbackLink = new LinkTypeDefinition(
                "NegativeFeedbackLink",
                ConjunctiveLink.class
        )
                .setInputDef(typeModel.inhibitoryDef.getActivation())
                .setOutputDef(activation);

        negativeFeedbackLink.inputValue = new FieldDefinition(MaxField.class, negativeFeedbackLink, "max-input-value", TOLERANCE);


        negativeFeedbackSynapse = new SynapseTypeDefinition(
                "NegativeFeedbackSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(negativeFeedbackLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(outerFeedbackAnnealingSynapseOutputSlot)
                .setInputNeuronType(INHIBITORY)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setOutputState(OUTER_FEEDBACK)
                .setPropagateRange(false)
                .setStoredAt(OUTPUT)
                .setRegisterInputSlot(ON_INIT);

        negativeWeight = scale(negativeFeedbackSynapse, "weight", -1, typeModel.neuron.getSynapse().weight);
        link(typeModel.neuron.getSynapse().weight, negativeFeedbackLink.getOutputDef().getNet(negativeFeedbackSynapse.outputState()))
                .setPropagateUpdates(false);


        relationInputLink = new LinkTypeDefinition(
                "RelationInputLink",
                ConjunctiveLink.class)
                .setInputDef(activation)
                .setOutputDef(activation);

        relationInputSynapse = new SynapseTypeDefinition(
                "RelationInputSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(relationInputLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setPropagateRange(false)
                .setStoredAt(OUTPUT);

        categoryLink = new LinkTypeDefinition(
                "BindingCategoryLink",
                Link.class)
                .setInputDef(typeModel.bindingDef.getCategoryActivation())
                .setOutputDef(typeModel.bindingDef.getActivation());

        categorySynapse = new SynapseTypeDefinition(
                "BindingCategorySynapse",
                Synapse.class
        )
                .setLinkType(categoryLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(CATEGORY)
                .setTransition(INPUT_INPUT, SAME_SAME)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT);


        categoryInputLink = new LinkTypeDefinition(
                "BindingCategoryInputLink",
                ConjunctiveLink.class
        );

        categoryInputSynapse = new SynapseTypeDefinition(
                "BindingCategoryInputSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(categoryInputLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.categoryDef.getCategoryInputAnnealingSynapseOutputSlot())
                .setInputNeuronType(CATEGORY)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT, SAME_SAME)
                .setRequired(INPUT_INPUT)
                .setOutputState(PRE_FEEDBACK)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setTrainingAllowed(false)
                .setRegisterInputSlot(ON_INIT)
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

    public ActivationTypeDefinition getLatentRelationActivation() {
        return latentRelationActivation;
    }

    public NeuronTypeDefinition getLatentRelationNeuron() {
        return latentRelationNeuron;
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


    public LinkTypeDefinition getCategoryInputLink() {
        return categoryInputLink;
    }

    public SynapseTypeDefinition getCategoryInputSynapse() {
        return categoryInputSynapse;
    }


    public SynapseSlotTypeDefinition getOuterFeedbackAnnealingSynapseOutputSlot() {
        return outerFeedbackAnnealingSynapseOutputSlot;
    }

    public LinkTypeDefinition getLink() {
        return inputObjectLink;
    }

    public SynapseTypeDefinition getSynapse() {
        return inputObjectSynapse;
    }

    public LinkTypeDefinition getInputObjectLink() {
        return inputObjectLink;
    }

    public SynapseTypeDefinition getInputObjectSynapse() {
        return inputObjectSynapse;
    }

    public LinkTypeDefinition getSameObjectLink() {
        return sameObjectLink;
    }

    public SynapseTypeDefinition getSameObjectSynapse() {
        return sameObjectSynapse;
    }

    public LinkTypeDefinition getInnerPositiveFeedbackLink() {
        return innerPositiveFeedbackLink;
    }

    public SynapseTypeDefinition getInnerPositiveFeedbackSynapse() {
        return innerPositiveFeedbackSynapse;
    }

    public LinkTypeDefinition getOuterPositiveFeedbackLink() {
        return outerPositiveFeedbackLink;
    }

    public SynapseTypeDefinition getOuterPositiveFeedbackSynapse() {
        return outerPositiveFeedbackSynapse;
    }

    public LinkTypeDefinition getNegativeFeedbackLink() {
        return negativeFeedbackLink;
    }

    public SynapseTypeDefinition getNegativeFeedbackSynapse() {
        return negativeFeedbackSynapse;
    }

    public LinkTypeDefinition getRelationInputLink() {
        return relationInputLink;
    }

    public SynapseTypeDefinition getRelationInputSynapse() {
        return relationInputSynapse;
    }


    public LinkTypeDefinition getCategoryLink() {
        return categoryLink;
    }

    public SynapseTypeDefinition getCategorySynapse() {
        return categorySynapse;
    }

}
