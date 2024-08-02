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

import network.aika.Model;
import network.aika.fields.ActivationFunction;
import network.aika.elements.NeuronType;
import network.aika.elements.activations.bsslots.BSSlotDefinition;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.RefType;

import java.util.List;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronDefinition extends TypeDefinition<NeuronDefinition, Neuron> {

    private NeuronType neuronType;

    private ActivationFunction activationFunction;

    private BSSlotDefinition[] bindingSignalSlots;

    private boolean trainingAllowed;

    public ActivationDefinition activationType;

    public TemplateRelationDefinition templateRelation;

    public NeuronDefinition(String name, Class<? extends Neuron> clazz) {
        super(name, clazz);
    }

    public Neuron instantiate(Model m, RefType rt) {
        return instantiate(
                List.of(Model.class, RefType.class),
                List.of(m, rt)
        );
    }

    public ActivationDefinition getActivationType() {
        return activationType;
    }

    public NeuronDefinition setActivation(ActivationDefinition activationType) {
        this.activationType = activationType;
        this.activationType.setNeuron(this);
        return this;
    }

    public NeuronDefinition setNeuronType(NeuronType neuronType) {
        assert neuronType != null;

        this.neuronType = neuronType;

        return this;
    }

    public NeuronDefinition setActivationFunction(ActivationFunction activationFunction) {
        assert activationFunction != null;

        this.activationFunction = activationFunction;

        return this;
    }

    public NeuronDefinition setBindingSignalSlots(BSSlotDefinition... bindingSignalSlots) {
        this.bindingSignalSlots = bindingSignalSlots;

        return this;
    }

    public NeuronDefinition setTrainingAllowed(boolean trainingAllowed) {
        this.trainingAllowed = trainingAllowed;

        return this;
    }

    public NeuronType getType() {
        return neuronType;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public BSSlotDefinition[] getBindingSignalSlots() {
        return bindingSignalSlots;
    }

    public boolean isTrainingAllowed() {
        return trainingAllowed;
    }

    public TemplateRelationDefinition getTemplateRelation() {
        return templateRelation;
    }

    public NeuronDefinition setTemplateRelation(TemplateRelationDefinition templateRelation) {
        this.templateRelation = templateRelation;
        return this;
    }
}
