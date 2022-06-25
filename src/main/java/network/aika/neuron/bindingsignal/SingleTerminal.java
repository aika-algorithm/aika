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
package network.aika.neuron.bindingsignal;

import network.aika.direction.Direction;
import network.aika.fields.FieldOutput;
import network.aika.neuron.Synapse;

import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public abstract class SingleTerminal implements Terminal {

    protected SingleTransition transition;
    protected State state;
    protected Direction type;

    public SingleTerminal(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setType(Direction type) {
        this.type = type;
    }

    public Direction getType() {
        return type;
    }

    public void setTransition(SingleTransition transition) {
        this.transition = transition;
    }

    public SingleTransition getTransition() {
        return transition;
    }

    public abstract BindingSignal getBindingSignal(FieldOutput bsEvent);

    public boolean linkCheck(Synapse ts, BindingSignal fromBS) {
        return getState() == fromBS.getState();
    }

    @Override
    public Stream<BindingSignal> propagate(BindingSignal bs) {
        if(bs.getState() != getState())
            return Stream.empty();

        BindingSignal toBS = transition.propagate(bs);
        if(toBS == null)
            return Stream.empty();

        return Stream.of(toBS);
    }
}