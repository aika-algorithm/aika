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
import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.Path;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lukas Molzberger
 */
public class ActivationDefinition extends TypeDefinition<ActivationDefinition, Activation> {

    private NeuronDefinition neuron;

    private List<StateDefinition> states = new ArrayList<>();

    private FieldDefinition<LinkDefinition> updateValue;

    public ActivationDefinition(String name, Class<? extends Activation> clazz) {
        super(name, clazz);
    }

    public ActivationDefinition setNeuron(NeuronDefinition neuron) {
        this.neuron = neuron;

        return this;
    }

    public FieldDefinition<LinkDefinition> getUpdateValue() {
        return updateValue;
    }

    public void setUpdateValue(FieldDefinition<LinkDefinition> updateValue) {
        this.updateValue = updateValue;
    }

    public NeuronType getType() {
        return neuron.getType();
    }

    public StateDefinition[] getStates() {
        return states.toArray(new StateDefinition[0]);
    }

    public ActivationDefinition addStateType(StateDefinition state) {
        states.add(state);
        state.setActivation(this);

        return this;
    }

    public StateDefinition getState(StateType stateType) {
        return states.get(stateType.ordinal());
    }

    public StateDefinition getState(Path p, StateType stateType) {
        StateDefinition s = getState(stateType);
        p.add(s);
        return s;
    }

    public FieldDefinition getNet(StateType stateType) {
        return states.get(stateType.ordinal()).getNet();
    }

    public Activation instantiate(int id, Document doc, Neuron n) {
        try {
            Activation instance = clazz
                    .getConstructor(Integer.class, Document.class, Neuron.class)
                    .newInstance(id, doc, n);

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