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
import network.aika.elements.typedef.*;
import network.aika.fielddefs.FieldDefinition;


import static network.aika.elements.typedef.StateDefinition.NET;
import static network.aika.elements.typedef.SynapseDefinition.WEIGHT;
import static network.aika.fields.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.fields.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.*;
import static network.aika.elements.activations.StateType.OUTER_FEEDBACK;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.*;
import static network.aika.elements.activations.bsslots.RegisterInputSlot.ON_INIT;
import static network.aika.enums.Transition.*;
import static network.aika.enums.Trigger.*;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.fields.MaxField.max;
import static network.aika.fields.ScaleFunction.scale;
import static network.aika.fields.SumField.sum;
import static network.aika.model.NeuronDef.*;

/**
 *
 * @author Lukas Molzberger
 */
public class BindingDef implements TypeDefinition {


    public static final String NEGATIVE_WEIGHT = "negative weight";


    private TypeModel typeModel;

    private ConjunctiveDef superType;

    private CategoryDef categoryDef;


    StateDef outerFeedbackState = new StateDef(typeModel);
    StateDef innerFeedbackState = new StateDef(typeModel);

    private ActivationDefinition activation;
    private NeuronDefinition neuron;

    private LinkDefinition categoryInputLink;
    private SynapseDefinition categoryInputSynapse;

    private ActivationDefinition categoryActivation;
    private NeuronDefinition categoryNeuron;

    private ActivationDefinition latentRelationActivation;
    private NeuronDefinition latentRelationNeuron;

    private LinkDefinition inputObjectLink;
    private SynapseDefinition inputObjectSynapse;
    private LinkDefinition sameObjectLink;
    private SynapseDefinition sameObjectSynapse;
    private LinkDefinition innerPositiveFeedbackLink;
    private SynapseDefinition innerPositiveFeedbackSynapse;
    private LinkDefinition outerPositiveFeedbackLink;
    private SynapseDefinition outerPositiveFeedbackSynapse;
    private LinkDefinition negativeFeedbackLink;
    private SynapseDefinition negativeFeedbackSynapse;
    private LinkDefinition relationInputLink;
    private SynapseDefinition relationInputSynapse;

    private LinkDefinition categoryLink;
    private SynapseDefinition categorySynapse;


    FieldDefinition<LinkDefinition> negativeWeight;

    public BindingDef(TypeModel typeModel, ConjunctiveDef superType, CategoryDef categoryDef) {
        this.typeModel = typeModel;
        this.superType = superType;
        this.categoryDef = categoryDef;
    }

