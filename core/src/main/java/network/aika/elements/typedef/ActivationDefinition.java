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
import network.aika.elements.activations.State;
import network.aika.elements.activations.StateType;
import network.aika.elements.neurons.Neuron;
import network.aika.fielddefs.*;

import java.util.*;

import static network.aika.fielddefs.ObjectRelationDefinition.single;
import static network.aika.fielddefs.ObjectRelationType.ONE_TO_MANY;
import static network.aika.fielddefs.ObjectRelationType.ONE_TO_ONE;

/**
 *
 * @author Lukas Molzberger
 */
public class ActivationDefinition extends Type<ActivationDefinition, Activation> {

    private NeuronDefinition neuron;
    ObjectRelationDefinition<ActivationDefinition, Activation, NeuronDefinition, Neuron> neuronRelation;

    private Map<StateType, StateDefinition> states = new TreeMap<>();
    Map<StateType, ObjectRelationDefinition<ActivationDefinition, Activation, StateDefinition, State>> stateRelations = new TreeMap<>();

    public ActivationDefinition(TypeRegistry registry, String name, Class<? extends Activation> clazz) {
        super(registry, name, clazz);
    }

    ActivationDefinition setNeuron(NeuronDefinition neuron) {
        assert neuron != null;

        this.neuron = neuron;

        neuronRelation = new ObjectRelationDefinition<>(
                this,
                neuron,
                ONE_TO_MANY,
                act -> single(act.getNeuron()),
                null
        );

        return this;
    }

    public Activation instantiate(int actId, Document doc, Neuron neuron) {
        return instantiate(
                List.of(ActivationDefinition.class, Integer.class, Document.class, Neuron.class),
                List.of(this, actId, doc, neuron)
        );
    }

    public NeuronType getType() {
        return neuron.getType();
    }

    public StateDefinition[] getStates() {
        return states.values().toArray(new StateDefinition[0]);
    }

    public ActivationDefinition addStateType(StateDefinition state) {
        assert state != null;

        states.put(state.getStateType(), state);

        stateRelations.put(
                state.getStateType(),
                new ObjectRelationDefinition<>(
                        this,
                        state,
                        ONE_TO_ONE,
                        act -> single(act.getState(state.getStateType())),
                        state.activationRelation
                )
        );
        state.setActivation(this);

        return this;
    }

    public NeuronDefinition getNeuron() {
        return neuron;
    }

    public NeuronDefinition getNeuron(ObjectPath p) {
        p.add(neuronRelation);
        return neuron;
    }

    public StateDefinition getState(StateType stateType) {
        StateDefinition state = states.get(stateType);
        if(state != null)
            return state;

        return getFromParent(p -> p.getState(stateType));
    }

    public ObjectRelationDefinition<ActivationDefinition, Activation, StateDefinition, State> getStateRelation(StateType stateType) {
        ObjectRelationDefinition<ActivationDefinition, Activation, StateDefinition, State> stateRelation = stateRelations.get(stateType);
        if(stateRelation != null)
            return stateRelation;

        return getFromParent(p -> p.getStateRelation(stateType));
    }

    public StateDefinition getState(ObjectPath p, StateType stateType) {
        StateDefinition s = getState(stateType);
        p.add(getStateRelation(stateType));
        return s;
    }

    @Override
    public void dumpTypeDetails(StringBuilder sb) {
        sb.append("  neuron: " + neuron.getName() + "\n");
        states.values()
                .forEach(s ->
                        sb.append("  state: " + s.getName() + "\n")
                );
    }
}
