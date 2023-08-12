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
package network.aika.fields;


import network.aika.Thought;
import network.aika.debugger.FieldObserver;
import network.aika.elements.Element;
import network.aika.queue.FieldStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Lukas Molzberger
 */
public class QueueInterceptor implements UpdateListener<FieldLink> {

    private Phase phase;

    private FieldStep step;

    private Field field;

    private Thought queue;

    private List<FieldObserver> observers = new ArrayList<>();

    public QueueInterceptor(Thought q, Field f, Phase p) {
        this.queue = q;
        this.field = f;
        this.phase = p;
    }

    public FieldStep getStep() {
        return step;
    }

    public Field getField() {
        return field;
    }

    private FieldStep getOrCreateStep(int r) {
        if(step == null || step.getRound() < r)
            step = new FieldStep<>(queue, phase, r, this);

        return step;
    }

    @Override
    public void receiveUpdate(FieldLink fl, boolean nextRound, double u) {
        updateObservers();

        FieldStep s = getOrCreateStep(getRound(nextRound));
        s.updateDelta(u);

        if(u != 0.0 && !s.isQueued()) {
            if(!Step.add(s)) {
                process(s);
            }
        }
    }

    private int getRound(boolean nextRound) {
        Thought t = field.getReference().getThought();
        return t != null ? t.getRound(nextRound) : 0;
    }

    public void process(FieldStep s) {
        field.receiveUpdateInternal(null, false, s.getDelta());
        step = null;

        updateObservers();
    }

    public void addObserver(FieldObserver observer) {
        if(observers.contains(observer))
            return;

        observers.add(observer);
    }

    public void removeObserver(FieldObserver observer) {
        observers.remove(observer);
    }

    protected void updateObservers() {
        observers.forEach(o ->
                o.receiveUpdate(field.value)
        );
    }

    public Thought getQueue() {
        return queue;
    }
}
