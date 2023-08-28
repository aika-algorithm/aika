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
package network.aika.debugger.activations.layout;

import network.aika.debugger.activations.ActivationGraphManager;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.links.PatternLink;
import network.aika.text.Range;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.Objects;
import java.util.OptionalDouble;

import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_X;


/**
 * @author Lukas Molzberger
 */
public class PatternParticleLink<L extends PatternLink> extends ParticleLink<L> {


    public PatternParticleLink(L l, Edge e, ActivationGraphManager gm) {
        super(l, e, gm);
    }

    public static ParticleLink create(PatternLink l, Edge e, ActivationGraphManager gm) {
        return new PatternParticleLink(l, e, gm);
    }

    @Override
    public void processLayout() {
        PatternActivation oAct = link.getOutput();

        OptionalDouble avgXPos = oAct.getInputLinks()
                .map(Link::getInput)
                .filter(Objects::nonNull)
                .map(Activation::getTokenPosRange)
                .filter(Objects::nonNull)
                .mapToDouble(r -> r.getEnd())
                .average();

        if(!avgXPos.isPresent())
            return;

        Node node = graphManager.getNode(oAct);

        if(node == null)
            return;

        node.setAttribute(
                "x",
                STANDARD_DISTANCE_X * avgXPos.getAsDouble()
        );
    }
}
