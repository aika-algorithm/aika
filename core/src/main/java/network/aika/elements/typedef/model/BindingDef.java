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
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.CategoryLink;
import network.aika.elements.links.ConjunctiveCategoryInputLink;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.links.PositiveFeedbackLink;
import network.aika.elements.links.types.NegativeFeedbackLink;
import network.aika.elements.links.types.RelationInputLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.PositiveFeedbackSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.slots.ConjunctiveSynapseSlot;
import network.aika.elements.synapses.types.NegativeFeedbackSynapse;
import network.aika.elements.synapses.types.RelationInputSynapse;
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
public class BindingDef {

    private TypeModel typeModel;

    private ActivationTypeDefinition bindingActivation;
    private NeuronTypeDefinition bindingNeuron;

    private LinkTypeDefinition bindingCategoryInputLink;
    private SynapseTypeDefinition bindingCategoryInputSynapse;

    private ActivationTypeDefinition bindingCategoryActivation;
    private NeuronTypeDefinition bindingCategoryNeuron;

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

    private LinkTypeDefinition bindingCategoryLink;
    private SynapseTypeDefinition bindingCategorySynapse;


    FieldDefinition<Synapse, Field> negativeWeight;

    public BindingDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }


    public void init() {
        bindingActivation = new ActivationTypeDefinition(
                "BindingActivation",
                ConjunctiveActivation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState())
                .addStateType(typeModel.states.getOuterFeedbackState())
                .addStateType(typeModel.states.getInnerFeedbackState())
                .addParent(typeModel.conjunctiveDef.getConjunctiveActivation());

        bindingNeuron = new NeuronTypeDefinition(
                "BindingNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(BINDING)
                .setActivationType(bindingActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME_FEEDBACK)
                .addParent(typeModel.conjunctiveDef.getConjunctiveNeuron())
                .setDebugStyle("fill-color: rgb(0,205,0);");


        outerFeedbackAnnealingSynapseOutputSlot = new SynapseSlotTypeDefinition(
                "OuterFeedbackAnnealingSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        )
                .addParent(typeModel.conjunctiveDef.getAnnealingSynapseOutputSlot());

        bindingCategoryActivation = new ActivationTypeDefinition(
                "BindingCategoryActivation",
                CategoryActivation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        bindingCategoryNeuron = new NeuronTypeDefinition(
                "BindingCategoryNeuron",
                CategoryNeuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(bindingCategoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,0,200);");

        latentRelationActivation = new ActivationTypeDefinition(
                "LatentRelationActivation",
                ConjunctiveActivation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState())
                .addParent(typeModel.conjunctiveDef.getConjunctiveActivation());

        latentRelationNeuron = new NeuronTypeDefinition(
                "LatentRelationNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(BINDING)
                .setActivationType(latentRelationActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots()
                .addParent(typeModel.conjunctiveDef.getConjunctiveNeuron())
                .setDebugStyle("fill-color: rgb(10,170,0);");



        inputObjectLink = new LinkTypeDefinition(
                "InputObjectLink",
                ConjunctiveLink.class
        )
                .setInputDef(typeModel.patternDef.getPatternActivation())
                .setOutputDef(bindingActivation)
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
                .addParent(typeModel.conjunctiveDef.getConjunctiveSynapse())
                .setDebugStyle("fill-color: rgb(0,150,00);");


        sameObjectLink = new LinkTypeDefinition(
                "SameObjectLink",
                ConjunctiveLink.class
        )
                .setInputDef(bindingActivation)
                .setOutputDef(bindingActivation);

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
                .setPropagateRange(false)
                .setDebugStyle("fill-color: rgb(50,200,120);");

        innerPositiveFeedbackLink = new LinkTypeDefinition(
                "InnerPositiveFeedbackLink",
                PositiveFeedbackLink.class)
                .setInputDef(typeModel.patternDef.getPatternActivation())
                .setOutputDef(typeModel.bindingDef.getBindingActivation());

        innerPositiveFeedbackSynapse = new SynapseTypeDefinition(
                "InnerPositiveFeedbackSynapse",
                PositiveFeedbackSynapse.class
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
                .setPropagateRange(false)
                .setDebugStyle("fill-color: rgb(120,200,50); arrow-shape: diamond;");


        outerPositiveFeedbackLink = new LinkTypeDefinition(
                "OuterPositiveFeedbackLink",
                PositiveFeedbackLink.class
        )
                .setInputDef(typeModel.patternDef.getPatternActivation())
                .setOutputDef(typeModel.bindingDef.getBindingActivation());

        outerPositiveFeedbackSynapse = new SynapseTypeDefinition(
                "OuterPositiveFeedbackSynapse",
                PositiveFeedbackSynapse.class
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
                .setRegisterInputSlot(ON_INIT)
                .setDebugStyle("fill-color: rgb(90,200,20); arrow-shape: diamond;");

        negativeFeedbackLink = new LinkTypeDefinition(
                "NegativeFeedbackLink",
                NegativeFeedbackLink.class
        )
                .setInputDef(typeModel.inhibitoryDef.getInhibitoryActivation())
                .setOutputDef(bindingActivation);

        negativeFeedbackLink.inputValue = new FieldDefinition(MaxField.class, negativeFeedbackLink, "max-input-value", TOLERANCE);


        negativeFeedbackSynapse = new SynapseTypeDefinition(
                "NegativeFeedbackSynapse",
                NegativeFeedbackSynapse.class
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
                .setRegisterInputSlot(ON_INIT)
                .setDebugStyle("fill-color: rgb(185,0,0); arrow-shape: diamond;");

        negativeWeight = scale(negativeFeedbackSynapse, "weight", -1, typeModel.neuron.getSynapse().weight);
        link(typeModel.neuron.getSynapse().weight, negativeFeedbackLink.getOutputDef().getNet(negativeFeedbackSynapse.outputState()))
                .setPropagateUpdates(false);


        relationInputLink = new LinkTypeDefinition(
                "RelationInputLink",
                RelationInputLink.class)
                .setInputDef(bindingActivation)
                .setOutputDef(bindingActivation);

        relationInputSynapse = new SynapseTypeDefinition(
                "RelationInputSynapse",
                RelationInputSynapse.class
        )
                .setLinkType(relationInputLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setPropagateRange(false)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(50,230,50);");

        bindingCategoryLink = new LinkTypeDefinition(
                "BindingCategoryLink",
                CategoryLink.class)
                .setInputDef(typeModel.bindingDef.getBindingCategoryActivation())
                .setOutputDef(typeModel.bindingDef.getBindingActivation());

        bindingCategorySynapse = new SynapseTypeDefinition(
                "BindingCategorySynapse",
                CategorySynapse.class
        )
                .setLinkType(bindingCategoryLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(CATEGORY)
                .setTransition(INPUT_INPUT, SAME_SAME)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(110,0,220);");


        bindingCategoryInputLink = new LinkTypeDefinition(
                "BindingCategoryInputLink",
                ConjunctiveLink.class
        );

        bindingCategoryInputSynapse = new SynapseTypeDefinition(
                "BindingCategoryInputSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(bindingCategoryInputLink)
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
                .setInstanceSynapseType(bindingCategorySynapse)
                .setDebugStyle("fill-color: rgb(110,200,220);");
    }

    public ActivationTypeDefinition getLatentRelationActivation() {
        return latentRelationActivation;
    }

    public NeuronTypeDefinition getLatentRelationNeuron() {
        return latentRelationNeuron;
    }

    public ActivationTypeDefinition getBindingActivation() {
        return bindingActivation;
    }

    public NeuronTypeDefinition getBindingNeuron() {
        return bindingNeuron;
    }

    public ActivationTypeDefinition getBindingCategoryActivation() {
        return bindingCategoryActivation;
    }

    public NeuronTypeDefinition getBindingCategoryNeuron() {
        return bindingCategoryNeuron;
    }


    public LinkTypeDefinition getBindingCategoryInputLink() {
        return bindingCategoryInputLink;
    }

    public SynapseTypeDefinition getBindingCategoryInputSynapse() {
        return bindingCategoryInputSynapse;
    }


    public SynapseSlotTypeDefinition getOuterFeedbackAnnealingSynapseOutputSlot() {
        return outerFeedbackAnnealingSynapseOutputSlot;
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


    public LinkTypeDefinition getBindingCategoryLink() {
        return bindingCategoryLink;
    }

    public SynapseTypeDefinition getBindingCategorySynapse() {
        return bindingCategorySynapse;
    }

}
