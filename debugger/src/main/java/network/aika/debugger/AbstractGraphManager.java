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
package network.aika.debugger;


import network.aika.elements.Element;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Lukas Molzberger
 */
public abstract class AbstractGraphManager {

    protected Map<Long, Node> nodes = new TreeMap<>();
    protected Map<long[], Edge> edges = new TreeMap<>(
            Comparator.comparingLong((long[] k) -> k[0])
                    .thenComparingLong(k -> k[1])
    );

    public AbstractGraphManager() {
    }

    public abstract Long getNodeId(Element key);
    public abstract long[] getEdgeIds(Element key);

    public abstract Node createNode(Element key);

    public abstract Edge createEdge(Element key);

    public Node lookupNode(Element key) {
        return nodes.computeIfAbsent(getNodeId(key), a ->
                createNode(key)
        );
    }

    public Edge lookupEdge(Element key) {
        return edges.computeIfAbsent(getEdgeIds(key), a ->
                createEdge(key)
        );
    }
}