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

import network.aika.Config;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.*;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.MultiFieldDefinition;
import network.aika.fielddefs.inputs.FixedFieldInputsDefinition;
import network.aika.statistic.AverageCoveredSpace;
import network.aika.statistic.NeuronStatistic;

import static network.aika.fields.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.fields.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.NON_FEEDBACK;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.MULTI_INPUT;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_SAME;
import static network.aika.elements.activations.bsslots.RegisterInputSlot.ON_INIT;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Transition.SAME_SAME;
import static network.aika.enums.Trigger.FIRED_OUTER_FEEDBACK;
import static network.aika.enums.Trigger.FIRED_NON_FEEDBACK;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.fields.Multiplication.mul;
import static network.aika.fields.ScaleFunction.scale;
import static network.aika.model.NeuronDef.*;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternDef implements TypeDefinition {

    private TypeModel typeModel;

    private ConjunctiveDef superType;

    private CategoryDef categoryDef;

    private ActivationDefinition activation;
    private NeuronDefinition neuron;

    private ActivationDefinition categoryActivation;
    private NeuronDefinition categoryNeuron;

    private LinkDefinition link;
    private SynapseDefinition synapse;
    private LinkDefinition categoryInputLink;
    private SynapseDefinition categoryInputSynapse;

    private LinkDefinition categoryLink;
    private SynapseDefinition categorySynapse;


    FieldDefinition<SynapseDefinition> averageCoveredSpace;

    MultiFieldDefinition<NeuronDefinition> neuronStatistic;


    public PatternDef(TypeModel typeModel, ConjunctiveDef superType, CategoryDef categoryDef) {
        this.typeModel = typeModel;
        this.superType = superType;
        this.categoryDef = categoryDef;
    }

    public void init(Config conf) {

        activation = new ActivationDefinition(
                "PatternActivation",
                Activation.class
        )
                .addStateType(typeModel.neuron.getNonFeedbackState());

        scale(
                activation,
                UPDATE_VALUE,
                conf.getLearnRate(false/*neuron.isAbstract()*/)
        ).in(0,
                mul(activation, "gradient * f'(net)")
                        .in(0, activation.getField(GRADIENT))
                        .in(1, activation.getField(NET_OUTER_GRADIENT))
        );


        neuron = new NeuronDefinition(
                "PatternNeuron",
                Neuron.class
        )
                .setNeuronType(PATTERN)
                .setActivation(activation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_SAME, MULTI_INPUT);


        averageCoveredSpace = new FieldDefinition(AverageCoveredSpace.class, new FixedFieldInputsDefinition(), neuron, "avgCoveredSpace");

        /*
            private NeuronStatistic statistic = new NeuronStatistic(
            this,
            "statistic",
            getConfig().getAlpha(),
            TOLERANCE
    );
        */

        neuronStatistic = new MultiFieldDefinition(NeuronStatistic.class, new FixedFieldInputsDefinition(), neuron, "statistic", TOLERANCE);

        averageCoveredSpace
                .out(0, (o, p) -> neuronStatistic, true);

        categoryActivation = new ActivationDefinition(
                "PatternCategoryActivation",
                Activation.class
        )
                .addStateType(typeModel.neuron.getNonFeedbackState())
                .addParent(categoryDef.getActivation());

        categoryNeuron = new NeuronDefinition(
                "PatternCategoryNeuron",
                Neuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivation(categoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_SAME)
                .setTrainingAllowed(false)
                .addParent(categoryDef.getNeuron());



        link = new LinkDefinition(
                "PatternLink",
                ConjunctiveLink.class
        )
                .setInput(typeModel.binding.getActivation())
                .setOutput(typeModel.pattern.activation)
                .setInputSlot(typeModel.conjunctive.getInputSlot())
                .setOutputSlot(typeModel.conjunctive.getOutputSlot());

        synapse = new SynapseDefinition(
                "PatternSynapse",
                ConjunctiveSynapse.class
        )
                .setLink(link)
                .setInput(typeModel.binding.getNeuron())
                .setOutput(neuron)
                .setTransition(SAME_SAME, INPUT_INPUT)
                .setRequired(SAME_SAME)
                .setTrigger(FIRED_OUTER_FEEDBACK)
                .setStoredAt(OUTPUT);

        categoryLink = new LinkDefinition(
                "PatternCategoryLink",
                Link.class
        )
                .setInput(activation)
                .setOutput(categoryActivation)
                .setInputSlot(typeModel.disjunctive.getInputSlot())
                .setOutputSlot(typeModel.disjunctive.getOutputSlot());

        categorySynapse = new SynapseDefinition(
                "PatternCategorySynapse",
                Synapse.class
        )
                .setLink(categoryLink)
                .setInput(neuron)
                .setOutput(categoryNeuron)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setStoredAt(INPUT);


        categoryInputLink = new LinkDefinition(
                "PatternCategoryInputLink",
                ConjunctiveLink.class
        )
                .setInput(categoryActivation)
                .setOutput(activation)
                .setInputSlot(superType.getInputSlot())
                .setOutputSlot(superType.getOutputSlot());

        categoryInputSynapse = new SynapseDefinition(
                "PatternCategoryInputSynapse",
                ConjunctiveSynapse.class
        )
                .setLink(categoryInputLink)
                .setInput(categoryNeuron)
                .setOutput(neuron)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setOutputState(NON_FEEDBACK)
                .setTrigger(FIRED_NON_FEEDBACK)
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
