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

import java.util.TreeSet;

/**
 *
 * @author Lukas Molzberger
 */
public class NodeQueue {

    private Document doc;

    private final TreeSet<Node> queue = new TreeSet<>(
            (n1, n2) -> Node.compareRank(doc, n1, n2)
    );

    private long queueIdCounter = 0;


    public NodeQueue(Document doc) {
        this.doc = doc;
    }


    public void add(Node n) {
        if(!n.isQueued(doc, queueIdCounter++)) {
            queue.add(n);
        }
    }


    public void process() {
        while(!queue.isEmpty()) {
            Node n = queue.pollFirst();

            n.setNotQueued(doc);
            n.processChanges(doc);
        }
    }
}
