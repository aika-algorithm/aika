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
package network.aika.debugger.activations;

import network.aika.Thought;
import network.aika.debugger.AbstractGraphManager;
import network.aika.debugger.Edge;
import network.aika.debugger.Node;
import network.aika.debugger.activations.layout.LinkEdge;
import network.aika.debugger.activations.particles.ActivationNode;
import network.aika.elements.Element;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;

/**
 * @author Lukas Molzberger
 */
public class ActivationGraphManager extends AbstractGraphManager {

    private Thought thought;

    public ActivationGraphManager(Thought t) {
        super();
        thought = t;
    }

    public Thought getThought() {
        return thought;
    }


    @Override
    public Long getNodeId(Element key) {
        return Long.valueOf(((Activation) key).getId());
    }

    @Override
    public long[] getEdgeIds(Element key) {
        Link l = (Link) key;
        return new long[] {
                Long.valueOf(l.getInput().getId()),
                Long.valueOf(l.getOutput().getId())
        };
    }

    @Override
    public Node createNode(Element key) {
        return ActivationNode.create((Activation) key);
    }

    @Override
    public Edge createEdge(Element key) {
        return LinkEdge.create((Link) key);
    }
}
