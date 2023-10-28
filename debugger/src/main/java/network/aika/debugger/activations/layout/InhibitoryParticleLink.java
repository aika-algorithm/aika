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
import network.aika.elements.links.inhibitoryloop.InhibitoryLink;
import org.graphstream.graph.Edge;

import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_Y;


/**
 * @author Lukas Molzberger
 */
public class InhibitoryParticleLink<L extends InhibitoryLink> extends ParticleLink<L> {

    public InhibitoryParticleLink(L l, Edge e, ActivationGraphManager gm) {
        super(l, e, gm);
    }

    public static ParticleLink create(InhibitoryLink l, Edge e, ActivationGraphManager gm) {

        return new InhibitoryParticleLink(l, e, gm);
    }

    @Override
    public double getInitialYDistance() {
        return STANDARD_DISTANCE_Y / 2;
    }
}