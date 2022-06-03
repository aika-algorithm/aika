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
package network.aika.steps;

import network.aika.neuron.activation.Element;
import network.aika.neuron.activation.Timestamp;

import java.util.ArrayDeque;
import java.util.stream.Stream;

import static network.aika.neuron.activation.Timestamp.NOT_SET;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class InnerQueue implements QueueKey, Element {

    private Timestamp currentTimestamp;

    private boolean isQueued;

    private ArrayDeque<Step> innerQueue = new ArrayDeque<>();


    public void notifyValueUpdate() {

    }


    public Stream<Step> getSteps() {
        return innerQueue.stream();
    }

    public void addStep(Step s) {
        innerQueue.addLast(s);
        if(!isQueued)
            addOuterQueueEntry();

        getThought().queueEntryAddedEvent(s);
    }

    private void updateOuterQueue() {

    }

    private void addOuterQueueEntry() {
        getThought().addInnerQueueEntry(this);
    }

    private void removeOuterQueueEntry() {
        getThought().removeInnerQueueEntry(this);
    }

    public void process() {
        while (!innerQueue.isEmpty()) {
            Step s = innerQueue.pollFirst();

            getThought().beforeProcessedEvent(s);
            s.process();
            getThought().afterProcessedEvent(s);
        }
    }

    @Override
    public Timestamp getPrimaryTimestamp() {
        return null;
    }

    @Override
    public Timestamp getSecondaryTimestamp() {
        return currentTimestamp;
    }

    public void setSecondaryTimestamp(Timestamp timestamp) {
        this.currentTimestamp = timestamp;
    }

    public String timestampToString() {
        if(getFired() != NOT_SET)
            return "Fired:" + getFired() + " TS:" + currentTimestamp;
        else
            return "Created:" + getCreated() + " TS:" + currentTimestamp;
    }
}
