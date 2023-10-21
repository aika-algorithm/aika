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

import network.aika.Document;
import network.aika.debugger.AbstractGraphManager;
import network.aika.debugger.AbstractParticleLink;
import network.aika.debugger.activations.layout.ParticleLink;
import network.aika.debugger.activations.particles.ActivationParticle;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.NodeParticle;

/**
 * @author Lukas Molzberger
 */
public class ActivationGraphManager extends AbstractGraphManager<Activation, Link, ActivationParticle> {

    private Document thought;

    private boolean disabledForces = false;

    public ActivationGraphManager(Graph graph, Document t) {
        super(graph);
        thought = t;

        k = STANDARD_DISTANCE_X;
        K1Init = 0.06f;
        K1Final = 0.01f;
        K2 = 0.005f;
    }

    public boolean isDisabledForces() {
        return disabledForces;
    }

    public void setDisabledForces(boolean disabledForces) {
        this.disabledForces = disabledForces;
    }

    public Document getThought() {
        return thought;
    }

    protected Long getAikaNodeId(Activation act) {
        return act != null ? Long.valueOf(act.getId()) : null;
    }

    @Override
    protected synchronized ActivationParticle createParticle(Activation key, Node node) {
        return ActivationParticle.create(key, node, this);
    }

    @Override
    protected ParticleLink createParticleLink(Link l, Edge edge) {
        return ParticleLink.create(l, edge, this);
    }

    @Override
    protected AbstractParticleLink getParticleLink(Link link) {
        return getParticle(link.getInput())
                .getOutputParticleLink(
                        Long.valueOf(link.getOutput().getId())
                );
    }

    public ParticleLink lookupParticleLink(Link l) {
        return (ParticleLink) lookupParticleLink(l, l.getInput(), l.getOutput());
    }

    public Edge getEdge(Link l) {
        return getEdge(l.getInput(), l.getOutput());
    }

    @Override
    public Link getLink(Edge e) {
        Activation iAct = getAikaNode(e.getSourceNode());
        Activation<?> oAct = getAikaNode(e.getTargetNode());

        return oAct.getInputLinks()
                .filter(l -> l.getInput() == iAct)
                .findAny()
                .orElse(null);
    }

    @Override
    public synchronized NodeParticle newNodeParticle(String id) {
        return getParticle(
                getNode(id)
        );
    }
}
