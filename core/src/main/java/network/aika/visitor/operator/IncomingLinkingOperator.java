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

import network.aika.Document;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.relations.Relation;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Scope;
import network.aika.enums.direction.Direction;
import network.aika.visitor.UpVisitor;

import static network.aika.elements.synapses.Synapse.getLatentLink;


/**
 * @author Lukas Molzberger
 */
public class IncomingLinkingOperator extends LinkingOperator {

    private Synapse sourceSyn; // Set only during latent linking

    public IncomingLinkingOperator(Activation sourceAct, Synapse sourceSyn, Synapse targetSyn, PatternActivation bindingSignal) {
        super(sourceAct, targetSyn, bindingSignal);
        this.sourceSyn = sourceSyn;
    }

    @Override
    public void visitorCheck(UpVisitor v, Link lastLink, Activation act, Scope s) {
        if(targetSyn.getRequired().getFrom() != s)
            return;

        checkAndLink(act);
    }

    @Override
    public Link checkAndLink(Activation act) {
        if (act.getNeuron() != targetSyn.getInput())
            return null;

        if (act == sourceAct)
            return null;

        if (!targetSyn.getTrigger().check(act))
            return null;

        return targetSyn.link(
                act,
                isLatent() ?
                        latentLink(act) :
                        sourceAct
        );
    }

    @Override
    public void relationCheck(Relation rel, Synapse relSyn, Activation relatedAct, Direction relDir) {
        if(!isLatent() && !checkBSMatches(relatedAct, sourceAct))
            return;

        Link l = checkAndLink(relatedAct);
        if (l != null)
            rel.createLatentRelation(
                    l.getOutput(),
                    relDir.getInput(sourceAct, relatedAct),
                    relDir.getOutput(sourceAct, relatedAct)
            );
    }

    private Activation latentLink(Activation act) {
        Link sl = latentLink(sourceAct, sourceSyn, act, targetSyn);
        return sl != null ?
                sl.getOutput() :
                null;
    }

    public static Link latentLink(Activation actA, Synapse synA, Activation actB, Synapse synB) {
        Link linkA = getLatentLink(synA, synB, actA, actB);
        if (linkA != null)
            return linkA;

        Document doc = actA.getDocument();
        Activation oAct = synA.getOutput().createActivation(doc);

        return synA.createAndInitLink(actA, oAct);
    }

    private boolean isLatent() {
        return sourceSyn != null;
    }
}
