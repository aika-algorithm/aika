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

import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.synapses.slots.AnnealingSynapseOutputSlot;
import network.aika.fields.Field;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;

/**
 *
 * @author Lukas Molzberger
 */
public class FeedbackTrigger extends ElementStep<ConjunctiveActivation> {

    boolean state;

    public static void add(ConjunctiveActivation act, boolean state) {
        Step.add(new FeedbackTrigger(act, state));
    }

    public FeedbackTrigger(ConjunctiveActivation act, boolean state) {
        super(act);
        this.state = state;
    }

    @Override
    public void process() {
        ConjunctiveActivation<?> act = getElement();
        AnnealingSynapseOutputSlot ciSlot = act.getActiveCategoryInputSlot();
        if(ciSlot == null)
            return;

        Field ft = act.getInstantiationAnnealingValue();
        if(ft == null)
            return;

        ft.receiveUpdate(
                null,
                state ? -1.0 : 1.0
        );
    }

    @Override
    public Phase getPhase() {
        return Phase.FEEDBACK_TRIGGER;
    }

    @Override
    public String toString() {
        return super.toString() + " State:" + state;
    }
}
