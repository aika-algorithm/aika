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
package network.aika.queue.activation;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.relations.Relation;
import network.aika.elements.synapses.Synapse;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;
import network.aika.visitor.UpVisitor;
import network.aika.visitor.operator.IncomingLinkingOperator;
import network.aika.visitor.operator.LinkingOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.enums.Scope.SAME;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.queue.Phase.INPUT_LINKING;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkingIn extends ElementStep<Activation> {

    protected static final Logger log = LoggerFactory.getLogger(LinkingIn.class);

    private PatternActivation bindingSignal;

    public static void add(Activation act, PatternActivation bs) {
        Step.add(new LinkingIn(act, bs));
    }

    public LinkingIn(Activation act, PatternActivation bs) {
        super(act);
        this.bindingSignal = bs;
    }

    @Override
    public void process() {
        Neuron<?, ?> n = getElement().getNeuron();
        n.getInputSynapsesAsStream()
                .forEach(targetSyn ->
                        linkIncoming(targetSyn)
                );
    }

    private void linkIncoming(Synapse targetSyn) {
        LinkingOperator op = new IncomingLinkingOperator(getElement(), null, targetSyn);
        Relation rel = targetSyn.getRelation();
        if(rel != null)
            targetSyn.expandRelation(op, rel, targetSyn.getOutput(), INPUT);
        else {
            new UpVisitor(
                    bindingSignal.getDocument(),
                    op
            ).start(bindingSignal, SAME);
        }
    }

    @Override
    public Phase getPhase() {
        return INPUT_LINKING;
    }
}
