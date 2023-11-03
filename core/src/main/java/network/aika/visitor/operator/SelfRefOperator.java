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
package network.aika.visitor.operator;

import network.aika.elements.Type;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;
import network.aika.visitor.UpVisitor;

/**
 * @author Lukas Molzberger
 */
public class SelfRefOperator implements Operator {

    private Activation target;

    private Transition identityRef;

    private boolean isSelfRef;

    public SelfRefOperator(Activation target, Transition identityRef) {
        this.target = target;
        this.identityRef = identityRef;
    }

    @Override
    public Synapse getStartSynapse() {
        return null;
    }

    @Override
    public Direction getDirection() {
        return null;
    }

    @Override
    public boolean checkForbiddenTransitions(Link l, Direction dir) {
        for(Transition t: l.getSynapse().getTransitions())
            if(t == identityRef)
                return true;

        return false;
    }

    @Override
    public boolean checkUp(Class<? extends Neuron> type) {
        return type == PatternNeuron.class;
    }

    @Override
    public void visitorCheck(UpVisitor v, Link lastLink, Activation act, int state) {
        if(act == target)
            isSelfRef = true;
    }

    @Override
    public void relationCheck(Synapse relSyn, Activation relAct, Direction relDir) {
        throw new UnsupportedOperationException();
    }

    public boolean isSelfRef() {
        return isSelfRef;
    }
}
