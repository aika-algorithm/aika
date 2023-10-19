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

import network.aika.elements.synapses.Synapse;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.enums.direction.Direction;
import network.aika.visitor.UpVisitor;
import network.aika.visitor.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Lukas Molzberger
 */
public class IncomingLinkingOperator extends LinkingOperator {

    protected static final Logger log = LoggerFactory.getLogger(Visitor.class);

    private Synapse sourceSyn;
    private Link sourceLink; // null if latent

    public IncomingLinkingOperator(Activation fromAct, Synapse sourceSyn, Link sourceLink, Synapse targetSyn) {
        super(fromAct, targetSyn);
        this.sourceSyn = sourceSyn;
        this.sourceLink = sourceLink;
    }

    @Override
    public Synapse getStartSynapse() {
        return sourceSyn;
    }

    @Override
    public Direction getDirection() {
        return Direction.INPUT;
    }

    @Override
    public void check(UpVisitor v, Link lastLink, Activation act, int state) {
        if(log.isDebugEnabled())
            log.debug("IncomingLinkingOperator.check() startSynapse:" + getStartSynapse() + " sourceLink:" + sourceLink + " lastLink:" + lastLink + " act:" + act);

        if (act.getNeuron() != targetSyn.getInput())
            return;

        if (act == sourceAct)
            return;

        if (!v.compatible(sourceSyn, targetSyn))
            return;

        if (!targetSyn.checkLinkingEvent(act))
            return;

        if(sourceLink == null && !sourceSyn.checkVisitorState(state))
            return;

        if(!targetSyn.checkVisitorState(state))
            return;

        link(sourceAct, sourceSyn, sourceLink, act, targetSyn);
    }

    public void checkRelation(Synapse relSyn, Activation fromAct, Activation toAct, Direction relDir) {
        if(log.isDebugEnabled())
            log.debug("IncomingLinkingOperator.check() startSynapse:" + getStartSynapse() + " sourceLink:" + sourceLink  + " toAct:" + toAct);

        if (toAct.getNeuron() != targetSyn.getInput())
            return;

        if (toAct == sourceAct)
            return;

        if (!targetSyn.checkLinkingEvent(toAct))
            return;

        Link l = link(sourceAct, sourceSyn, sourceLink, toAct, targetSyn);
        if (l != null)
            l.getOutput().createLatentRelation(relSyn, relDir, fromAct, toAct);
    }
}
