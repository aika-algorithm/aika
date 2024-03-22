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
import network.aika.elements.Type;
import network.aika.elements.activations.bsslots.BSSlotDefinition;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronType;

import java.util.HashMap;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronTypeDefinition {

    private Type type;

    private ActivationFunction activationFunction;

    private BSSlotDefinition[] bindingSignalSlots;

    private boolean trainingAllowed;

    public static NeuronTypeDefinition getDefinition(Class clazz) {
        return cache.computeIfAbsent(clazz, c ->
                new NeuronTypeDefinition(
                        c.getAnnotation(NeuronType.class)
                )
        );
    }

    private static HashMap<Class<Neuron>, NeuronTypeDefinition> cache = new HashMap();

    private NeuronTypeDefinition(NeuronType typeAnnotation) {
        this.type = typeAnnotation.type();
        this.activationFunction = typeAnnotation.activationFunction();
        this.bindingSignalSlots = typeAnnotation.bindingSignalSlots();
        this.trainingAllowed = typeAnnotation.trainingAllowed();
    }

    public Type getType() {
        return type;
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
}
