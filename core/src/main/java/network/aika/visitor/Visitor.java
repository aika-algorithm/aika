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
import network.aika.elements.links.Link;
import network.aika.enums.Scope;
import network.aika.visitor.operator.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lukas Molzberger
 */
public abstract class Visitor {

    protected static final Logger log = LoggerFactory.getLogger(Visitor.class);

    protected long v;

    protected Operator operator;

    public Operator getOperator() {
        return operator;
    }

    public void start(Activation<?> act, Scope s) {
        next(act,null, s, 0);
    }

    public void start(Link l, Scope s) {
        l.visit(this, s, 0);
    }

    public abstract int getDirectionIndex();

    public long getV() {
        return v;
    }

    public abstract void next(Activation<?> act, Link lastLink, Scope s, int depth);

    public abstract void next(Link<?, ?, ?> l, Scope s, int depth);

    protected abstract String dirToString();

    public abstract boolean isDown();
}
