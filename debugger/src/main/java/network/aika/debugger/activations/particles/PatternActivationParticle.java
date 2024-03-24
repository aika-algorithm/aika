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
import network.aika.elements.activations.types.PatternActivation;
import network.aika.Range;
import network.aika.text.TextReference;
import org.graphstream.graph.Node;

import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_X;

/**
 * @author Lukas Molzberger
 */
public class PatternActivationParticle<E extends PatternActivation> extends ActivationParticle<E> {

    public PatternActivationParticle(E act, Node n, ActivationGraphManager gm) {
        super(act, n, gm);
    }

    @Override
    public void processLayout() {
        if(!act.getNeuron().getInputSynapses().isEmpty())
            return;

        TextReference ref = act.getTextReference();
        if(ref == null)
            return;

        Range r = ref.getTokenPosRange();
        if(r == null)
            return;

        if(!act.getNeuron().isAbstract())
            node.setAttribute("layout.frozen");

        Double x = STANDARD_DISTANCE_X * r.getBegin();

        node.setAttribute("x", x);
    }

    public static ActivationParticle create(PatternActivation act, Node n, ActivationGraphManager gm) {
        return new PatternActivationParticle(act, n, gm);
    }
}
