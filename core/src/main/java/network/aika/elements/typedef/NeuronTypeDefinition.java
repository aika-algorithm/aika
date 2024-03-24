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
import network.aika.elements.activations.bsslots.BSSlotDefinition;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.RefType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronTypeDefinition<T extends Type> extends TypeDefinition<T> {

    private RefType refType;

    private NeuronType neuronType;

    private ActivationFunction activationFunction;

    private BSSlotDefinition[] bindingSignalSlots;

    private boolean trainingAllowed;

    public static NeuronTypeDefinition getDefinition(Class clazz) {
        return cache.computeIfAbsent(clazz, c ->
                new NeuronTypeDefinition(
                        c.getAnnotation(network.aika.elements.neurons.NeuronType.class),
                        clazz.getSimpleName(),
                        clazz
                )
        );
    }

    private static HashMap<Class<Neuron>, NeuronTypeDefinition> cache = new HashMap();

    private NeuronTypeDefinition(network.aika.elements.neurons.NeuronType typeAnnotation, String name, Class<T> clazz) {
        super(name, clazz);
        this.neuronType = typeAnnotation.type();
        this.activationFunction = typeAnnotation.activationFunction();
        this.bindingSignalSlots = typeAnnotation.bindingSignalSlots();
        this.trainingAllowed = typeAnnotation.trainingAllowed();
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

    public T instantiate(Model m) {
        try {
            T instance = clazz
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
