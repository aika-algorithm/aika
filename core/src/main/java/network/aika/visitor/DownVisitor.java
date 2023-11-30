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
package network.aika.visitor;

import network.aika.Document;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.enums.Scope;
import network.aika.visitor.operator.Operator;

import static network.aika.enums.direction.Direction.INPUT;

/**
 * @author Lukas Molzberger
 */
public class DownVisitor extends Visitor {

    public DownVisitor(Document doc, Operator op) {
        super(doc, op);
    }

    @Override
    public void next(Activation<?> act, Link lastLink, Scope s, int depth) {
        if(act.checkVisited(v))
            return;

        act.getInputLinks()
                .forEach(l ->
                        l.visit(this, s, depth + 1)
                );
    }

    @Override
    public void next(Link<?, ?, ?> l, Scope s, int depth) {
        Scope ts = INPUT.transition(s, l.getSynapse().getTransition());
        if (ts == null)
            return;

        int nd = depth + 1;
        Activation iAct = l.getInput();
        if(iAct != null) {
            operator.check(iAct, ts, nd);
            next(iAct, l, ts, nd);
        }
    }

    public boolean isDown() {
        return true;
    }

    protected String dirToString() {
        return "down";
    }
}
