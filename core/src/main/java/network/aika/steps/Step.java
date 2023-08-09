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

import network.aika.Thought;
import network.aika.elements.Element;
import network.aika.elements.Timestamp;
import network.aika.steps.keys.FiredQueueKey;
import network.aika.steps.keys.QueueKey;

import static network.aika.steps.keys.QueueKey.MAX_ROUND;


/**
 * @author Lukas Molzberger
 */
public abstract class Step<E extends Element> {

    private E element;
    protected QueueKey queueKey;

    public Step(E element) {
        this.element = element;
    }

    public boolean isQueued() {
        return queueKey != null;
    }

    public QueueKey getQueueKey() {
        return queueKey;
    }

    public void createQueueKey(Timestamp timestamp) {
        queueKey = new FiredQueueKey(
                getRound(),
                getPhase(),
                element,
                timestamp
        );
    }

    public int getRound() {
        return getPhase().isDelayed() ?
                MAX_ROUND :
                element.getThought().getRound(false);
    }

    public void removeQueueKey() {
        queueKey = null;
    }

    public String getStepName() {
        return getClass().getSimpleName();
    }

    public abstract void process();

    public abstract Phase getPhase();

    public static boolean add(Step s) {
        Thought t = s.getElement().getThought();
        if(t == null)
            return false;

        t.addStep(s);
        return true;
    }

    public E getElement() {
        return element;
    }

    @Override
    public String toString() {
        return "" + getElement();
    }
}
