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

import static network.aika.enums.direction.Direction.OUTPUT;

/**
 * @author Lukas Molzberger
 */
public class UpVisitor extends Visitor {

    public UpVisitor(Document doc, Operator op) {
        super(doc, op);
    }

    @Override
    public void next(Activation<?> act, Link lastLink, Scope s, int depth) {
        operator.visitorCheck(this, lastLink, act, s);

        if(act.checkVisited(v))
            return;

        act.getOutputLinks()
                .forEach(l ->
                        l.visit(this, s, depth + 1)
                );
    }

    @Override
    public void next(Link l, Scope s, int depth) {
        Scope ts = OUTPUT.transition(s, l.getSynapse().getTransition());
        if (ts == null)
            return;

        next(l.getOutput(), l, ts, depth + 1);
    }

    public boolean isDown() {
        return false;
    }

    protected String dirToString() {
        return "up";
    }
}
