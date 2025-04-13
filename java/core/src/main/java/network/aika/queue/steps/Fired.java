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
package network.aika.queue.steps;

import network.aika.activations.Activation;
import network.aika.queue.Phase;
import network.aika.queue.Queue;
import network.aika.queue.Timestamp;
import network.aika.queue.Step;
import network.aika.queue.keys.FieldQueueKey;
import network.aika.utils.ApproximateComparisonValueUtil;
import network.aika.utils.StringUtils;

import static network.aika.queue.Phase.FIRED;

/**
 *
 * @author Lukas Molzberger
 */
public class Fired extends Step<Activation> {

    private final Activation act;

    private double net;

    private int sortValue;

    public Fired(Activation act) {
        this.act = act;
    }

    @Override
    public void createQueueKey(Timestamp timestamp, int round) {
        queueKey = new FieldQueueKey(
                round,
                getPhase(),
                sortValue,
                timestamp
        );
    }

    @Override
    public void process() {
        Activation act = getElement();

        act.setFired();

        // Only once the activation is fired, will it be visible to other neurons.
        act.getBindingSignals()
                .values().
                forEach(bs ->
                        bs.addActivation(act)
                );

        act.linkOutgoing();
    }

    public void updateNet(double net) {
        this.net = net;
        sortValue = ApproximateComparisonValueUtil.convert(net);
    }

    @Override
    public Phase getPhase() {
        return FIRED;
    }

    @Override
    public Activation getElement() {
        return act;
    }

    @Override
    public Queue getQueue() {
        return act.getDocument();
    }

    @Override
    public String toString() {
        return "" + getElement() + " net:" + StringUtils.doubleToString(net);
    }
}
