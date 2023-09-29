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
import network.aika.elements.links.Link;
import network.aika.visitor.operator.Operator;
import network.aika.visitor.step.Down;
import network.aika.visitor.step.Step;
import network.aika.visitor.step.Up;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.utils.Utils.depthToSpace;
import static network.aika.utils.Utils.idToString;

/**
 * @author Lukas Molzberger
 */
public abstract class Visitor<T extends Activation> {

    protected static final Logger log = LoggerFactory.getLogger(Visitor.class);

    private long v;

    protected T bindingSource;

    protected Step direction;

    protected Operator operator;

    public Visitor(Thought t, Operator operator) {
        this.v = t.getNewVisitorId();

        if(log.isDebugEnabled()) {
            log.debug("");
            log.debug(depthToSpace(0) + "Start:" + getClass().getSimpleName() + " " + operator.getClass().getSimpleName());
        }

        this.operator = operator;
        direction = new Down();
    }

    protected Visitor(Visitor<T> downVisitor, T bindingSource) {
        this.v = downVisitor.v;
        this.bindingSource = bindingSource;
        this.operator = downVisitor.operator;

        direction = new Up();
    }

    public void start(Activation<?> act) {
        visit(act, null, 0);
    }

    public abstract void upIntern(T origin, int depth);

    public void up(T origin, int depth) {
        if(direction.isUp())
            return;

        if(log.isDebugEnabled()) {
            log.debug(depthToSpace(depth) + "U-TURN " + origin.getClass().getSimpleName() + " " + origin.getId() + " " + origin.getLabel());
        }
        upIntern(origin, depth);
    }

    public Step getDirection() {
        return direction;
    }

    public long getV() {
        return v;
    }

    public abstract void check(Link lastLink, Activation act);

    public abstract void visit(Link l, int depth);

    public abstract void visit(Activation act, Link l, int depth);

    public void next(Activation<?> act, Link lastLink, int depth) {
        if(log.isDebugEnabled())
            log.debug(depthToSpace(depth) + direction + " " + act.getClass().getSimpleName() + " " + act.getId() + " " + act.getLabel());

        check(lastLink, act);
        direction.next(this, act, depth + 1);
    }

    public void next(Link<?, ?, ?> l, int depth) {
        if(log.isDebugEnabled())
            log.debug(depthToSpace(depth) + direction + " " + l.getClass().getSimpleName() + " " + idToString(l.getInput()) + " " + idToString(l.getOutput()));

        direction.next(this, l, depth + 1);
    }
}
