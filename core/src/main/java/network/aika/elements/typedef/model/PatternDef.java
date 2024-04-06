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
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.ActivationTypeDefinition;
import network.aika.elements.typedef.LinkTypeDefinition;
import network.aika.elements.typedef.NeuronTypeDefinition;
import network.aika.elements.typedef.SynapseTypeDefinition;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.PRE_FEEDBACK;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.MULTI_INPUT;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_SAME;
import static network.aika.elements.activations.bsslots.RegisterInputSlot.ON_INIT;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Transition.SAME_SAME;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;
import static network.aika.enums.Trigger.PRIMARY_CHECKED_FIRED_OUTER_FEEDBACK;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternDef {

    private TypeModel typeModel;

    private ActivationTypeDefinition patternActivation;
    private NeuronTypeDefinition patternNeuron;

    private ActivationTypeDefinition patternCategoryActivation;
    private NeuronTypeDefinition patternCategoryNeuron;

    private LinkTypeDefinition patternLink;
    private SynapseTypeDefinition patternSynapse;
    private LinkTypeDefinition patternCategoryInputLink;
    private SynapseTypeDefinition patternCategoryInputSynapse;

    private LinkTypeDefinition patternCategoryLink;
    private SynapseTypeDefinition patternCategorySynapse;


    public PatternDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }


    public void init() {

        patternActivation = new ActivationTypeDefinition(
                "PatternActivation",
                ConjunctiveActivation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

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
                .addStateType(typeModel.states.getPreFeedbackState());

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



        patternLink = new LinkTypeDefinition(
                "PatternLink",
                ConjunctiveLink.class);

        patternSynapse = new SynapseTypeDefinition(
                "PatternSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(patternLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseOutputSlot())
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
                ConjunctiveSynapse.class
        )
                .setLinkType(patternCategoryInputLink)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.categoryDef.getCategoryInputAnnealingSynapseOutputSlot())
                .setInputNeuronType(CATEGORY)
                .setOutputNeuronType(PATTERN)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setOutputState(PRE_FEEDBACK)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setTrainingAllowed(false)
                .setRegisterInputSlot(ON_INIT)
                .setDebugStyle("fill-color: rgb(110,200,220);");

        patternCategoryLink = new LinkTypeDefinition(
                "PatternCategoryLink",
                CategoryLink.class);

        patternCategorySynapse = new SynapseTypeDefinition(
                "PatternCategorySynapse",
                CategorySynapse.class
        )
                .setLinkType(patternCategoryLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(CATEGORY)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(100,0,200);");
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

    public LinkTypeDefinition getPatternCategoryLink() {
        return patternCategoryLink;
    }

    public SynapseTypeDefinition getPatternCategorySynapse() {
        return patternCategorySynapse;
    }

}
