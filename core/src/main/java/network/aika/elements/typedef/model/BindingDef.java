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
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.typedef.ActivationTypeDefinition;
import network.aika.elements.typedef.NeuronTypeDefinition;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.BINDING;
import static network.aika.elements.NeuronType.CATEGORY;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.*;

/**
 *
 * @author Lukas Molzberger
 */
public class BindingDef {

    private TypeModel typeModel;

    private ActivationTypeDefinition bindingActivation;
    private NeuronTypeDefinition bindingNeuron;

    private ActivationTypeDefinition bindingCategoryActivation;
    private NeuronTypeDefinition bindingCategoryNeuron;

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


}
