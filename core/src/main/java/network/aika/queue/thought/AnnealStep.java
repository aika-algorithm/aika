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
package network.aika.queue.thought;

import network.aika.Thought;
import network.aika.elements.Timestamp;
import network.aika.ActivationFunction;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;
import network.aika.queue.keys.DocQueueKey;

import static network.aika.queue.Phase.*;
import static network.aika.queue.keys.QueueKey.MAX_ROUND;
import static network.aika.utils.Utils.doubleToString;


/**
 *
 * @author Lukas Molzberger
 */
public class AnnealStep extends ElementStep<Thought> {

    double nextStep;

    public static void add(Thought t) {
        add(new AnnealStep(t));
    }

    public AnnealStep(Thought t) {
        super(t);
    }

    @Override
    public void createQueueKey(Timestamp timestamp) {
        queueKey = new DocQueueKey(
                MAX_ROUND,
                getPhase(),
                timestamp
        );
    }

    @Override
    public void process() {
        Thought t = queue;

        double av = t.getAnnealing().getValue();
        nextStep = t.getConfig().getAnnealStepSize() / ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT.outerGrad(av);
        double nextAnnealValue = nextStep + av;
        nextAnnealValue = Math.min(nextAnnealValue, 1.0);

        t.incrementRound();
        t.setFeedbackTriggerRound();
        t.getAnnealing().setValue(nextAnnealValue);

        if (nextAnnealValue < 1.0)
            AnnealStep.add(t);
    }

    @Override
    public Phase getPhase() {
        return ANNEAL;
    }

    @Override
    public String toString() {
        return "docId:" + queue.getId() +
                " NextStep:" + doubleToString(nextStep, "#.######") +
                " NextAnnealValue:" + doubleToString(queue.getAnnealing().getValue(), "#.######");
    }
}