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

import network.aika.elements.Timestamp;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingSignalSlot;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.keys.FieldQueueKey;
import network.aika.utils.Utils;

import static network.aika.queue.Phase.FIRED;
import static network.aika.queue.keys.FieldQueueKey.SORT_VALUE_PRECISION;

/**
 *
 * @author Lukas Molzberger
 */
public class Fired extends ElementStep<Activation> {

    private double net;
    private int sortValue;

    public Fired(Activation act) {
        super(act);
    }

    @Override
    public void process() {
        Activation<?> act = getElement();

        act.setFired();

        act.getBindingSignalSlots()
                .forEach(BindingSignalSlot::onFired);

        Counting.add(act);
/*
        if (act.getNeuron().isAbstract() &&
                act.getModel().getConfig().isMetaInstantiationEnabled())
            Instantiation.add(act);
 */
    }

    public void updateNet(double net) {
        this.net = net;
        sortValue = convertSortValue(net);
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

    private int convertSortValue(double newSortValue) {
        return (int) (SORT_VALUE_PRECISION * newSortValue);
    }

    @Override
    public Phase getPhase() {
        return FIRED;
    }

    @Override
    public String toString() {
        return super.toString() + " net:" + Utils.doubleToString(net);
    }
}
