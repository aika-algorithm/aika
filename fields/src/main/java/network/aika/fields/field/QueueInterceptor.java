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
package network.aika.fields.field;

import network.aika.queue.ProcessingPhase;
import network.aika.queue.Queue;
import network.aika.queue.steps.FieldUpdate;
import network.aika.queue.Step;

/**
 *
 * @author Lukas Molzberger
 */
public class QueueInterceptor {

    private ProcessingPhase phase;

    private FieldUpdate step;

    private Field field;

    private Queue queue;

    private boolean isNextRound;

    public QueueInterceptor(Queue q, Field f, ProcessingPhase phase, boolean isNextRound) {
        this.queue = q;
        this.field = f;
        this.phase = phase;
        this.isNextRound = isNextRound;
    }

    public FieldUpdate getStep() {
        return step;
    }

    public Field getField() {
        return field;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    private FieldUpdate getOrCreateStep() {
        if(step == null)
            step = new FieldUpdate<>(phase, this);

        return step;
    }

    public void receiveUpdate(double u, boolean replaceUpdate) {
        FieldUpdate s = getOrCreateStep();
        s.updateDelta(u, replaceUpdate);

        if(u != 0.0 && !s.isQueued()) {
            if(!Step.add(s)) {
                process(s);
            }
        }
    }

    public void process(FieldUpdate s) {
        step = null;
        field.triggerUpdate(s.getDelta());
    }

    public Queue getQueue() {
        return queue;
    }
}
