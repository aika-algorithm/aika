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

import network.aika.Document;
import network.aika.elements.Timestamp;
import network.aika.elements.Element;
import network.aika.fields.QueueInterceptor;
import network.aika.queue.keys.FieldQueueKey;
import network.aika.utils.Utils;

import static network.aika.queue.keys.FieldQueueKey.SORT_VALUE_PRECISION;
import static network.aika.utils.Utils.*;

/**
 *
 * @author Lukas Molzberger
 */
public class FieldStep<E extends Element> extends Step<E> {

    private QueueInterceptor interceptor;

    private Phase phase;

    private int round;

    private int sortValue = Integer.MAX_VALUE;

    private double delta = 0.0;


    public FieldStep(Document queue, Phase p, int round, QueueInterceptor qf) {
        super(queue);
        this.phase = p;
        this.round = p.isDelayed() ?
                Integer.MAX_VALUE :
                round;

        this.interceptor = qf;
    }

    private void updateSortValue(double newSortValue) {
        if(Utils.belowTolerance(TOLERANCE, sortValue - newSortValue))
            return;

        if(isQueued()) {
            Document q = interceptor.getQueue();
            q.removeStep(this);
            sortValue = convertSortValue(newSortValue);
            q.addStep(this);
        } else
            sortValue = convertSortValue(newSortValue);
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
    public void createQueueKey(Timestamp timestamp) {
        queueKey = new FieldQueueKey(round, getPhase(), sortValue, timestamp);
    }

    @Override
    public void process() {
        interceptor.process(this);
    }

    public int getRound() {
        return round;
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
        return " Round:" + roundToString(round) +
                " Delta:" + doubleToString(delta);
    }

    public String toString() {
        return " Delta:" + doubleToString(delta) +
                " Field: " + interceptor.getField() +
                " Ref:" + interceptor.getField().getReference();
    }
}
