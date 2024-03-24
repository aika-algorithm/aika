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
import network.aika.elements.NeuronType;
import network.aika.elements.activations.StateType;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.Trigger;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseTypeDefinition<T extends Type> extends TypeDefinition<T> {
    private NeuronType inputNeuronType;

    private NeuronType outputNeuronType;

    private Transition[] transition;

    private Transition required;

    private Trigger trigger;
    
    private StateType outputState;

    private boolean propagateRange;

    private Direction storedAt;

    private boolean trainingAllowed;

    public static SynapseTypeDefinition getDefinition(Class clazz) {
        return cache.computeIfAbsent(clazz, c ->
                new SynapseTypeDefinition(
                        c.getAnnotation(SynapseType.class),
                        clazz.getSimpleName(),
                        clazz
                )
        );
    }

    private static HashMap<Class<Synapse>, SynapseTypeDefinition> cache = new HashMap();

    private SynapseTypeDefinition(SynapseType synTypeAnno, String name, Class<T> clazz) {
        super(name, clazz);
        inputNeuronType = synTypeAnno.inputType();
        outputNeuronType = synTypeAnno.outputType();
        transition = synTypeAnno.transition();
        required = synTypeAnno.required();
        trigger = synTypeAnno.trigger();
        outputState = synTypeAnno.outputState();
        propagateRange = synTypeAnno.propagateRange();
        storedAt = synTypeAnno.storedAt().getDir();
    }

    public NeuronType getInputType() {
        return inputNeuronType;
    }

    public NeuronType getOutputType() {
        return outputNeuronType;
    }

    public Transition[] getTransition() {
        return transition;
    }

    public Transition getRequired() {
        return required;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public StateType outputState() {
        return outputState;
    }

    public boolean isPropagateRange() {
        return propagateRange;
    }

    public Direction getStoredAt() {
        return storedAt;
    }

    public boolean isTrainingAllowed() {
        return trainingAllowed;
    }

    @Override
    public T instantiate(Model m) {
        try {
            T instance = clazz.getConstructor().newInstance();
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
