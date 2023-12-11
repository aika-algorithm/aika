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
import network.aika.elements.Element;
import network.aika.fields.QueueInterceptor;
import network.aika.queue.Phase;
import network.aika.queue.Queue;
import network.aika.queue.Step;
import network.aika.queue.keys.FieldQueueKey;

import static network.aika.queue.keys.FieldQueueKey.SORT_VALUE_PRECISION;
import static network.aika.utils.Utils.*;

/**
 *
 * @author Lukas Molzberger
 */
public class FieldUpdate<E extends Element> extends Step<E> {

    private QueueInterceptor interceptor;

    private Phase phase;

    private int sortValue = Integer.MAX_VALUE;

    private double delta = 0.0;

    public FieldUpdate(Phase p, QueueInterceptor qf) {
        this.phase = p;
        this.interceptor = qf;
    }

    @Override
    public boolean incrementRound() {
        return interceptor.getField().isNextRound();
    }

    private void updateSortValue(double delta) {
        int newSortValue = convertSortValue(delta);
        if(Math.abs(sortValue - newSortValue) == 0)
            return;

        if(isQueued()) {
            Queue q = getQueue();
            q.removeStep(this);
            sortValue = newSortValue;
            q.addStep(this);
        } else
            sortValue = newSortValue;
    }

    private int convertSortValue(double newSortValue) {
        return (int) (SORT_VALUE_PRECISION * newSortValue);
    }

    public int getSortValue() {
        return sortValue;
    }

    public void updateDelta(double delta, boolean replaceUpdate) {
        if(replaceUpdate)
            this.delta = 0;

        this.delta += delta;

        updateSortValue(
                Math.abs(this.delta)
        );
    }

    public void reset() {
        delta = 0.0;
    }

    @Override
    public Queue getQueue() {
        return interceptor.getQueue();
    }

    @Override
    public void createQueueKey(Timestamp timestamp, int round) {
        queueKey = new FieldQueueKey(round, getPhase(), sortValue, timestamp);
    }

    @Override
    public void process() {
        interceptor.process(this);
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

    @Override
    public E getElement() {
        return (E) interceptor.getField().getReference();
    }

    public double getDelta() {
        return delta;
    }

    public QueueInterceptor getInterceptor() {
        return interceptor;
    }

    public String toShortString() {
        return " Round:" + roundToString(getQueueKey().getRound()) +
                " Delta:" + doubleToString(delta);
    }

    public String toString() {
        return " Delta:" + doubleToString(delta) +
                " Field: " + interceptor.getField() +
                " Ref:" + interceptor.getField().getReference();
    }
}
