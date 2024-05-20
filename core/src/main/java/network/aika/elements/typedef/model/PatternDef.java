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

import network.aika.elements.activations.Activation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.*;
import network.aika.fielddefs.FieldDefinition;
import network.aika.statistic.AverageCoveredSpace;
import network.aika.statistic.NeuronStatistic;

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
import static network.aika.fielddefs.FieldLinkDefinition.link;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternDef implements TypeDefinition {

    private TypeModel typeModel;

    private ActivationTypeDefinition activation;
    private NeuronTypeDefinition neuron;

    private ActivationTypeDefinition categoryActivation;
    private NeuronTypeDefinition categoryNeuron;

    private LinkTypeDefinition link;
    private SynapseTypeDefinition synapse;
    private LinkTypeDefinition categoryInputLink;
    private SynapseTypeDefinition categoryInputSynapse;

    private LinkTypeDefinition categoryLink;
    private SynapseTypeDefinition categorySynapse;


    FieldDefinition<Neuron, AverageCoveredSpace> averageCoveredSpace;

    FieldDefinition<Neuron, NeuronStatistic> neuronStatistic;


    public PatternDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }


    public void init() {

        activation = new ActivationTypeDefinition(
                "PatternActivation",
                Activation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        neuron = new NeuronTypeDefinition(
                "PatternNeuron",
                Neuron.class
        )
                .setNeuronType(PATTERN)
                .setActivationType(activation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_SAME, MULTI_INPUT)
                .setDebugStyle("fill-color: rgb(224, 34, 245);");


        averageCoveredSpace = new FieldDefinition(AverageCoveredSpace.class, neuron, "avgCoveredSpace");

        /*
            private NeuronStatistic statistic = new NeuronStatistic(
            this,
            "statistic",
            getConfig().getAlpha(),
            TOLERANCE
    );
        */

        neuronStatistic = new FieldDefinition(NeuronStatistic.class, neuron, "statistic", TOLERANCE);

        link(averageCoveredSpace, neuronStatistic);


        categoryActivation = new ActivationTypeDefinition(
                "PatternCategoryActivation",
                Activation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        categoryNeuron = new NeuronTypeDefinition(
                "PatternCategoryNeuron",
                Neuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(categoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_SAME)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,0,200);");



        link = new LinkTypeDefinition(
                "PatternLink",
                ConjunctiveLink.class
        )
                .setInputDef(typeModel.bindingDef.getActivation())
                .setOutputDef(typeModel.patternDef.activation);

        synapse = new SynapseTypeDefinition(
                "PatternSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(link)
                .setInputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.conjunctiveDef.getConjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(PATTERN)
                .setTransition(SAME_SAME, INPUT_INPUT)
                .setRequired(SAME_SAME)
                .setTrigger(PRIMARY_CHECKED_FIRED_OUTER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(224, 34, 245);");

        categoryLink = new LinkTypeDefinition(
                "PatternCategoryLink",
                Link.class
        )
                .setInputDef(activation)
                .setOutputDef(categoryActivation);

        categorySynapse = new SynapseTypeDefinition(
                "PatternCategorySynapse",
                Synapse.class
        )
                .setLinkType(categoryLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(CATEGORY)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(100,0,200);");


        categoryInputLink = new LinkTypeDefinition(
                "PatternCategoryInputLink",
                ConjunctiveLink.class
        )
                .setInputDef(categoryActivation)
                .setOutputDef(activation);

        categoryInputSynapse = new SynapseTypeDefinition(
                "PatternCategoryInputSynapse",
                ConjunctiveSynapse.class
        )
                .setLinkType(categoryInputLink)
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
                .setInstanceSynapseType(categorySynapse)
                .setDebugStyle("fill-color: rgb(110,200,220);");

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
