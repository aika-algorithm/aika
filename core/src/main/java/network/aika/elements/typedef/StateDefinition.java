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

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.State;
import network.aika.elements.activations.StateType;
import network.aika.elements.neurons.Neuron;
import network.aika.fielddefs.ObjectRelationDefinition;
import network.aika.fielddefs.Type;
import network.aika.fielddefs.ObjectPath;
import network.aika.fielddefs.TypeRegistry;

import java.util.List;
import java.util.Set;

import static network.aika.fielddefs.ObjectRelationDefinition.single;
import static network.aika.fielddefs.ObjectRelationType.ONE_TO_MANY;
import static network.aika.fielddefs.ObjectRelationType.ONE_TO_ONE;

/**
 *
 * @author Lukas Molzberger
 */
public class StateDefinition extends Type<StateDefinition, State> {

    private StateType stateType;

    private boolean isNextRound;

    private ActivationDefinition activation;
    ObjectRelationDefinition<StateDefinition, State, ActivationDefinition, Activation> activationRelation;


    public StateDefinition(TypeRegistry registry, String name, StateType stateType) {
        super(registry, name, State.class);

        this.stateType = stateType;
    }

    public State instantiate(Activation act) {
        return instantiate(
                List.of(StateDefinition.class, Activation.class),
                List.of(this, act)
        );
    }

    public StateType getStateType() {
        return stateType;
    }

    public StateDefinition setActivation(ActivationDefinition activation) {
        assert activation != null;

        this.activation = activation;

        activationRelation = new ObjectRelationDefinition<>(
                this,
                activation,
                ONE_TO_ONE,
                s -> single(s.getActivation()),
                activation.stateRelations.get(stateType)
        );

        return this;
    }

    public ActivationDefinition getActivation() {
        return activation;
    }

    public ActivationDefinition getActivation(ObjectPath p) {
        p.add(activationRelation);
        return activation;
    }

    public StateDefinition setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    @Override
    public void dumpTypeDetails(StringBuilder sb) {
        sb.append("  isNextRound: " + isNextRound + "\n");
    }
}
