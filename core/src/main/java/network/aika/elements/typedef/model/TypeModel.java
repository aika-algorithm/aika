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


import network.aika.elements.activations.*;
import network.aika.elements.links.ConjunctiveCategoryInputLink;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.links.CategoryLink;
import network.aika.elements.links.DisjunctiveLink;
import network.aika.elements.links.PositiveFeedbackLink;
import network.aika.elements.links.types.NegativeFeedbackLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.DisjunctiveNeuron;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.slots.ConjunctiveSynapseSlot;
import network.aika.elements.synapses.slots.DisjunctiveSynapseSlot;
import network.aika.elements.synapses.types.NegativeFeedbackSynapse;
import network.aika.elements.synapses.types.RelationInputSynapse;
import network.aika.elements.typedef.*;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.*;
import static network.aika.elements.activations.StateType.INNER_FEEDBACK;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.*;
import static network.aika.enums.Transition.*;
import static network.aika.enums.Trigger.*;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public class TypeModel {

    StateDef states = new StateDef(this);

    NeuronDef neuron = new NeuronDef(this);

    ConjunctiveDef conjunctiveDef = new ConjunctiveDef(this);

    DisjunctiveDef disjunctiveDef = new DisjunctiveDef(this);

    BindingDef bindingDef = new BindingDef(this);


    private ActivationTypeDefinition latentRelationActivation;
    private NeuronTypeDefinition latentRelationNeuron;
    private ActivationTypeDefinition patternActivation;
    private NeuronTypeDefinition patternNeuron;
    private ActivationTypeDefinition patternCategoryActivation;
    private NeuronTypeDefinition patternCategoryNeuron;
    private ActivationTypeDefinition inhibitoryActivation;
    private NeuronTypeDefinition inhibitoryNeuron;
    private ActivationTypeDefinition inhibitoryCategoryActivation;
    private NeuronTypeDefinition inhibitoryCategoryNeuron;

    private SynapseSlotTypeDefinition annealingSynapseOutputSlot;

    private SynapseSlotTypeDefinition outerFeedbackAnnealingSynapseOutputSlot;
    private SynapseSlotTypeDefinition categoryInputAnnealingSynapseOutputSlot;

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
    private LinkTypeDefinition bindingCategoryInputLink;
    private SynapseTypeDefinition bindingCategoryInputSynapse;
    private LinkTypeDefinition patternLink;
    private SynapseTypeDefinition patternSynapse;
    private LinkTypeDefinition patternCategoryInputLink;
    private SynapseTypeDefinition patternCategoryInputSynapse;
    private LinkTypeDefinition inhibitoryLink;
    private SynapseTypeDefinition inhibitorySynapse;
    private LinkTypeDefinition primaryInhibitoryLink;
    private SynapseTypeDefinition primaryInhibitorySynapse;
    private LinkTypeDefinition inhibitoryCategoryInputLink;
    private SynapseTypeDefinition inhibitoryCategoryInputSynapse;
    private LinkTypeDefinition bindingCategoryLink;
    private SynapseTypeDefinition bindingCategorySynapse;
    private LinkTypeDefinition patternCategoryLink;
    private SynapseTypeDefinition patternCategorySynapse;
    private LinkTypeDefinition inhibitoryCategoryLink;
    private SynapseTypeDefinition inhibitoryCategorySynapse;

    public TypeModel() {
        states.init();
        initNeuronsAndActivations();
        initSynapseSlots();
        initSynapsesAndLinks();
    }


    private void initNeuronsAndActivations() {
        neuron.init();
        conjunctiveDef.init();
        disjunctiveDef.init();
        bindingDef.init();

        latentRelationActivation = new ActivationTypeDefinition(
                "LatentRelationActivation",
                ConjunctiveActivation.class
        )
                .addStateType(states.getPreFeedbackState())
                .addParent(conjunctiveDef.getConjunctiveActivation());

        latentRelationNeuron = new NeuronTypeDefinition(
                "LatentRelationNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(BINDING)
                .setActivationType(latentRelationActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots()
                .addParent(conjunctiveDef.getConjunctiveNeuron())
                .setDebugStyle("fill-color: rgb(10,170,0);");


        patternActivation = new ActivationTypeDefinition(
                "PatternActivation",
                ConjunctiveActivation.class
        )
                .addStateType(states.getPreFeedbackState());

        patternNeuron = new NeuronTypeDefinition(
                "PatternNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(PATTERN)
                .setActivationType(patternActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_SAME, MULTI_INPUT)
                .setDebugStyle("fill-color: rgb(224, 34, 245);");

        patternCategoryActivation = new ActivationTypeDefinition(
                "PatternCategoryActivation",
                CategoryActivation.class
        )
                .addStateType(states.getPreFeedbackState());

        patternCategoryNeuron = new NeuronTypeDefinition(
                "PatternCategoryNeuron",
                CategoryNeuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(patternCategoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_SAME)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,0,200);");

        inhibitoryActivation = new ActivationTypeDefinition(
                "InhibitoryActivation",
                DisjunctiveActivation.class
        )
                .addStateType(states.getPreFeedbackState());

        inhibitoryNeuron = new NeuronTypeDefinition(
                "InhibitoryNeuron",
                DisjunctiveNeuron.class
        )
                .setNeuronType(INHIBITORY)
                .setActivationType(inhibitoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,100,255);");


        inhibitoryCategoryActivation = new ActivationTypeDefinition(
                "InhibitoryCategoryActivation",
                CategoryActivation.class
        )
                .addStateType(states.getPreFeedbackState());

        inhibitoryCategoryNeuron = new NeuronTypeDefinition(
                "InhibitoryCategoryNeuron",
                CategoryNeuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(inhibitoryCategoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,0,200);");
    }

    private void initSynapseSlots() {

        annealingSynapseOutputSlot = new SynapseSlotTypeDefinition(
                "AnnealingSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        )
                .addParent(conjunctiveDef.getConjunctiveSynapseOutputSlot());

        outerFeedbackAnnealingSynapseOutputSlot = new SynapseSlotTypeDefinition(
                "OuterFeedbackAnnealingSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        )
                .addParent(annealingSynapseOutputSlot);

        categoryInputAnnealingSynapseOutputSlot = new SynapseSlotTypeDefinition(
                "CategoryInputAnnealingSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        )
                .addParent(annealingSynapseOutputSlot);
    }


    private void initSynapsesAndLinks() {
        inputObjectLink = new LinkTypeDefinition(
                "InputObjectLink",
                ConjunctiveLink.class)
                .addParent(conjunctiveDef.getConjunctiveLink());

        inputObjectSynapse = new SynapseTypeDefinition(
                "InputObjectSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(inputObjectLink)
                .setInputSlotType(conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(conjunctiveDef.getConjunctiveSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(BINDING)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .addParent(conjunctiveDef.getConjunctiveSynapse())
                .setDebugStyle("fill-color: rgb(0,150,00);");


        sameObjectLink = new LinkTypeDefinition(
                "SameObjectLink",
                ConjunctiveLink.class);

        sameObjectSynapse = new SynapseTypeDefinition(
                "SameObjectSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(sameObjectLink)
                .setInputSlotType(conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(conjunctiveDef.getConjunctiveSynapseOutputSlot())
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
                PositiveFeedbackLink.class);

        innerPositiveFeedbackSynapse = new SynapseTypeDefinition(
                "InnerPositiveFeedbackSynapse",
                PositiveFeedbackSynapse.class
        )
                .setLinkType(innerPositiveFeedbackLink)
                .setInputSlotType(conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(conjunctiveDef.getConjunctiveSynapseOutputSlot())
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
                PositiveFeedbackLink.class);

        outerPositiveFeedbackSynapse = new SynapseTypeDefinition(
                "OuterPositiveFeedbackSynapse",
                PositiveFeedbackSynapse.class
        )
                .setLinkType(outerPositiveFeedbackLink)
                .setInputSlotType(conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(outerFeedbackAnnealingSynapseOutputSlot)
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(BINDING)
                .setTransition(SAME_INPUT)
                .setRequired(INPUT_SAME)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setOutputState(OUTER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(90,200,20); arrow-shape: diamond;");

        negativeFeedbackLink = new LinkTypeDefinition(
                "NegativeFeedbackLink",
                NegativeFeedbackLink.class);

        negativeFeedbackSynapse = new SynapseTypeDefinition(
                "NegativeFeedbackSynapse",
                NegativeFeedbackSynapse.class
        )
                .setLinkType(negativeFeedbackLink)
                .setInputSlotType(conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(outerFeedbackAnnealingSynapseOutputSlot)
                .setInputNeuronType(INHIBITORY)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setOutputState(OUTER_FEEDBACK)
                .setPropagateRange(false)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(185,0,0); arrow-shape: diamond;");


        relationInputLink = new LinkTypeDefinition(
                "RelationInputLink",
                NegativeFeedbackLink.class);

        relationInputSynapse = new SynapseTypeDefinition(
                "RelationInputSynapse",
                RelationInputSynapse.class
        )
                .setLinkType(relationInputLink)
                .setInputSlotType(conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(conjunctiveDef.getConjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setPropagateRange(false)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(50,230,50);");


        bindingCategoryInputLink = new LinkTypeDefinition(
                "BindingCategoryInputLink",
                ConjunctiveCategoryInputLink.class);

        bindingCategoryInputSynapse = new SynapseTypeDefinition(
                "BindingCategoryInputSynapse",
                ConjunctiveCategoryInputSynapse.class
        )
                .setLinkType(bindingCategoryInputLink)
                .setInputSlotType(conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(categoryInputAnnealingSynapseOutputSlot)
                .setInputNeuronType(CATEGORY)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT, SAME_SAME)
                .setRequired(INPUT_INPUT)
                .setOutputState(PRE_FEEDBACK)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(110,200,220);");


        patternLink = new LinkTypeDefinition(
                "PatternLink",
                ConjunctiveLink.class);

        patternSynapse = new SynapseTypeDefinition(
                "PatternSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(patternLink)
                .setInputSlotType(conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(conjunctiveDef.getConjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(PATTERN)
                .setTransition(SAME_SAME, INPUT_INPUT)
                .setRequired(SAME_SAME)
                .setTrigger(PRIMARY_CHECKED_FIRED_OUTER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(224, 34, 245);");


        patternCategoryInputLink = new LinkTypeDefinition(
                "PatternCategoryInputLink",
                ConjunctiveCategoryInputLink.class);

        patternCategoryInputSynapse = new SynapseTypeDefinition(
                "PatternCategoryInputSynapse",
                ConjunctiveCategoryInputSynapse.class
        )
                .setLinkType(patternCategoryInputLink)
                .setInputSlotType(conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(categoryInputAnnealingSynapseOutputSlot)
                .setInputNeuronType(CATEGORY)
                .setOutputNeuronType(PATTERN)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setOutputState(PRE_FEEDBACK)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(110,200,220);");


        inhibitoryLink = new LinkTypeDefinition(
                "InhibitoryLink",
                DisjunctiveLink.class);

        inhibitorySynapse = new SynapseTypeDefinition(
                "InhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .setLinkType(inhibitoryLink)
                .setInputSlotType(disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(INHIBITORY)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(100,100,255);");


        primaryInhibitoryLink = new LinkTypeDefinition(
                "PrimaryInhibitoryLink",
                DisjunctiveLink.class);

        primaryInhibitorySynapse = new SynapseTypeDefinition(
                "PrimaryInhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .setLinkType(primaryInhibitoryLink)
                .setInputSlotType(disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(INHIBITORY)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(70,70,210);");


        inhibitoryCategoryInputLink = new LinkTypeDefinition(
                "InhibitoryCategoryInputLink",
                ConjunctiveCategoryInputLink.class);

        inhibitoryCategoryInputSynapse = new SynapseTypeDefinition(
                "InhibitoryCategoryInputSynapse",
                ConjunctiveCategoryInputSynapse.class
        )
                .setLinkType(bindingCategoryInputLink)
                .setInputSlotType(disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(CATEGORY)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setOutputState(PRE_FEEDBACK)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(110,200,220); ");

        bindingCategoryLink = new LinkTypeDefinition(
                "BindingCategoryLink",
                CategoryLink.class);

        bindingCategorySynapse = new SynapseTypeDefinition(
                "BindingCategorySynapse",
                CategorySynapse.class
        )
                .setLinkType(bindingCategoryLink)
                .setInputSlotType(disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(CATEGORY)
                .setTransition(INPUT_INPUT, SAME_SAME)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(110,0,220);");


        patternCategoryLink = new LinkTypeDefinition(
                "PatternCategoryLink",
                CategoryLink.class);

        patternCategorySynapse = new SynapseTypeDefinition(
                "PatternCategorySynapse",
                CategorySynapse.class
        )
                .setLinkType(patternCategoryLink)
                .setInputSlotType(disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(CATEGORY)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(100,0,200);");

        inhibitoryCategoryLink = new LinkTypeDefinition(
                "InhibitoryCategoryLink",
                CategoryLink.class);

        inhibitoryCategorySynapse = new SynapseTypeDefinition(
                "InhibitoryCategorySynapse",
                CategorySynapse.class
        )
                .setLinkType(inhibitoryCategoryLink)
                .setInputSlotType(disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(INHIBITORY)
                .setOutputNeuronType(CATEGORY)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(NOT_FIRED)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(110,0,220);");
    }

    public ActivationTypeDefinition getLatentRelationActivation() {
        return latentRelationActivation;
    }

    public NeuronTypeDefinition getLatentRelationNeuron() {
        return latentRelationNeuron;
    }

    public ActivationTypeDefinition getPatternActivation() {
        return patternActivation;
    }

    public NeuronTypeDefinition getPatternNeuron() {
        return patternNeuron;
    }

    public ActivationTypeDefinition getPatternCategoryActivation() {
        return patternCategoryActivation;
    }

    public NeuronTypeDefinition getPatternCategoryNeuron() {
        return patternCategoryNeuron;
    }

    public ActivationTypeDefinition getInhibitoryActivation() {
        return inhibitoryActivation;
    }

    public NeuronTypeDefinition getInhibitoryNeuron() {
        return inhibitoryNeuron;
    }

    public ActivationTypeDefinition getInhibitoryCategoryActivation() {
        return inhibitoryCategoryActivation;
    }

    public NeuronTypeDefinition getInhibitoryCategoryNeuron() {
        return inhibitoryCategoryNeuron;
    }

    public SynapseSlotTypeDefinition getAnnealingSynapseOutputSlot() {
        return annealingSynapseOutputSlot;
    }

    public SynapseSlotTypeDefinition getOuterFeedbackAnnealingSynapseOutputSlot() {
        return outerFeedbackAnnealingSynapseOutputSlot;
    }

    public SynapseSlotTypeDefinition getCategoryInputAnnealingSynapseOutputSlot() {
        return categoryInputAnnealingSynapseOutputSlot;
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

    public LinkTypeDefinition getBindingCategoryInputLink() {
        return bindingCategoryInputLink;
    }

    public SynapseTypeDefinition getBindingCategoryInputSynapse() {
        return bindingCategoryInputSynapse;
    }

    public LinkTypeDefinition getPatternLink() {
        return patternLink;
    }

    public SynapseTypeDefinition getPatternSynapse() {
        return patternSynapse;
    }

    public LinkTypeDefinition getPatternCategoryInputLink() {
        return patternCategoryInputLink;
    }

    public SynapseTypeDefinition getPatternCategoryInputSynapse() {
        return patternCategoryInputSynapse;
    }

    public LinkTypeDefinition getInhibitoryLink() {
        return inhibitoryLink;
    }

    public SynapseTypeDefinition getInhibitorySynapse() {
        return inhibitorySynapse;
    }

    public LinkTypeDefinition getPrimaryInhibitoryLink() {
        return primaryInhibitoryLink;
    }

    public SynapseTypeDefinition getPrimaryInhibitorySynapse() {
        return primaryInhibitorySynapse;
    }

    public LinkTypeDefinition getInhibitoryCategoryInputLink() {
        return inhibitoryCategoryInputLink;
    }

    public SynapseTypeDefinition getInhibitoryCategoryInputSynapse() {
        return inhibitoryCategoryInputSynapse;
    }

    public LinkTypeDefinition getBindingCategoryLink() {
        return bindingCategoryLink;
    }

    public SynapseTypeDefinition getBindingCategorySynapse() {
        return bindingCategorySynapse;
    }

    public LinkTypeDefinition getPatternCategoryLink() {
        return patternCategoryLink;
    }

    public SynapseTypeDefinition getPatternCategorySynapse() {
        return patternCategorySynapse;
    }

    public LinkTypeDefinition getInhibitoryCategoryLink() {
        return inhibitoryCategoryLink;
    }

    public SynapseTypeDefinition getInhibitoryCategorySynapse() {
        return inhibitoryCategorySynapse;
    }

}