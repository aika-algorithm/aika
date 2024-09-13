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
import network.aika.enums.Scope;
import network.aika.fielddefs.FieldDefinition;


import static network.aika.elements.typedef.FieldTags.*;
import static network.aika.fielddefs.FieldInputDefinition.argLink;
import static network.aika.fielddefs.FieldInputDefinition.varLink;
import static network.aika.fields.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.fields.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.*;
import static network.aika.elements.activations.StateType.OUTER_FEEDBACK;
import static network.aika.elements.typedef.BSSlotDefinition.*;
import static network.aika.elements.activations.bsslots.RegisterInputSlot.ON_INIT;
import static network.aika.enums.Transition.*;
import static network.aika.enums.Trigger.*;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.fields.MaxField.max;
import static network.aika.fields.ScaleFunction.scale;
import static network.aika.fields.SumField.sum;

/**
 *
 * @author Lukas Molzberger
 */
public class BindingDef extends TypeDefinitionBase {

    StateDef nonFeedbackState;
    StateDef outerFeedbackState;
    StateDef innerFeedbackState;

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


    FieldDefinition<LinkDefinition, Link> negativeWeight;

    public BindingDef(TypeModel typeModel, ConjunctiveDef superType) {
        super(typeModel, superType);

        nonFeedbackState = new StateDef(typeModel);
        outerFeedbackState = new StateDef(getTypeModel());
        innerFeedbackState = new StateDef(getTypeModel());
    }

