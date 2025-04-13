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

import network.aika.queue.*;
import network.aika.queue.keys.FieldQueueKey;
import network.aika.type.Obj;
import network.aika.fields.field.QueueInterceptor;
import network.aika.utils.ApproximateComparisonValueUtil;

import static network.aika.utils.StringUtils.doubleToString;
import static network.aika.utils.StringUtils.roundToString;


/**
 *
 * @author Lukas Molzberger
 */
public class FieldUpdate<E extends Obj & QueueProvider> extends Step<E> {

    private final QueueInterceptor interceptor;

    private final ProcessingPhase phase;

    private int sortValue = Integer.MAX_VALUE;

    private double delta = 0.0;

    public FieldUpdate(ProcessingPhase p, QueueInterceptor qf) {
        this.phase = p;
        this.interceptor = qf;
    }

    @Override
    public boolean incrementRound() {
        return interceptor.isNextRound();
    }

    private void updateSortValue(double delta) {
        int newSortValue = ApproximateComparisonValueUtil.convert(delta);
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
    public ProcessingPhase getPhase() {
        return phase;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getElement() {
        return (E) interceptor.getField().getObject();
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
        return getElement() + " Delta:" + doubleToString(delta) +
                " Field: " + interceptor.getField() +
                " Ref:" + interceptor.getField().getObject();
    }
}
