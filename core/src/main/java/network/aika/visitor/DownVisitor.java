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

import network.aika.Thought;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.direction.Direction;
import network.aika.visitor.operator.Operator;
import network.aika.visitor.relations.BoundUpVisitor;
import network.aika.visitor.types.VisitorType;

import static network.aika.utils.Utils.depthToSpace;
import static network.aika.utils.Utils.idToString;

/**
 * @author Lukas Molzberger
 */
public abstract class DownVisitor<T extends ConjunctiveActivation> extends Visitor<T> {

    public DownVisitor(Thought t, VisitorType type, Operator operator) {
        this.v = t.getNewVisitorId();
        this.type = type;

        if(log.isDebugEnabled()) {
            log.debug("");
            log.debug(depthToSpace(0) + "Start: Visitor:" + getClass().getSimpleName() + " Operator:" + operator.getClass().getSimpleName() + " Type:" + type.getClass().getSimpleName());
        }

        this.operator = operator;
    }

    public void checkRelation(T downBindingSource, Synapse relSyn, Direction relDir, int depth) {
        relSyn.getRelation()
                .evaluateLatentRelation((PatternActivation) downBindingSource, relDir.invert())
                .forEach(relTokenAct -> {
                            if (log.isDebugEnabled())
                                log.debug(
                                        depthToSpace(depth) + "U-TURN (rel) " +
                                                "downBS:" + downBindingSource.getClass().getSimpleName() + " " + downBindingSource.getId() + " " + downBindingSource.getLabel() + "  " +
                                                "upBS:" + relTokenAct.getClass().getSimpleName() + " " + relTokenAct.getId() + " " + relTokenAct.getLabel()
                                );

                            UpVisitor<PatternActivation> v = new BoundUpVisitor(
                                    this,
                                    (PatternActivation) downBindingSource,
                                    relTokenAct,
                                    relSyn,
                                    relDir
                            );

                            type.visit(v, relTokenAct, null, depth);
                        }
                );
    }

    @Override
    public void next(Activation<?> act, Link lastLink, int depth) {
        if(log.isDebugEnabled())
            log.debug(depthToSpace(depth) + dirToString() + " " + act.getClass().getSimpleName() + " " + act.getId() + " " + act.getLabel());

        act.getInputLinks()
                .forEach(l ->
                        type.visit(this, l, depth + 1)
                );
    }

    @Override
    public void next(Link<?, ?, ?> l, int depth) {
        if(log.isDebugEnabled())
            log.debug(depthToSpace(depth) + dirToString() + " " + l.getClass().getSimpleName() + " " + idToString(l.getInput()) + " " + idToString(l.getOutput()));

        if(l.getInput() != null)
            type.visit(this, l.getInput(), l, depth + 1);
    }

    public boolean isDown() {
        return true;
    }

    public int getDirectionIndex() {
        return 0;
    }

    protected String dirToString() {
        return "down";
    }
}
