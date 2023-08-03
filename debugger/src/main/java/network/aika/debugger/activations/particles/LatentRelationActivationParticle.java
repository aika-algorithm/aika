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
package network.aika.debugger.activations.particles;

import network.aika.debugger.activations.ActivationGraphManager;
import network.aika.debugger.activations.LayoutState;
import network.aika.elements.activations.LatentRelationActivation;
import org.graphstream.graph.Node;

import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_X;


/**
 * @author Lukas Molzberger
 */
public class LatentRelationActivationParticle extends BindingActivationParticle<LatentRelationActivation> {

    public LatentRelationActivationParticle(LatentRelationActivation act, Node n, ActivationGraphManager gm) {
        super(act, n, gm);
    }

    @Override
    public void processLayout(LayoutState ls) {
        if(act.getFromAct() != null && act.getToAct() != null) {
            node.setAttribute(
                    "x",
                    STANDARD_DISTANCE_X * (act.getFromAct().getTokenPosRange().getBegin() + act.getToAct().getTokenPosRange().getBegin()) / 2.0
            );
        }
    }
}
