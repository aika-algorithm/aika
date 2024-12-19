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

import network.aika.exceptions.TimeoutException;
import network.aika.queue.keys.QueueKey;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 *
 * @author Lukas Molzberger
 */
public class Queue {

    protected Step currentStep;

    private final NavigableMap<QueueKey, Step> queue = new TreeMap<>(QueueKey.COMPARATOR);

    private long timestampCounter = 0;

    private Timestamp timestampOnProcess = new Timestamp(0);

    public Long getTimeout(){
        return Long.MAX_VALUE;
    }

    public Timestamp getTimestampOnProcess() {
        return timestampOnProcess;
    }

    public Timestamp getCurrentTimestamp() {
        return new Timestamp(timestampCounter);
    }

    public Timestamp getNextTimestamp() {
        return new Timestamp(timestampCounter++);
    }

    public synchronized void addStep(Step s) {
        s.createQueueKey(
                getNextTimestamp(),
                getRound(s)
        );
        queue.put(s.getQueueKey(), s);
        s.setQueued(true);
    }

    private int getRound(Step s) {
        int round;
        if(s.getPhase().isDelayed())
            round = QueueKey.MAX_ROUND;
        else
            round = getCurrentRound();

        if(s.incrementRound())
            round++;

        return round;
    }

    public int getCurrentRound() {
        if(currentStep == null)
            return 0;

        int r = currentStep.getQueueKey().getRound();
        return r == QueueKey.MAX_ROUND ? 0 : r;
    }

    public synchronized void removeStep(Step s) {
        Step removedStep = queue.remove(s.getQueueKey());
        assert removedStep != null;
        s.setQueued(false);
    }

    public Collection<Step> getQueueEntries() {
        return queue.values();
    }

    public void process() {
        process(null);
    }

    public synchronized void process(Predicate<Step> filter) {
        long startTime = System.currentTimeMillis();

        while (!queue.isEmpty()) {
            checkTimeout(startTime);

            currentStep = queue.pollFirstEntry().getValue();
            currentStep.setQueued(false);

            timestampOnProcess = getCurrentTimestamp();
            if(filter == null || filter.test(currentStep))
                currentStep.process();

            currentStep = null;
        }
    }

    private void checkTimeout(long startTime) {
        Long timeout = getTimeout();
        if(timeout == null)
            return;

        long currentTime = System.currentTimeMillis();
        if (startTime + timeout < currentTime)
            throw new TimeoutException();
    }
}
