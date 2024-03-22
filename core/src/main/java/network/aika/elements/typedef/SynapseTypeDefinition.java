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

import network.aika.elements.Type;
import network.aika.elements.activations.StateType;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.Trigger;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;

import java.util.HashMap;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseTypeDefinition {
    private Type inputType;

    private Type outputType;

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
                        c.getAnnotation(SynapseType.class)
                )
        );
    }

    private static HashMap<Class<Synapse>, SynapseTypeDefinition> cache = new HashMap();

    private SynapseTypeDefinition(SynapseType synTypeAnno) {
        inputType = synTypeAnno.inputType();
        outputType = synTypeAnno.outputType();
        transition = synTypeAnno.transition();
        required = synTypeAnno.required();
        trigger = synTypeAnno.trigger();
        outputState = synTypeAnno.outputState();
        propagateRange = synTypeAnno.propagateRange();
        storedAt = synTypeAnno.storedAt().getDir();
    }

    public Type getInputType() {
        return inputType;
    }

    public Type getOutputType() {
        return outputType;
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
}
