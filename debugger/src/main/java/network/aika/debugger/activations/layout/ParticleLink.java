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

import network.aika.debugger.AbstractParticleLink;
import network.aika.debugger.activations.ActivationGraphManager;
import network.aika.debugger.activations.particles.ActivationParticle;
import network.aika.elements.links.types.*;
import network.aika.enums.direction.Direction;
import network.aika.elements.links.*;
import org.graphstream.graph.Edge;
import org.graphstream.ui.geom.Vector3;
import org.miv.pherd.geom.Point3;

import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_Y;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 * @author Lukas Molzberger
 */
public class ParticleLink<L extends Link> extends AbstractParticleLink<L> {

    public ParticleLink(L l, Edge e, ActivationGraphManager gm) {
        super(l, e, gm);
        this.link = l;

        inputNode = graphManager.getNode(l.getInput());
        outputNode = graphManager.getNode(l.getOutput());

        inputParticle = graphManager.getParticle(inputNode);
        outputParticle = graphManager.getParticle(outputNode);

        inputParticle.addOutputParticleLink(this);
        outputParticle.addInputParticleLink(this);
    }

    @Override
    public Long getInputId() {
        return Long.valueOf(link.getInput().getId());
    }

    @Override
    public Long getOutputId() {
        return Long.valueOf(link.getOutput().getId());
    }

    public L getLink() {
        return link;
    }

    public static ParticleLink create(Link l, Edge e, ActivationGraphManager gm) {
        if(l instanceof InputObjectLink) {
            return InputObjectParticleLink.create((InputObjectLink) l, e, gm);
        } else if(l instanceof CategoryInputLink) {
            return CategoryInputParticleLink.create(l, e, gm);
        } else if (l instanceof SameObjectLink) {
            return SameObjectParticleLink.create((SameObjectLink) l, e, gm);
        } else if (l instanceof RelationInputLink) {
            return RelationInputParticleLink.create((RelationInputLink) l, e, gm);
        } else if(l instanceof PatternLink) {
            return PatternParticleLink.create((PatternLink) l, e, gm);
        } else if(l instanceof InhibitoryLink) {
            return InhibitoryParticleLink.create((InhibitoryLink) l, e, gm);
        } else if (l instanceof PositiveFeedbackLink) {
            return FeedbackParticleLink.create(l, e, gm);
        } else if (l instanceof NegativeFeedbackLink) {
            return FeedbackParticleLink.create(l, e, gm);
        }
        return new ParticleLink(l, e, gm);
    }

    public void calculateForce(Vector3 delta, Point3 pos, Direction dir, ActivationParticle other) {
        double targetDistance = getInitialYDistance();

        Point3 opos = other.getPosition();
        double dy = 0.0;

        if(dir == INPUT) {
            dy = (opos.y + targetDistance) - pos.y;
            dy = Math.max(0.0, dy);
        } else if(dir == OUTPUT) {
            dy = opos.y - (pos.y + targetDistance);
            dy = Math.min(0.0, dy);
        }

        delta.set(0.0, dy, 0.0);
    }

    public double getInitialYDistance() {
        return STANDARD_DISTANCE_Y;
    }

    @Override
    public void processLayout() {
        if(outputParticle.isManuallyMoved())
            return;

        if(link.getInput().getTextReference() == null)
            return;

        outputNode.setAttribute(
                "x",
                inputParticle.x
        );
    }
}
