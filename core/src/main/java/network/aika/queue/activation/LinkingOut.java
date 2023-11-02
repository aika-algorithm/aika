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

import network.aika.elements.neurons.Neuron;
import network.aika.elements.activations.Activation;
import network.aika.enums.LinkingMode;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;

import static network.aika.queue.Phase.OUTPUT_LINKING;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkingOut extends ElementStep<Activation> {

    private LinkingMode mode;

    public static void add(Activation act, LinkingMode mode) {
        Step.add(new LinkingOut(act, mode));
    }

    public LinkingOut(Activation act, LinkingMode mode) {
        super(act);

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
                        s.linkAndPropagateOut(act)
                );
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
