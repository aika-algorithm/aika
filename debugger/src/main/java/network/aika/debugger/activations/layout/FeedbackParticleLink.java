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
import network.aika.debugger.activations.particles.ActivationParticle;
import network.aika.enums.direction.Direction;
import network.aika.elements.links.FeedbackLink;
import org.graphstream.graph.Edge;
import org.graphstream.ui.geom.Vector3;
import org.miv.pherd.geom.Point3;

/**
 * @author Lukas Molzberger
 */
public class FeedbackParticleLink<L extends FeedbackLink> extends ParticleLink<L> {

    public FeedbackParticleLink(L l, Edge e, ActivationGraphManager gm) {
        super(l, e, gm);
    }

    public static ParticleLink create(FeedbackLink l, Edge e, ActivationGraphManager gm) {
        return new FeedbackParticleLink(l, e, gm);
    }

    @Override
    public void calculateForce(Vector3 delta, Point3 pos, Direction dir, ActivationParticle other) {
    }

    @Override
    public void processLayout() {
    }
}