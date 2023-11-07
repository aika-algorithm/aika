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
import network.aika.elements.links.Link;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.types.NegativeFeedbackSynapse;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import static network.aika.debugger.TypeMapper.synapseTypeModifiers;

/**
 * @author Lukas Molzberger
 */
public abstract class AbstractParticleLink<E extends Element> {

    protected E link;

    protected Edge edge;

    protected Node inputNode;
    protected Node outputNode;

    protected AbstractParticle inputParticle;
    protected AbstractParticle outputParticle;

    protected AbstractGraphManager graphManager;

    public AbstractParticleLink(E link, Edge e, AbstractGraphManager gm) {
        this.link = link;
        this.edge = e;
        this.graphManager = gm;
    }

    public abstract Long getInputId();

    public abstract Long getOutputId();

    public Edge getEdge() {
        return edge;
    }

    public E getLink() {
        return link;
    }

    public abstract void processLayout();

    public void onEvent() {
        String synapseTypeModifier = "";

        Synapse s;
        if(link instanceof Link) {
            Link l = ((Link) link);
            s = l.getSynapse();

            if(!l.isActive())
                synapseTypeModifier += " stroke-mode: dashes;";
        } else {
            s = (Synapse) link;
        }

        String style = synapseTypeModifiers.get(s.getClass());
        if(style != null)
            synapseTypeModifier += style;

        if(s instanceof PositiveFeedbackSynapse || s instanceof NegativeFeedbackSynapse)
            synapseTypeModifier += " arrow-shape: diamond;";

        edge.setAttribute("ui.style", synapseTypeModifier);
    }
}
