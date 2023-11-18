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

import network.aika.elements.Timestamp;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.LinkingMode;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;
import network.aika.queue.keys.LatentLinkingQueueKey;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.UpVisitor;
import network.aika.visitor.operator.BSIncomingLinkingOperator;
import network.aika.visitor.operator.IncomingLinkingOperator;
import network.aika.visitor.operator.LinkingOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.elements.synapses.Synapse.getNetUB;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.queue.Phase.LATENT_LINKING;

/**
 *
 * @author Lukas Molzberger
 */
public class BSLatentLinking extends ElementStep<Activation> {

    protected static final Logger log = LoggerFactory.getLogger(BSLatentLinking.class);

    private PatternActivation bindingSignal;
    private Synapse sourceSyn;
    private Synapse targetSyn;

    public static void add(Activation act, PatternActivation bs, LinkingMode mode) {
        Neuron<?, ?> n = act.getNeuron();

        n.wakeupPropagable();

        n.getOutputSynapsesAsStream(act.getDocument())
                .filter(s ->
                        s.getLinkingMode() == mode
                ).forEach(sourceSyn ->
                        expandTargetSynapses(act, bs, sourceSyn)
                );
    }

    private static void expandTargetSynapses(Activation act, PatternActivation bs, Synapse sourceSyn) {
        if(!sourceSyn.isLinkingAllowed(true))
            return;

        Neuron<?, ?> targetNeuron = sourceSyn.getOutput();

        targetNeuron.getInputSynapsesAsStream()
        .filter(ts -> sourceSyn != ts)
        .filter(ts -> ts.isLinkingAllowed(true))
        .filter(ts -> getNetUB(sourceSyn, ts) > 0.0)
        .forEach(ts ->
                Step.add(new BSLatentLinking(act, bs, sourceSyn, ts))
        );
    }

    public BSLatentLinking(Activation act, PatternActivation bs, Synapse sourceSyn, Synapse targetSyn) {
        super(act);

        this.bindingSignal = bs;
        this.sourceSyn = sourceSyn;
        this.targetSyn = targetSyn;
    }

    @Override
    public void createQueueKey(Timestamp timestamp) {
        queueKey = new LatentLinkingQueueKey(
                getRound(),
                getPhase(),
                getElement(),
                sourceSyn.getInput().getId(),
                targetSyn.getInput().getId(),
                timestamp
        );
    }

    @Override
    public void process() {
        Activation iAct = getElement();

        if(log.isDebugEnabled())
            log.debug("latentLinkOutgoing: sourceSyn:" + sourceSyn + " targetSyn:" + this + " iAct:" + iAct);

        LinkingOperator op = new BSIncomingLinkingOperator(iAct, sourceSyn, targetSyn);
        Neuron to = targetSyn.getInput();

        if(sourceSyn.getRelation() != null)
            sourceSyn.expandRelation(op, sourceSyn.getRelation(), to, OUTPUT);
        else if(targetSyn.getRelation() != null)
            targetSyn.expandRelation(op, targetSyn.getRelation(), to, INPUT);
        else {
            new UpVisitor(
                    bindingSignal.getDocument(),
                    op
            ).start(bindingSignal);
        }
    }

    @Override
    public Phase getPhase() {
        return LATENT_LINKING;
    }

    @Override
    public String toString() {
        return super.toString() +
                " Source-Syn Input-Id:" + sourceSyn.getInput().getId() +
                " Target-Syn Input-Id:" + targetSyn.getInput().getId();
    }
}
