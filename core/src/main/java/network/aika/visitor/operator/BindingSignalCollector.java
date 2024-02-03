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
import network.aika.elements.relations.Relation;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Scope;
import network.aika.enums.direction.Direction;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.UpVisitor;

import static network.aika.enums.Scope.SAME;

/**
 * @author Lukas Molzberger
 */
public class BindingSignalCollector implements Operator {

    private PatternActivation bindingSignal;

    public static PatternActivation retrieveBindingSignal(Activation act, Scope s) {
        BindingSignalCollector op = new BindingSignalCollector();
        new DownVisitor(act.getDocument(), op)
                .start(act, s);
        return op.bindingSignal;
    }

    public static PatternActivation retrieveBindingSignal(Link l, Scope s) {
        BindingSignalCollector op = new BindingSignalCollector();
        new DownVisitor(l.getDocument(), op)
                .start(l, s);
        return op.bindingSignal;
    }

    public BindingSignalCollector() {
    }

    @Override
    public PatternActivation getBindingSignal() {
        return bindingSignal;
    }

    @Override
    public void check(Activation bsAct, Scope s, int depth) {
        if((bsAct instanceof PatternActivation) && s == SAME)
            bindingSignal = (PatternActivation) bsAct;
    }

    @Override
    public void visitorCheck(UpVisitor v, Link lastLink, Activation act, Scope s) {
    }
}
