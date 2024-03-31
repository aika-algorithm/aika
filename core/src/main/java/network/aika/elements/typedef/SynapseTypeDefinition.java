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

import network.aika.elements.NeuronType;
import network.aika.elements.activations.StateType;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Trigger;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseTypeDefinition extends TypeDefinition<Synapse> {

    private LinkTypeDefinition linkType;

    private NeuronType inputNeuronType;

    private SynapseSlotTypeDefinition inputSlotType;

    private NeuronType outputNeuronType;

    private SynapseSlotTypeDefinition outputSlotType;

    private Transition[] transition;

    private Transition required;

    private Trigger trigger;
    
    private StateType outputState;

    private boolean propagateRange;

    private Direction storedAt;

    private boolean trainingAllowed;

    private String debugStyle;


    public SynapseTypeDefinition(String name, Class<? extends Synapse> clazz) {
        super(name, clazz);
    }

    public SynapseSlotTypeDefinition getInputSlotType() {
        return inputSlotType;
    }

    public SynapseTypeDefinition setInputSlotType(SynapseSlotTypeDefinition inputSlotType) {
        this.inputSlotType = inputSlotType;

        return this;
    }

    public SynapseSlotTypeDefinition getOutputSlotType() {
        return outputSlotType;
    }

    public SynapseTypeDefinition setOutputSlotType(SynapseSlotTypeDefinition outputSlotType) {
        this.outputSlotType = outputSlotType;

        return this;
    }

    public SynapseTypeDefinition setLinkType(LinkTypeDefinition linkType) {
        this.linkType = linkType;

        return this;
    }

    public SynapseTypeDefinition setInputNeuronType(NeuronType inputNeuronType) {
        this.inputNeuronType = inputNeuronType;

        return this;
    }

    public SynapseTypeDefinition setOutputNeuronType(NeuronType outputNeuronType) {
        this.outputNeuronType = outputNeuronType;

        return this;
    }

    public SynapseTypeDefinition setTransition(Transition... transition) {
        this.transition = transition;

        return this;
    }

    public SynapseTypeDefinition setRequired(Transition required) {
        this.required = required;

        return this;
    }

    public SynapseTypeDefinition setTrigger(Trigger trigger) {
        this.trigger = trigger;

        return this;
    }

    public SynapseTypeDefinition setOutputState(StateType outputState) {
        this.outputState = outputState;

        return this;
    }

    public SynapseTypeDefinition setPropagateRange(boolean propagateRange) {
        this.propagateRange = propagateRange;

        return this;
    }

    public SynapseTypeDefinition setStoredAt(Direction storedAt) {
        this.storedAt = storedAt;

        return this;
    }

    public SynapseTypeDefinition setTrainingAllowed(boolean trainingAllowed) {
        this.trainingAllowed = trainingAllowed;

        return this;
    }

    public LinkTypeDefinition getLinkType() {
        return linkType;
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

    public SynapseTypeDefinition setDebugStyle(String c) {
        debugStyle = c;
        return this;
    }

    public String getDebugStyle() {
        return debugStyle;
    }

    public Synapse instantiate() {
        try {
            Synapse instance = clazz.getConstructor().newInstance();
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
