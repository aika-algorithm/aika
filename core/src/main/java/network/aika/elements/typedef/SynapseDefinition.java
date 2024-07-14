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
import network.aika.fielddefs.ObjectPath;

import java.util.Set;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseDefinition extends TypeDefinition<SynapseDefinition, Synapse> {

    private LinkDefinition link;

    private NeuronDefinition input;

    private NeuronDefinition output;

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


    public NeuronDefinition getInput(ObjectPath p) {
        addPathEntry(p, input, s -> Set.of(s.getInput()));
        return input;
    }

    public NeuronDefinition getOutput(ObjectPath p) {
        addPathEntry(p, output, s -> Set.of(s.getOutput()));
        return output;
    }

    public FieldDefinition<SynapseDefinition> getWeight() {
        return weight;
    }

    public void setWeight(FieldDefinition<SynapseDefinition> weight) {
        this.weight = weight;
    }

    public SynapseDefinition setLink(LinkDefinition link) {
        this.link = link;

        return this;
    }

    public NeuronDefinition getInput() {
        return input;
    }

    public SynapseDefinition setInput(NeuronDefinition input) {
        this.input = input;

        return this;
    }

    public NeuronDefinition getOutput() {
        return output;
    }

    public SynapseDefinition setOutput(NeuronDefinition outputDef) {
        this.output = outputDef;

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

    public LinkDefinition getLink() {
        return link;
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
}
