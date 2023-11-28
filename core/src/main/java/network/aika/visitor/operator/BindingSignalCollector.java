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

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;
import network.aika.utils.BitUtils;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.UpVisitor;

/**
 * @author Lukas Molzberger
 */
public class BindingSignalCollector implements Operator {

    private Transition type;

    private PatternActivation bindingSignal;

    public static PatternActivation retrieveBindingSignal(Activation act, Transition bsType) {
        BindingSignalCollector op = new BindingSignalCollector(bsType);
        new DownVisitor(
                act.getDocument(),
                op
        ).start(act);
        return op.bindingSignal;
    }

    public static PatternActivation retrieveBindingSignal(Link l, Transition bsType) {
        BindingSignalCollector op = new BindingSignalCollector(bsType);
        new DownVisitor(
                l.getDocument(),
                op
        ).start(l);
        return op.bindingSignal;
    }

    public BindingSignalCollector(Transition type) {
        this.type = type;
    }

    public PatternActivation getBindingSignal() {
        return bindingSignal;
    }

    @Override
    public boolean checkForbiddenTransitions(Link l, Direction dir) {
        return l.getSynapse().getForbidden() == type;
    }

    @Override
    public void check(Activation bsAct, int state, int depth) {
        if(!(bsAct instanceof PatternActivation))
            return;

        for(Transition t: Transition.values()) {
            if(BitUtils.isSet(state, t))
                bindingSignal = (PatternActivation) bsAct;
        }
    }

    @Override
    public void visitorCheck(UpVisitor v, Link lastLink, Activation act, int state) {
    }

    @Override
    public void relationCheck(Synapse relSyn, Activation toAct, Direction relDir) {
    }
}
