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
package network.aika.visitor.operator;

import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.enums.direction.Direction;
import network.aika.visitor.LinkingVisitor;


/**
 * @author Lukas Molzberger
 */
public class IncomingLinkingOperator extends LinkingOperator {

    private Synapse sourceSyn;
    private Link sourceLink;

    public IncomingLinkingOperator(Activation fromAct, Synapse sourceSyn, Link sourceLink, Synapse targetSyn) {
        super(fromAct, targetSyn);
        this.sourceSyn = sourceSyn;
        this.sourceLink = sourceLink;
    }

    public Direction getDirection() {
        return Direction.INPUT;
    }

    @Override
    public boolean verifySamePatternSynapse(NeuronProvider candidateSPSInput) {
        return super.verifySamePatternSynapse(candidateSPSInput) ||
                sourceSyn.getPInput() == candidateSPSInput;
    }

    @Override
    public void check(LinkingVisitor v, Link lastLink, Activation act) {
        if (act.getNeuron() != targetSyn.getInput())
            return;

        if (act == sourceAct)
            return;

        if (!v.compatible(sourceSyn, targetSyn))
            return;

        if (!targetSyn.checkLinkingEvent(act))
            return;

        Link l = link(sourceAct, sourceSyn, sourceLink, act, targetSyn);
        if (l != null)
            v.createRelation(l);
    }
}