    public void initNodes() {
        nonFeedbackState.init("NonFeedbackState", NON_FEEDBACK);
        outerFeedbackState.init("OuterFeedbackState", OUTER_FEEDBACK);
        innerFeedbackState.init("InnerFeedbackState", INNER_FEEDBACK);

        activation = new ActivationDefinition(
                getTypeModel(),
                "BindingActivation",
                Activation.class
        )
                .addParent(superType.getActivation())
                .addStateType(nonFeedbackState.state)
                .addStateType(outerFeedbackState.state)
                .addStateType(innerFeedbackState.state);

        sum(activation, UPDATE_VALUE);

        activation.getState(NON_FEEDBACK).getField(NET)
                .out((o,p) -> o.getActivation(p).getState(p, OUTER_FEEDBACK).getFieldInput(NET), varLink());

        outerFeedbackState.state.getField(NET)
                .out((o,p) -> o.getActivation(p).getState(p, INNER_FEEDBACK).getFieldInput(NET), varLink());


        neuron = new NeuronDefinition(
                typeModel,
                "BindingNeuron",
                Neuron.class
        )
                .addParent(superType.getNeuron())
                .setNeuronType(BINDING)
                .setActivation(activation)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(
                        bsSlotDef(Scope.INPUT),
                        bsSlotDef(Scope.SAME)
                                .setFeedback(true)
                );

        categoryActivation = new ActivationDefinition(
                getTypeModel(),
                "BindingCategoryActivation",
                Activation.class
        )
                .addParent(typeModel.category.getActivation())
                .addStateType(activation.getState(NON_FEEDBACK));

        categoryNeuron = new NeuronDefinition(
                getTypeModel(),
                "BindingCategoryNeuron",
                Neuron.class
        )
                .addParent(typeModel.category.getNeuron())
                .setNeuronType(CATEGORY)
                .setActivation(categoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(
                        bsSlotDef(Scope.INPUT),
                        bsSlotDef(Scope.SAME)
                )
                .setTrainingAllowed(false);

        latentRelationActivation = new ActivationDefinition(
                getTypeModel(),
                "LatentRelationActivation",
                Activation.class
        )
                .addParent(getTypeModel().conjunctive.getActivation())
                .addStateType(activation.getState(NON_FEEDBACK));

        latentRelationNeuron = new NeuronDefinition(
                getTypeModel(),
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
                getTypeModel(),
                "InputObjectLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInput(getTypeModel().pattern.getActivation())
                .setOutput(activation)
                .setInputSlot(getTypeModel().conjunctive.getInputSlot())
                .setOutputSlot(getTypeModel().conjunctive.getOutputSlot());

        inputObjectSynapse = new SynapseDefinition(
                getTypeModel(),
                "InputObjectSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(inputObjectLink)
                .setInput(getTypeModel().pattern.getNeuron())
                .setOutput(neuron)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setStoredAt(OUTPUT);


        sameObjectLink = new LinkDefinition(
                getTypeModel(),
                "SameObjectLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInput(activation)
                .setOutput(activation)
                .setInputSlot(getTypeModel().conjunctive.getInputSlot())
                .setOutputSlot(getTypeModel().conjunctive.getOutputSlot());

        sameObjectSynapse = new SynapseDefinition(
                getTypeModel(),
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
                getTypeModel(),
                "InnerPositiveFeedbackLink",
                ConjunctiveLink.class)
                .addParent(superType.getLink())
                .setInput(getTypeModel().pattern.getActivation())
                .setOutput(getTypeModel().binding.getActivation())
                .setInputSlot(getTypeModel().conjunctive.getInputSlot())
                .setOutputSlot(getTypeModel().conjunctive.getOutputSlot());

        innerPositiveFeedbackSynapse = new SynapseDefinition(
                getTypeModel(),
                "InnerPositiveFeedbackSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(innerPositiveFeedbackLink)
                .setInput(getTypeModel().pattern.getNeuron())
                .setOutput(neuron)
                .setTransition(SAME_SAME)
                .setRequired(SAME_SAME)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setOutputState(INNER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setPropagateRange(false);


        outerPositiveFeedbackLink = new LinkDefinition(
                getTypeModel(),
                "OuterPositiveFeedbackLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInput(getTypeModel().pattern.getActivation())
                .setOutput(getTypeModel().binding.getActivation())
                .setInputSlot(superType.getInputSlot())
                .setOutputSlot(superType.getOutputSlot());

        outerPositiveFeedbackSynapse = new SynapseDefinition(
                getTypeModel(),
                "OuterPositiveFeedbackSynapse",
                ConjunctiveSynapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(outerPositiveFeedbackLink)
                .setInput(getTypeModel().pattern.getNeuron())
                .setOutput(neuron)
                .setTransition(SAME_INPUT)
                .setRequired(INPUT_SAME)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setOutputState(OUTER_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setRegisterInputSlot(ON_INIT);

        negativeFeedbackLink = new LinkDefinition(
                getTypeModel(),
                "NegativeFeedbackLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInput(getTypeModel().inhibitory.getActivation())
                .setOutput(activation)
                .setInputSlot(superType.getInputSlot())
                .setOutputSlot(superType.getOutputSlot());

        negativeFeedbackSynapse = new SynapseDefinition(
                getTypeModel(),
                "NegativeFeedbackSynapse",
                ConjunctiveSynapse.class
        )
                .setLink(negativeFeedbackLink)
                .setInput(getTypeModel().inhibitory.getNeuron())
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
                .in((o, p) -> o.getSynapse(p).getFieldOutput(WEIGHT), argLink(0))
                .out((o, p) -> o.getOutput(p).getState(p, negativeFeedbackSynapse.outputState()).getFieldInput(NET), varLink(false));


        relationInputLink = new LinkDefinition(
                getTypeModel(),
                "RelationInputLink",
                ConjunctiveLink.class)
                .addParent(superType.getLink())
                .setInput(activation)
                .setOutput(activation)
                .setInputSlot(getTypeModel().conjunctive.getInputSlot())
                .setOutputSlot(getTypeModel().conjunctive.getOutputSlot());

        relationInputSynapse = new SynapseDefinition(
                getTypeModel(),
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
                getTypeModel(),
                "BindingCategoryLink",
                Link.class)
                .addParent(typeModel.category.getLink())
                .setInput(getTypeModel().binding.getCategoryActivation())
                .setOutput(getTypeModel().binding.getActivation())
                .setInputSlot(getTypeModel().disjunctive.getInputSlot())
                .setOutputSlot(getTypeModel().disjunctive.getOutputSlot());

        categorySynapse = new SynapseDefinition(
                getTypeModel(),
                "BindingCategorySynapse",
                Synapse.class
        )
                .addParent(typeModel.category.getSynapse())
                .setLink(categoryLink)
                .setInput(neuron)
                .setOutput(categoryNeuron)
                .setTransition(INPUT_INPUT, SAME_SAME)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_NON_FEEDBACK)
                .setStoredAt(INPUT);


        categoryInputLink = new LinkDefinition(
                getTypeModel(),
                "BindingCategoryInputLink",
                ConjunctiveLink.class
        )
                .addParent(superType.getLink())
                .setInputSlot(superType.getInputSlot())
                .setOutputSlot(superType.getOutputSlot());

        categoryInputSynapse = new SynapseDefinition(
                getTypeModel(),
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
    public LinkDefinition getCategoryInputLink() {
        return categoryInputLink;
    }

    @Override
    public SynapseDefinition getCategoryInputSynapse() {
        return categoryInputSynapse;
    }

    @Override
    public LinkDefinition getLink() {
        return inputObjectLink;
    }

    @Override
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

    @Override
    public LinkDefinition getCategoryLink() {
        return categoryLink;
    }

    @Override
    public SynapseDefinition getCategorySynapse() {
        return categorySynapse;
    }

}
