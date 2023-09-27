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
package network.aika.queue;

import network.aika.debugger.EventType;
import network.aika.elements.Timestamp;
import network.aika.queue.keys.QueueKey;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;

import static network.aika.debugger.EventType.*;
import static network.aika.queue.keys.QueueKey.MAX_ROUND;

/**
 *
 * @author Lukas Molzberger
 */
public class Queue {

    protected Step currentStep;

    private final NavigableMap<QueueKey, Step> queue = new TreeMap<>(QueueKey.COMPARATOR);

    int round = 0;

    private long timestampCounter = 0;

    private Timestamp timestampOnProcess = new Timestamp(0);

    public Timestamp getTimestampOnProcess() {
        return timestampOnProcess;
    }

    public Timestamp getCurrentTimestamp() {
        return new Timestamp(timestampCounter);
    }

    public Timestamp getNextTimestamp() {
        return new Timestamp(timestampCounter++);
    }

    public void addStep(Step s) {
        s.createQueueKey(getNextTimestamp());
        queue.put(s.getQueueKey(), s);
        queueEvent(ADDED, s);
    }

    public void removeStep(Step s) {
        Step removedStep = queue.remove(s.getQueueKey());
        assert removedStep != null;
        s.removeQueueKey();
    }

    public Collection<Step> getQueue() {
        return queue.values();
    }

    public int getRound(boolean nextRound) {
        return round + (nextRound ? 1 : 0);
    }

    public void updateRound(int r) {
        if(currentStep.getRound() != MAX_ROUND && round < r)
            round = r;
    }

    public void incrementRound() {
        round++;
    }


    public void process(int maxRound, Phase maxPhase) {
        while (!queue.isEmpty()) {
            if(checkMaxPhaseReached(maxRound, maxPhase, true))
                break;

            currentStep = queue.pollFirstEntry().getValue();
            currentStep.removeQueueKey();

            timestampOnProcess = getCurrentTimestamp();

            queueEvent(BEFORE, currentStep);

            updateRound(currentStep.getRound());

            currentStep.process();
            queueEvent(AFTER, currentStep);
            currentStep = null;
        }
    }

    public void skip(int maxRound, Phase maxPhase) {
        while (!queue.isEmpty()) {
            if(checkMaxPhaseReached(maxRound, maxPhase, false))
                break;

            currentStep = queue.pollFirstEntry().getValue();
            currentStep.removeQueueKey();
        }
    }

    public void queueEvent(EventType et, Step s) {

    }

    /**
     * The postprocessing steps such as counting, cleanup or save are executed.
     */
    public void postProcessing() {
        process(MAX_ROUND, null);
    }


    private boolean checkMaxPhaseReached(int maxRound, Phase maxPhase, boolean incl) {
        QueueKey fe = queue.firstEntry().getKey();
        if(fe.getRound() > maxRound)
            return true;

        if(maxPhase == null)
            return false;

        int r = maxPhase.compareTo(fe.getPhase());
        return incl ?
                r < 0 :
                r <= 0;
    }
}
