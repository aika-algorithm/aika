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
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.LinkingMode;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;
import network.aika.queue.keys.LatentLinkingQueueKey;

import static network.aika.elements.synapses.Synapse.getNetUB;
import static network.aika.queue.Phase.LATENT_LINKING;

/**
 *
 * @author Lukas Molzberger
 */
public class LatentLinking extends ElementStep<Activation> {

    private Synapse sourceSyn;
    private Synapse targetSyn;

    public static void add(Activation act, LinkingMode mode) {
        Neuron<?, ?> n = act.getNeuron();

        n.wakeupPropagable();

        n.getOutputSynapsesAsStream(act.getDocument())
                .filter(s ->
                        s.getLinkingMode() == mode
                ).forEach(sourceSyn ->
                        expandTargetSynapses(act, sourceSyn)
                );
    }

    private static void expandTargetSynapses(Activation act, Synapse sourceSyn) {
        if(!sourceSyn.isLinkingAllowed(true))
            return;

        Neuron<?, ?> targetNeuron = sourceSyn.getOutput();

        targetNeuron.getInputSynapsesAsStream()
        .filter(ts -> sourceSyn != ts)
        .filter(ts -> ts.isLinkingAllowed(true))
        .filter(ts -> getNetUB(sourceSyn, ts) > 0.0)
        .forEach(ts ->
                Step.add(new LatentLinking(act, sourceSyn, ts))
        );
    }

    public LatentLinking(Activation act, Synapse sourceSyn, Synapse targetSyn) {
        super(act);

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
        targetSyn.latentLinking(sourceSyn, getElement());
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
