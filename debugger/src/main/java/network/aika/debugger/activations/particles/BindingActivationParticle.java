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
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.LatentRelationActivation;
import org.graphstream.graph.Node;

/**
 * @author Lukas Molzberger
 */
public class BindingActivationParticle<E extends BindingActivation> extends ActivationParticle<E> {


    public BindingActivationParticle(E act, Node n, ActivationGraphManager gm) {
        super(act, n, gm);

        processLayout(null);
    }

    @Override
    public void processLayout(LayoutState ls) {
    }

    public static ActivationParticle create(BindingActivation act, Node n, ActivationGraphManager gm) {
        if(act instanceof LatentRelationActivation) {
            return new LatentRelationActivationParticle((LatentRelationActivation) act, n, gm);
        }

        return new BindingActivationParticle(act, n, gm);
    }
}
