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
package network.aika.lattice;


import network.aika.Document;
import network.aika.lattice.activation.AndActivation;
import network.aika.lattice.activation.OrActivation;
import network.aika.neuron.activation.Activation;

import java.util.*;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class NodeActivation<T extends Node> implements Comparable<NodeActivation<T>> {

    public final int id;

    private final T node;

    protected final Document doc;

    protected Node.ThreadState threadState;

    public Long repropagateV;
    public boolean registered;

    public TreeMap<Integer, AndActivation.Link> outputsToAndNode = new TreeMap<>();
    public TreeMap<Integer, OrActivation.Link> outputsToOrNode = new TreeMap<>();


    public NodeActivation(Document doc, T node) {
        this.id = doc.getNewNodeActivationId();
        this.doc = doc;
        this.node = node;
    }

    public Node.ThreadState initThreadState() {
        assert this.threadState == null;
        this.threadState = new Node.ThreadState();
        return this.threadState;
    }

    public T getNode() {
        return node;
    }


    public Document getDocument() {
        return doc;
    }


    public abstract Activation getInputActivation(int i);


    @Override
    public int compareTo(NodeActivation<T> act) {
        return Integer.compare(id, act.id);
    }
}
