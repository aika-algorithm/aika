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
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.Link;
import network.aika.enums.Scope;
import network.aika.visitor.operator.Operator;
import network.aika.visitor.types.VisitorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lukas Molzberger
 */
public abstract class Visitor<T extends ConjunctiveActivation> {

    protected static final Logger log = LoggerFactory.getLogger(Visitor.class);

    protected long v;

    protected VisitorType type;

    protected Operator operator;

    protected BindingActivation referenceAct;

    public Operator getOperator() {
        return operator;
    }

    public void start(Activation<?> act) {
        type.visit(this, act, null, 3, 0);
    }

    public void start(Link l) {
        type.visit(this, l, filterState(3, l), 0);
    }

    public void up(T bindingSource, int state, int depth) {
        // Nothing to do
    }

    public abstract int getDirectionIndex();

    public long getV() {
        return v;
    }

    public abstract void next(Activation<?> act, Link lastLink, int state, int depth);

    public abstract void next(Link<?, ?, ?> l, int state, int depth);

    protected abstract String dirToString();

    public VisitorType getType() {
        return type;
    }

    public abstract boolean isDown();

    public void setReferenceAct(BindingActivation refAct) {
        this.referenceAct = refAct;
    }

    public Activation getReferenceAct() {
        return referenceAct;
    }

    public static int filterState(int state, Link l) {
        return state &
                ((l.getSynapse().getScope() != Scope.INPUT ? 1 : 0) +
                (l.getSynapse().getScope() != Scope.SAME ? 2 : 0));
    }
}
