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
import network.aika.fielddefs.inputs.ArgInputs;
import network.aika.statistic.AverageCoveredSpace;
import network.aika.statistic.NeuronStatistic;

import static network.aika.elements.typedef.FieldTags.*;
import static network.aika.fielddefs.inputs.ArgInputs.argLink;
import static network.aika.fielddefs.inputs.VariableInputs.varLink;
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
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternDef extends TypeDefinitionBase {

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


    FieldDefinition<NeuronDefinition, Neuron> neuronAverageCoveredSpace;
    FieldDefinition<NeuronDefinition, Neuron> neuronStatistic;

    FieldDefinition<SynapseDefinition, Synapse> synapseAverageCoveredSpace;


    public PatternDef(TypeModel typeModel, ConjunctiveDef superType) {
        super(typeModel, superType);
    }

    public void initNodes(Config conf) {

        activation = new ActivationDefinition(
                getTypeModel(),
                "PatternActivation",
                Activation.class
        )
                .addStateType(typeModel.neuron.getNonFeedbackState());

        FieldDefinition<ActivationDefinition, Activation> g = mul(activation, ACT_GRADIENT)
                .in(activation.getFieldOutput(GRADIENT), argLink(0))
                .in(activation.getFieldOutput(NET_OUTER_GRADIENT), argLink(1));

        scale(
                activation,
                UPDATE_VALUE,
                conf.getLearnRate(false/*neuron.isAbstract()*/)
        )
                .in(g.getFieldOutput(), argLink(0));


        neuron = new NeuronDefinition(
                getTypeModel(),
                "PatternNeuron",
                Neuron.class
        )
                .setNeuronType(PATTERN)
                .setActivation(activation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_SAME, MULTI_INPUT);


        neuronAverageCoveredSpace = new FieldDefinition(AverageCoveredSpace.class, new ArgInputs(), neuron, AVERAGE_COVERED_SPACE);

        /*
            private NeuronStatistic statistic = new NeuronStatistic(
            this,
            "statistic",
            getConfig().getAlpha(),
            TOLERANCE
    );
        */

        neuronStatistic = new FieldDefinition(NeuronStatistic.class, new ArgInputs(), neuron, STATISTIC, TOLERANCE);

        neuronAverageCoveredSpace
                .out((o, p) -> neuronStatistic, argLink(0));

        categoryActivation = new ActivationDefinition(
                getTypeModel(),
                "PatternCategoryActivation",
                Activation.class
        )
                .addParent(typeModel.category.getActivation())
                .addStateType(typeModel.neuron.getNonFeedbackState());

        categoryNeuron = new NeuronDefinition(
                getTypeModel(),
                "PatternCategoryNeuron",
                Neuron.class
        )
                .addParent(typeModel.category.getNeuron())
                .setNeuronType(CATEGORY)
                .setActivation(categoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_SAME)
                .setTrainingAllowed(false);
    }


    public void initRelations(Config conf) {
        link = new LinkDefinition(
                getTypeModel(),
                "PatternLink",
                ConjunctiveLink.class
        )
                .setInput(typeModel.binding.getActivation())
                .setOutput(typeModel.pattern.activation)
                .setInputSlot(typeModel.conjunctive.getInputSlot())
                .setOutputSlot(typeModel.conjunctive.getOutputSlot());

        synapse = new SynapseDefinition(
                getTypeModel(),
                "PatternSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(link)
                .setInput(typeModel.binding.getNeuron())
                .setOutput(neuron)
                .setTransition(SAME_SAME, INPUT_INPUT)
                .setRequired(SAME_SAME)
                .setTrigger(FIRED_OUTER_FEEDBACK)
                .setStoredAt(OUTPUT);

        categoryLink = new LinkDefinition(
                getTypeModel(),
                "PatternCategoryLink",
                Link.class
        )
                .addParent(typeModel.category.getLink())
                .setInput(activation)
                .setOutput(categoryActivation)
                .setInputSlot(typeModel.disjunctive.getInputSlot())
                .setOutputSlot(typeModel.disjunctive.getOutputSlot());

        categorySynapse = new SynapseDefinition(
                getTypeModel(),
                "PatternCategorySynapse",
                Synapse.class
        )
                .addParent(typeModel.category.getSynapse())
                .setLink(categoryLink)
                .setInput(neuron)
                .setOutput(categoryNeuron)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setStoredAt(INPUT);


        categoryInputLink = new LinkDefinition(
                getTypeModel(),
                "PatternCategoryInputLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInput(categoryActivation)
                .setOutput(activation)
                .setInputSlot(superType.getInputSlot())
                .setOutputSlot(superType.getOutputSlot());

        categoryInputSynapse = new SynapseDefinition(
                getTypeModel(),
                "PatternCategoryInputSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
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

    @Override
    public TypeModel getTypeModel() {
        return superType.getTypeModel();
    }

    @Override
    public ActivationDefinition getActivation() {
        return activation;
    }

    @Override
    public NeuronDefinition getNeuron() {
        return neuron;
    }

    @Override
    public ActivationDefinition getCategoryActivation() {
        return categoryActivation;
    }

    @Override
    public NeuronDefinition getCategoryNeuron() {
        return categoryNeuron;
    }

    @Override
    public LinkDefinition getLink() {
        return link;
    }

    @Override
    public SynapseDefinition getSynapse() {
        return synapse;
    }

    @Override
    public LinkDefinition getCategoryInputLink() {
        return categoryInputLink;
    }

    @Override
    public SynapseDefinition getCategoryInputSynapse() {
        return categoryInputSynapse;
    }

    @Override
    public LinkDefinition getCategoryLink() {
        return categoryLink;
    }

    @Override
    public SynapseDefinition getCategorySynapse() {
        return categorySynapse;
    }

}
