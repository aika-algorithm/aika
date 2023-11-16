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
import network.aika.enums.LinkingMode;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;
import network.aika.queue.link.LinkingIn;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.UpVisitor;
import network.aika.visitor.operator.BSOutgoingLinkingOperator;
import network.aika.visitor.operator.LinkingOperator;
import network.aika.visitor.operator.OutgoingLinkingOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.queue.Phase.OUTPUT_LINKING;

/**
 *
 * @author Lukas Molzberger
 */
public class BSLinkingOut extends ElementStep<Activation> {

    protected static final Logger log = LoggerFactory.getLogger(LinkingIn.class);

    private LinkingMode mode;
    private PatternActivation bindingSignal;


    public static void add(Activation act, PatternActivation bs, LinkingMode mode) {
        Step.add(new BSLinkingOut(act, bs, mode));
    }

    public BSLinkingOut(Activation act, PatternActivation bs, LinkingMode mode) {
        super(act);

        this.bindingSignal = bs;
        this.mode = mode;
    }

    @Override
    public void process() {
        Activation<?> act = getElement();
        Neuron<?, ?> n = act.getNeuron();

        n.wakeupPropagable();

        n.getOutputSynapsesAsStream(act.getDocument())
                .filter(s ->
                        s.getLinkingMode() == mode
                )
                .toList()
                .forEach(s ->
                        linkOutgoing(s)
                );
    }

    private void linkOutgoing(Synapse targetSyn) {
        Activation<?> act = getElement();

        if(log.isDebugEnabled())
            log.debug("linkOutgoing: targetSyn:" + targetSyn + " iAct:" + act);

        Neuron to = targetSyn.getOutput();
        LinkingOperator op = new BSOutgoingLinkingOperator(act, targetSyn);

        Relation rel = targetSyn.getRelation();
        if(rel != null)
            targetSyn.expandRelation(op, rel, to, OUTPUT);
        else {
            new UpVisitor(
                    act.getDocument(),
                    op
            ).start(bindingSignal);
        }
    }

    @Override
    public Phase getPhase() {
        return OUTPUT_LINKING;
    }

    @Override
    public String toString() {
        return super.toString() + " LinkingMode:" + mode;
    }
}
