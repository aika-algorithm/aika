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

    public IncomingLinkingOperator(Activation sourceAct, Synapse sourceSyn, Link sourceLink, Synapse targetSyn) {
        super(sourceAct, targetSyn);
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
    public void visitorCheck(UpVisitor v, Link lastLink, Activation act, int state) {
        if(!targetSyn.checkVisitorState(state))
            return;

        if(sourceLink == null && !sourceSyn.checkVisitorState(state))
            return;

        checkAndLink(act);
    }

    @Override
    public Link checkAndLink(Activation act) {
        if(log.isDebugEnabled())
            log.debug("IncomingLinkingOperator.check() startSynapse:" + getStartSynapse() + " sourceLink:" + sourceLink + " act:" + act);

        if (act.getNeuron() != targetSyn.getInput())
            return null;

        if (act == sourceAct)
            return null;

        if (!targetSyn.getLinkingMode().check(act))
            return null;

        if(sourceLink != null && !targetSyn.checkSecondaryVisitorRun(act, sourceLink.getOutput()))
            return null;

        Link sl = sourceLink != null ?
                sourceLink :
                latentLink(sourceAct, sourceSyn, act, targetSyn);

        return targetSyn.link(act, sl.getOutput());
    }

    public void relationCheck(Synapse relSyn, Activation relatedAct, Direction relDir) {
        Link l = checkAndLink(relatedAct);
        if (l != null)
            relSyn.createLatentRelation(
                    l.getOutput(),
                    relDir.getInput(sourceAct, relatedAct),
                    relDir.getOutput(sourceAct, relatedAct)
            );
    }
}