    public void initNodes() {
        outerFeedbackState.init("OuterFeedbackState", OUTER_FEEDBACK);
        innerFeedbackState.init("InnerFeedbackState", INNER_FEEDBACK);

        activation = new ActivationDefinition(
                "BindingActivation",
                Activation.class
        )
                .addParent(superType.getActivation())
                .addStateType(outerFeedbackState.state)
                .addStateType(innerFeedbackState.state);

        sum(activation, UPDATE_VALUE);

        activation.getState(NON_FEEDBACK).getField(NET)
                .out((o,p) -> outerFeedbackState.state.getField(NET));

        outerFeedbackState.state.getField(NET)
                .out((o,p) -> innerFeedbackState.state.getField(NET));


        neuron = new NeuronDefinition(
                "BindingNeuron",
                Neuron.class
        )
                .addParent(superType.getNeuron())
                .setNeuronType(BINDING)
                .setActivation(activation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME_FEEDBACK);

        categoryActivation = new ActivationDefinition(
                "BindingCategoryActivation",
                Activation.class
        )
                .addParent(categoryDef.getActivation())
                .addStateType(activation.getState(NON_FEEDBACK));

        categoryNeuron = new NeuronDefinition(
                "BindingCategoryNeuron",
                Neuron.class
        )
                .addParent(categoryDef.getNeuron())
                .setNeuronType(CATEGORY)
                .setActivation(categoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME)
                .setTrainingAllowed(false);

        latentRelationActivation = new ActivationDefinition(
                "LatentRelationActivation",
                Activation.class
        )
                .addParent(typeModel.conjunctive.getActivation())
                .addStateType(activation.getState(NON_FEEDBACK));

        latentRelationNeuron = new NeuronDefinition(
                "LatentRelationNeuron",
                Neuron.class
        )
                .addParent(superType.getNeuron())
                .setNeuronType(BINDING)
                .setActivation(latentRelationActivation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots();

    }

    public void initRelations() {
        inputObjectLink = new LinkDefinition(
                "InputObjectLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInput(typeModel.pattern.getActivation())
                .setOutput(activation)
                .setInputSlot(typeModel.conjunctive.getInputSlot())
                .setOutputSlot(typeModel.conjunctive.getOutputSlot());

        inputObjectSynapse = new SynapseDefinition(
                "InputObjectSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(inputObjectLink)
                .setInput(typeModel.pattern.getNeuron())
                .setOutput(neuron)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setStoredAt(OUTPUT);


        sameObjectLink = new LinkDefinition(
                "SameObjectLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInput(activation)
                .setOutput(activation)
                .setInputSlot(typeModel.conjunctive.getInputSlot())
                .setOutputSlot(typeModel.conjunctive.getOutputSlot());

        sameObjectSynapse = new SynapseDefinition(
                "SameObjectSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(sameObjectLink)
                .setInput(neuron)
                .setOutput(neuron)
                .setTransition(SAME_SAME)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_OUTER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setPropagateRange(false);

        innerPositiveFeedbackLink = new LinkDefinition(
                "InnerPositiveFeedbackLink",
                ConjunctiveLink.class)
                .addParent(superType.getLink())
                .setInput(typeModel.pattern.getActivation())
                .setOutput(typeModel.binding.getActivation())
                .setInputSlot(typeModel.conjunctive.getInputSlot())
                .setOutputSlot(typeModel.conjunctive.getOutputSlot());

        innerPositiveFeedbackSynapse = new SynapseDefinition(
                "InnerPositiveFeedbackSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(innerPositiveFeedbackLink)
                .setInput(typeModel.pattern.getNeuron())
                .setOutput(neuron)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setOutputState(INNER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setPropagateRange(false);


        outerPositiveFeedbackLink = new LinkDefinition(
                "OuterPositiveFeedbackLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInput(typeModel.pattern.getActivation())
                .setOutput(typeModel.binding.getActivation())
                .setInputSlot(superType.getInputSlot())
                .setOutputSlot(superType.getOutputSlot());

        outerPositiveFeedbackSynapse = new SynapseDefinition(
                "OuterPositiveFeedbackSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(outerPositiveFeedbackLink)
                .setInput(typeModel.pattern.getNeuron())
                .setOutput(neuron)
                .setTransition(SAME_INPUT)
                .setRequired(INPUT_SAME)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setOutputState(OUTER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setRegisterInputSlot(ON_INIT);

        negativeFeedbackLink = new LinkDefinition(
                "NegativeFeedbackLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInput(typeModel.inhibitory.getActivation())
                .setOutput(activation)
                .setInputSlot(superType.getInputSlot())
                .setOutputSlot(superType.getOutputSlot());

        negativeFeedbackSynapse = new SynapseDefinition(
                "NegativeFeedbackSynapse",
                ConjunctiveSynapse.class
        )
                .setLink(negativeFeedbackLink)
                .setInput(typeModel.inhibitory.getNeuron())
                .setOutput(neuron)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setOutputState(OUTER_FEEDBACK)
                .setPropagateRange(false)
                .setStoredAt(OUTPUT)
                .setRegisterInputSlot(ON_INIT);

        max(negativeFeedbackLink, INPUT_VALUE);

        negativeWeight = scale(negativeFeedbackLink, NEGATIVE_WEIGHT, -1)
                .in(0, (o, p) -> o.getSynapse(p).getFieldOutput(WEIGHT))
                .out((o, p) -> o.getOutput(p).getState(p, negativeFeedbackSynapse.outputState()).getField(NET), false);


        relationInputLink = new LinkDefinition(
                "RelationInputLink",
                ConjunctiveLink.class)
                .addParent(superType.getLink())
                .setInput(activation)
                .setOutput(activation)
                .setInputSlot(typeModel.conjunctive.getInputSlot())
                .setOutputSlot(typeModel.conjunctive.getOutputSlot());

        relationInputSynapse = new SynapseDefinition(
                "RelationInputSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(relationInputLink)
                .setInput(neuron)
                .setOutput(neuron)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setPropagateRange(false)
                .setStoredAt(OUTPUT);

        categoryLink = new LinkDefinition(
                "BindingCategoryLink",
                Link.class)
                .addParent(categoryDef.getLink())
                .setInput(typeModel.binding.getCategoryActivation())
                .setOutput(typeModel.binding.getActivation())
                .setInputSlot(typeModel.disjunctive.getInputSlot())
                .setOutputSlot(typeModel.disjunctive.getOutputSlot());

        categorySynapse = new SynapseDefinition(
                "BindingCategorySynapse",
                Synapse.class
        )
                .addParent(categoryDef.getSynapse())
                .setLink(categoryLink)
                .setInput(neuron)
                .setOutput(categoryNeuron)
                .setTransition(INPUT_INPUT, SAME_SAME)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setStoredAt(INPUT);


        categoryInputLink = new LinkDefinition(
                "BindingCategoryInputLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInputSlot(superType.getInputSlot())
                .setOutputSlot(superType.getOutputSlot());

        categoryInputSynapse = new SynapseDefinition(
                "BindingCategoryInputSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(categoryInputLink)
                .setInput(categoryNeuron)
                .setOutput(neuron)
                .setTransition(INPUT_INPUT, SAME_SAME)
                .setRequired(INPUT_INPUT)
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

    public StateDefinition getOuterFeedbackState() {
        return outerFeedbackState.state;
    }

    public StateDefinition getInnerFeedbackState() {
        return innerFeedbackState.state;
    }

    public ActivationDefinition getLatentRelationActivation() {
        return latentRelationActivation;
    }

    public NeuronDefinition getLatentRelationNeuron() {
        return latentRelationNeuron;
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


    public LinkDefinition getCategoryInputLink() {
        return categoryInputLink;
    }

    public SynapseDefinition getCategoryInputSynapse() {
        return categoryInputSynapse;
    }

    public LinkDefinition getLink() {
        return inputObjectLink;
    }

    public SynapseDefinition getSynapse() {
        return inputObjectSynapse;
    }

    public LinkDefinition getInputObjectLink() {
        return inputObjectLink;
    }

    public SynapseDefinition getInputObjectSynapse() {
        return inputObjectSynapse;
    }

    public LinkDefinition getSameObjectLink() {
        return sameObjectLink;
    }

    public SynapseDefinition getSameObjectSynapse() {
        return sameObjectSynapse;
    }

    public LinkDefinition getInnerPositiveFeedbackLink() {
        return innerPositiveFeedbackLink;
    }

    public SynapseDefinition getInnerPositiveFeedbackSynapse() {
        return innerPositiveFeedbackSynapse;
    }

    public LinkDefinition getOuterPositiveFeedbackLink() {
        return outerPositiveFeedbackLink;
    }

    public SynapseDefinition getOuterPositiveFeedbackSynapse() {
        return outerPositiveFeedbackSynapse;
    }

    public LinkDefinition getNegativeFeedbackLink() {
        return negativeFeedbackLink;
    }

    public SynapseDefinition getNegativeFeedbackSynapse() {
        return negativeFeedbackSynapse;
    }

    public LinkDefinition getRelationInputLink() {
        return relationInputLink;
    }

    public SynapseDefinition getRelationInputSynapse() {
        return relationInputSynapse;
    }


    public LinkDefinition getCategoryLink() {
        return categoryLink;
    }

    public SynapseDefinition getCategorySynapse() {
        return categorySynapse;
    }

}
