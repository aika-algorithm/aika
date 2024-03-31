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
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.DisjunctiveNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.enums.direction.DirectionEnum;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.SumField;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.*;
import static network.aika.elements.activations.StateType.INNER_FEEDBACK;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.*;
import static network.aika.enums.Transition.*;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;
import static network.aika.enums.Trigger.PRIMARY_CHECKED_FIRED_OUTER_FEEDBACK;
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

    public void initTypeModel() {
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

        /*
                neuronTypeModifiers.put(BindingNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(0,205,0);"));
        neuronTypeModifiers.put(LatentRelationNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(10,170,0);"));
        neuronTypeModifiers.put(InhibitoryNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(100,100,255);"));
        neuronTypeModifiers.put(PatternCategoryNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(100,0,200);"));
        neuronTypeModifiers.put(BindingCategoryNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(100,0,200);"));
        neuronTypeModifiers.put(InhibitoryCategoryNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(100,0,200);"));
        neuronTypeModifiers.put(PatternNeuron.class, n -> n.setAttribute("ui.style", "fill-color: rgb(224, 34, 245);"));

         */

        ActivationTypeDefinition bindingActivation = new ActivationTypeDefinition(
                "BindingActivation",
                ConjunctiveActivation.class
        )
                .addStateType(preFeedbackState);

        NeuronTypeDefinition bindingNeuron = new NeuronTypeDefinition(
                "BindingNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(BINDING)
                .setActivationType(bindingActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME_FEEDBACK)
                .setDebugStyle("fill-color: rgb(0,205,0);");

        ActivationTypeDefinition bindingCategoryActivation = new ActivationTypeDefinition(
                "BindingCategoryActivation",
                CategoryActivation.class
        )
                .addStateType(preFeedbackState);

        NeuronTypeDefinition bindingCategoryNeuron = new NeuronTypeDefinition(
                "BindingCategoryNeuron",
                CategoryNeuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(bindingCategoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,0,200);");

        ActivationTypeDefinition patternActivation = new ActivationTypeDefinition(
                "PatternActivation",
                ConjunctiveActivation.class
        )
                .addStateType(preFeedbackState);


        ActivationTypeDefinition latentRelationActivation = new ActivationTypeDefinition(
                "LatentRelationActivation",
                ConjunctiveActivation.class
        )
                .addStateType(preFeedbackState);

        NeuronTypeDefinition latentRelationNeuron = new NeuronTypeDefinition(
                "LatentRelationNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(BINDING)
                .setActivationType(latentRelationActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots()
                .setDebugStyle("fill-color: rgb(10,170,0);");


        NeuronTypeDefinition patternNeuron = new NeuronTypeDefinition(
                "PatternNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(PATTERN)
                .setActivationType(patternActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_SAME, MULTI_INPUT)
                .setDebugStyle("fill-color: rgb(224, 34, 245);");

        ActivationTypeDefinition patternCategoryActivation = new ActivationTypeDefinition(
                "PatternCategoryActivation",
                CategoryActivation.class
        )
                .addStateType(preFeedbackState);

        NeuronTypeDefinition patternCategoryNeuron = new NeuronTypeDefinition(
                "PatternCategoryNeuron",
                CategoryNeuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(patternCategoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_SAME)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,0,200);");

        ActivationTypeDefinition inhibitoryActivation = new ActivationTypeDefinition(
                "InhibitoryActivation",
                DisjunctiveActivation.class
        )
                .addStateType(preFeedbackState);

        NeuronTypeDefinition inhibitoryNeuron = new NeuronTypeDefinition(
                "InhibitoryNeuron",
                DisjunctiveNeuron.class
        )
                .setNeuronType(INHIBITORY)
                .setActivationType(inhibitoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,100,255);");


        ActivationTypeDefinition inhibitoryCategoryActivation = new ActivationTypeDefinition(
                "InhibitoryCategoryActivation",
                CategoryActivation.class
        )
                .addStateType(preFeedbackState);

        NeuronTypeDefinition inhibitoryCategoryNeuron = new NeuronTypeDefinition(
                "InhibitoryCategoryNeuron",
                CategoryNeuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(inhibitoryCategoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,0,200);");


        /*

        synapseTypeModifiers.put(OuterPositiveFeedbackSynapse.class, "fill-color: rgb(90,200,20); arrow-shape: diamond;");
        synapseTypeModifiers.put(RelationInputSynapse.class, "fill-color: rgb(50,230,50);");
        synapseTypeModifiers.put(NegativeFeedbackSynapse.class, "fill-color: rgb(185,0,0); arrow-shape: diamond;");
        synapseTypeModifiers.put(InnerPositiveFeedbackSynapse.class, "fill-color: rgb(120,200,50); arrow-shape: diamond;");
        synapseTypeModifiers.put(InhibitorySynapse.class, "fill-color: rgb(100,100,255);");
        synapseTypeModifiers.put(PrimaryInhibitorySynapse.class, "fill-color: rgb(70,70,210);");
        synapseTypeModifiers.put(PatternCategorySynapse.class, "fill-color: rgb(100,0,200);");
        synapseTypeModifiers.put(BindingCategorySynapse.class, "fill-color: rgb(110,0,220);");
        synapseTypeModifiers.put(InhibitoryCategorySynapse.class, "fill-color: rgb(110,0,220);");
        synapseTypeModifiers.put(PatternSynapse.class, "fill-color: rgb(224, 34, 245);");
        synapseTypeModifiers.put(PatternCategoryInputSynapse.class, "fill-color: rgb(110,200,220); ");
        synapseTypeModifiers.put(BindingCategoryInputSynapse.class, "fill-color: rgb(110,200,220); ");
        synapseTypeModifiers.put(InhibitoryCategoryInputSynapse.class, "fill-color: rgb(110,200,220); ");

         */

        LinkTypeDefinition inputObjectLink = new LinkTypeDefinition(
                "InputObjectLink",
                ConjunctiveLink.class);

        SynapseTypeDefinition inputObjectSynapse = new SynapseTypeDefinition(
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


        LinkTypeDefinition sameObjectLink = new LinkTypeDefinition(
                "SameObjectLink",
                ConjunctiveLink.class);

        SynapseTypeDefinition sameObjectSynapse = new SynapseTypeDefinition(
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
}
