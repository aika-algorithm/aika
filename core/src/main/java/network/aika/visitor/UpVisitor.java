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

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;

import static network.aika.utils.Utils.depthToSpace;
import static network.aika.utils.Utils.idToString;

/**
 * @author Lukas Molzberger
 */
public abstract class UpVisitor extends Visitor {

    private final ConjunctiveActivation bindingSource;

    protected UpVisitor(DownVisitor downVisitor, ConjunctiveActivation bindingSource) {
        this.v = downVisitor.v;
        this.type = downVisitor.type;
        this.bindingSource = bindingSource;
        this.operator = downVisitor.operator;
        this.referenceAct = downVisitor.referenceAct;
    }

    public void check(Link lastLink, Activation act, int state) {
        operator.check(this, lastLink, act, state);
    }

    public boolean compatible(Synapse from, Synapse to) {
        return bindingSource != null;
    }

    @Override
    public void next(Activation<?> act, Link lastLink, int state, int depth) {
        check(lastLink, act, state);

        if(log.isDebugEnabled())
            log.debug(depthToSpace(depth) + dirToString() + " " + act.getClass().getSimpleName() + " " + act.getId() + " " + act.getLabel());

        act.getOutputLinks()
                .forEach(l ->
                        type.visit(this, l, filterState(state, l), depth + 1)
                );
    }

    @Override
    public void next(Link<?, ?, ?> l, int state, int depth) {
        if(log.isDebugEnabled())
            log.debug(depthToSpace(depth) + dirToString() + " " + l.getClass().getSimpleName() + " " + idToString(l.getInput()) + " " + idToString(l.getOutput()));

        type.visit(this, l.getOutput(), l, state, depth + 1);
    }

    public boolean isDown() {
        return false;
    }

    public int getDirectionIndex() {
        return 1;
    }

    protected String dirToString() {
        return "up";
    }

    public void createLatentRelation(Link l) {
        // Nothing to do
    }
}
