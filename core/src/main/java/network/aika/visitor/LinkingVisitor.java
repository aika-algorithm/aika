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
import network.aika.elements.synapses.Synapse;
import network.aika.visitor.operator.Operator;


/**
 * @author Lukas Molzberger
 */
public abstract class LinkingVisitor<T extends Activation> extends Visitor<T> {

    public LinkingVisitor(Thought t, Operator operator) {
        super(t, operator);
    }

    protected LinkingVisitor(LinkingVisitor<T> downVisitor, T origin) {
        super(downVisitor, origin);
    }

    public void check(Link lastLink, Activation act) {
        if(direction.isUp())
            operator.check(this, lastLink, act);
    }

    public boolean compatible(Synapse from, Synapse to) {
        return bindingSource != null;
    }

    public void createLatentRelation(Link l) {
    }
}
