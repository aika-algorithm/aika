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
package network.aika.queue.link;

import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.relations.Relation;
import network.aika.elements.synapses.Synapse;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.operator.IncomingLinkingOperator;
import network.aika.visitor.operator.LinkingOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.queue.Phase.INPUT_LINKING;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkingIn extends ElementStep<Link> {

    protected static final Logger log = LoggerFactory.getLogger(LinkingIn.class);


    public static void add(Link l) {
        Step.add(new LinkingIn(l));
    }

    public LinkingIn(Link l) {
        super(l);
    }

    @Override
    public void process() {
        Link l = getElement();
        linkAndPropagateIn(l);
    }

    public void linkAndPropagateIn(Link l) {
        Neuron<?, ?> n = l.getOutput().getNeuron();
        n.getInputSynapsesAsStream()
                .filter(Synapse::allowDeprecatedLinking)
                .filter(targetSyn -> targetSyn != l.getSynapse())
                .forEach(targetSyn ->
                    linkIncoming(l, targetSyn)
                );
    }

    private static void linkIncoming(Link l, Synapse targetSyn) {
        if(log.isDebugEnabled())
            log.debug("linkAndPropagateIn: link:" + l + " targetSyn:" + targetSyn);

        LinkingOperator op = new IncomingLinkingOperator(l.getOutput(), l.getSynapse(), l, targetSyn);
        Relation rel = targetSyn.getRelation();
        if(rel != null)
            targetSyn.expandRelation(op, rel, targetSyn.getOutput(), INPUT);
        else {
            new DownVisitor(
                    l.getDocument(),
                    op
            ).start(l);
        }
    }

    @Override
    public Phase getPhase() {
        return INPUT_LINKING;
    }
}
