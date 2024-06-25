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

import network.aika.elements.activations.StateType;
import network.aika.elements.activations.bsslots.RegisterInputSlot;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Trigger;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.Path;
import network.aika.fields.SumField;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseDefinition extends TypeDefinition<SynapseDefinition, Synapse> {

    private LinkDefinition linkType;

    private NeuronDefinition inputDef;

    private SynapseSlotDefinition inputSlotType;

    private NeuronDefinition outputDef;

    private SynapseSlotDefinition outputSlotType;

    private Transition[] transition;

    private Transition required;

    private Trigger trigger;
    
    private StateType outputState;

    private boolean propagateRange;

    private Direction storedAt;

    private boolean trainingAllowed;

    private RegisterInputSlot registerInputSlot = RegisterInputSlot.ON_LINKING;

    private SynapseDefinition instanceSynapseType;

    private FieldDefinition<SynapseDefinition> weight;

    public SynapseDefinition(String name, Class<? extends Synapse> clazz) {
        super(name, clazz);
    }


    public NeuronDefinition getInput(Path p) {
        p.add(inputDef);
        return inputDef;
    }

    public NeuronDefinition getOutput(Path p) {
        p.add(outputDef);
        return outputDef;
    }

    public FieldDefinition<SynapseDefinition> getWeight() {
        return weight;
    }

    public void setWeight(FieldDefinition<SynapseDefinition> weight) {
        this.weight = weight;
    }

    public SynapseSlotDefinition getInputSlotType() {
        return inputSlotType;
    }

    public SynapseDefinition setInputSlotType(SynapseSlotDefinition inputSlotType) {
        this.inputSlotType = inputSlotType;

        return this;
    }

    public SynapseSlotDefinition getOutputSlotType() {
        return outputSlotType;
    }

    public SynapseDefinition setOutputSlotType(SynapseSlotDefinition outputSlotType) {
        this.outputSlotType = outputSlotType;

        return this;
    }

    public SynapseDefinition setLinkType(LinkDefinition linkType) {
        this.linkType = linkType;

        return this;
    }

    public NeuronDefinition getInputDef() {
        return inputDef;
    }

    public SynapseDefinition setInputNeuronType(NeuronDefinition inputDef) {
        this.inputDef = inputDef;

        return this;
    }

    public NeuronDefinition getOutputDef() {
        return outputDef;
    }

    public SynapseDefinition setOutputNeuronType(NeuronDefinition outputDef) {
        this.outputDef = outputDef;

        return this;
    }

    public SynapseDefinition setTransition(Transition... transition) {
        this.transition = transition;

        return this;
    }

    public SynapseDefinition setRequired(Transition required) {
        this.required = required;

        return this;
    }

    public SynapseDefinition setTrigger(Trigger trigger) {
        this.trigger = trigger;

        return this;
    }

    public SynapseDefinition setOutputState(StateType outputState) {
        this.outputState = outputState;

        return this;
    }

    public SynapseDefinition setPropagateRange(boolean propagateRange) {
        this.propagateRange = propagateRange;

        return this;
    }

    public SynapseDefinition setStoredAt(Direction storedAt) {
        this.storedAt = storedAt;

        return this;
    }

    public SynapseDefinition setTrainingAllowed(boolean trainingAllowed) {
        this.trainingAllowed = trainingAllowed;

        return this;
    }

    public LinkDefinition getLinkType() {
        return linkType;
    }

    public LinkDefinition getLinkType(Path p) {
        p.add(linkType);
        return linkType;
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

    public RegisterInputSlot getRegisterInputSlot() {
        return registerInputSlot;
    }

    public SynapseDefinition setRegisterInputSlot(RegisterInputSlot registerInputSlot) {
        this.registerInputSlot = registerInputSlot;

        return this;
    }

    public SynapseDefinition getInstanceSynapseType() {
        return instanceSynapseType;
    }

    public SynapseDefinition setInstanceSynapseType(SynapseDefinition instanceSynapseType) {
        this.instanceSynapseType = instanceSynapseType;

        return this;
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
