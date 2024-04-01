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
package network.aika.elements.typedef;


import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.DisjunctiveActivation;
import network.aika.elements.activations.StateType;
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
import network.aika.elements.synapses.types.NegativeFeedbackSynapse;
import network.aika.elements.synapses.types.RelationInputSynapse;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.SumField;

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
import static network.aika.fielddefs.FieldLinkDefinition.link;
import static network.aika.fielddefs.Operators.func;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class TypeModel {

    private ActivationTypeDefinition bindingActivation;
    private NeuronTypeDefinition bindingNeuron;
    private ActivationTypeDefinition bindingCategoryActivation;
    private NeuronTypeDefinition bindingCategoryNeuron;
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

    public void initTypeModel() {

        initNeuronsAndActivations();


        initSynapsesAndLinks();
    }

    private StateTypeDefinition initStateType(String name, StateType stateType) {
        StateTypeDefinition state = new StateTypeDefinition(name, stateType)
                .setNextRound(stateType == PRE_FEEDBACK);

        FieldDefinition net = new FieldDefinition(SumField.class, state, "net");
        state.addFieldDefinition(net);
/*
        net.addListener("onFired", (fl, u) ->
                updateFiredStep(fl)
        );
*/
        FieldDefinition value = func(
                this,
                "value = f(net)",
                TOLERANCE,
                net,
                x -> act.getActivationFunction().f(x)
        );
        value.setQueued(preFeedbackState, INFERENCE);
        return preFeedbackState;
    }

    private void initNeuronsAndActivations() {
        StateTypeDefinition preFeedbackState = initStateType("PreFeedbackState", PRE_FEEDBACK);
        StateTypeDefinition outerFeedbackState = initStateType("OuterFeedbackState", OUTER_FEEDBACK);
        StateTypeDefinition innerFeedbackState = initStateType("InnerFeedbackState", INNER_FEEDBACK);

        link(
                preFeedbackState.getFieldDef("net"),
                outerFeedbackState.getFieldDef("net")
        );

        link(
                outerFeedbackState.getFieldDef("net"),
                innerFeedbackState.getFieldDef("net")
        );

        bindingActivation = new ActivationTypeDefinition(
                "BindingActivation",
                ConjunctiveActivation.class
        )
                .addStateType(preFeedbackState);

        bindingNeuron = new NeuronTypeDefinition(
                "BindingNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(BINDING)
                .setActivationType(bindingActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME_FEEDBACK)
                .setDebugStyle("fill-color: rgb(0,205,0);");

        bindingCategoryActivation = new ActivationTypeDefinition(
                "BindingCategoryActivation",
                CategoryActivation.class
        )
                .addStateType(preFeedbackState);

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
                .addStateType(preFeedbackState);

        latentRelationNeuron = new NeuronTypeDefinition(
                "LatentRelationNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(BINDING)
                .setActivationType(latentRelationActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots()
                .setDebugStyle("fill-color: rgb(10,170,0);");


        patternActivation = new ActivationTypeDefinition(
                "PatternActivation",
                ConjunctiveActivation.class
        )
                .addStateType(preFeedbackState);

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
                .addStateType(preFeedbackState);

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
                .addStateType(preFeedbackState);

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
                .addStateType(preFeedbackState);

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

    private void initSynapsesAndLinks() {
        inputObjectLink = new LinkTypeDefinition(
                "InputObjectLink",
                ConjunctiveLink.class);

        inputObjectSynapse = new SynapseTypeDefinition(
                "InputObjectSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(inputObjectLink)
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(BINDING)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(0,150,00);");


        sameObjectLink = new LinkTypeDefinition(
                "SameObjectLink",
                ConjunctiveLink.class);

        sameObjectSynapse = new SynapseTypeDefinition(
                "SameObjectSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(sameObjectLink)
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
                .setInputNeuronType(INHIBITORY)
                .setOutputNeuronType(CATEGORY)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(NOT_FIRED)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(110,0,220);");
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
