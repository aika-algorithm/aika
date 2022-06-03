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
import java.util.Comparator;
import java.util.stream.Stream;

import static network.aika.neuron.activation.Timestamp.NOT_SET;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class OuterQueueEntry implements Element {

    public static Comparator<OuterQueueEntry> COMPARATOR = Comparator
            .<OuterQueueEntry, Timestamp>comparing(k -> k.getFired())
            .thenComparingDouble(k -> k.getSortValue())
            .thenComparing(k -> k.getCreated())
            .thenComparing(k -> k.getCurrentTimestamp());

    private Timestamp currentTimestamp;

    private boolean isQueued;
    private double currentSortValue;

    private ArrayDeque<Step> innerQueue = new ArrayDeque<>();


    public void notifyValueUpdate(double newSortValue) {
        if(!isQueued)
            return;

        removeOuterQueueEntry();
        currentSortValue = newSortValue;
        addOuterQueueEntry();
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

    private void addOuterQueueEntry() {
        getThought().addInnerQueueEntry(this);
        isQueued = true;
    }

    private void removeOuterQueueEntry() {
        getThought().removeInnerQueueEntry(this);
        isQueued = false;
    }

    public void process() {
        while (!innerQueue.isEmpty()) {
            Step s = innerQueue.pollFirst();

            getThought().beforeProcessedEvent(s);
            s.process();
            getThought().afterProcessedEvent(s);
        }
    }


    public double getSortValue() {
        return currentSortValue;
    }

    public Timestamp getCurrentTimestamp() {
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
