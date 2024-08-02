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

import network.aika.Document;
import network.aika.elements.NeuronType;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.StateType;
import network.aika.elements.neurons.Neuron;
import network.aika.fielddefs.ObjectPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Lukas Molzberger
 */
public class ActivationDefinition extends TypeDefinition<ActivationDefinition, Activation> {

    private NeuronDefinition neuron;

    private List<StateDefinition> states = new ArrayList<>();

    public ActivationDefinition(String name, Class<? extends Activation> clazz) {
        super(name, clazz);
    }

    ActivationDefinition setNeuron(NeuronDefinition neuron) {
        assert neuron != null;

        this.neuron = neuron;

        return this;
    }

    public Activation instantiate(int actId, Document doc, Neuron neuron) {
        return instantiate(
                List.of(Integer.class, Document.class, Neuron.class),
                List.of(actId, doc, neuron)
        );
    }

    public NeuronType getType() {
        return neuron.getType();
    }

    public StateDefinition[] getStates() {
        return states.toArray(new StateDefinition[0]);
    }

    public ActivationDefinition addStateType(StateDefinition state) {
        assert state != null;

        states.add(state);
        state.setActivation(this);

        return this;
    }

    public NeuronDefinition getNeuron() {
        return neuron;
    }

    public NeuronDefinition getNeuron(ObjectPath p) {
        addPathEntry(p, neuron, act -> Set.of(act.getNeuron()));
        return neuron;
    }

    public StateDefinition getState(StateType stateType) {
        return states.get(stateType.ordinal());
    }

    public StateDefinition getState(ObjectPath p, StateType stateType) {
        StateDefinition s = getState(stateType);
        addPathEntry(p, s, act -> Set.of(act.getState(stateType)));
        return s;
    }
}
