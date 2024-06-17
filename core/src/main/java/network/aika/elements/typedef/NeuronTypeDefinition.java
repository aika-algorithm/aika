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

import network.aika.ActivationFunction;
import network.aika.Model;
import network.aika.elements.NeuronType;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.bsslots.BSSlotDefinition;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.RefType;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronTypeDefinition extends TypeDefinition<NeuronTypeDefinition, Neuron> {

    private NeuronType neuronType;

    private ActivationFunction activationFunction;

    private BSSlotDefinition[] bindingSignalSlots;

    private boolean trainingAllowed;

    public ActivationTypeDefinition activationType;


    public TemplateRelationDefinition templateRelation;

    public NeuronTypeDefinition(String name, Class<? extends Neuron> clazz) {
        super(name, clazz);
    }

    public ActivationTypeDefinition getActivationType() {
        return activationType;
    }

    public NeuronTypeDefinition setActivationType(ActivationTypeDefinition activationType) {
        this.activationType = activationType;
        this.activationType.setNeuronType(this);
        return this;
    }

    public NeuronTypeDefinition setNeuronType(NeuronType neuronType) {
        this.neuronType = neuronType;

        return this;
    }

    public NeuronTypeDefinition setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;

        return this;
    }

    public NeuronTypeDefinition setBindingSignalSlots(BSSlotDefinition... bindingSignalSlots) {
        this.bindingSignalSlots = bindingSignalSlots;

        return this;
    }

    public NeuronTypeDefinition setTrainingAllowed(boolean trainingAllowed) {
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

    public NeuronTypeDefinition setTemplateRelation(TemplateRelationDefinition templateRelation) {
        this.templateRelation = templateRelation;
        return this;
    }

    public Neuron instantiate(Model m, RefType refType) {
        try {
            Neuron instance = clazz
                    .getConstructor(Model.class, RefType.class)
                    .newInstance(m, refType);

            instance.setTypeDefinition(this);
            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
