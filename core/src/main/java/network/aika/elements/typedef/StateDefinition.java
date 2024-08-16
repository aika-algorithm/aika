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
import network.aika.fielddefs.Type;
import network.aika.fielddefs.ObjectPath;
import network.aika.fielddefs.TypeRegistry;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Lukas Molzberger
 */
public class StateDefinition extends Type<StateDefinition, State> {

    private StateType stateType;

    private boolean isNextRound;

    private ActivationDefinition activation;

    public StateDefinition(TypeRegistry registry, String name, StateType stateType) {
        super(registry, name, State.class);

        this.stateType = stateType;
    }

    @Override
    public void dumpTypeDetails(StringBuilder sb) {
        sb.append("  isNextRound: " + isNextRound + "\n");
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
        return this;
    }

    public ActivationDefinition getActivation() {
        return activation;
    }

    public ActivationDefinition getActivation(ObjectPath p) {
        addPathEntry(p, "state.activation", activation, s -> Set.of(s.getActivation()));
        return activation;
    }

    public StateDefinition setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }
}
