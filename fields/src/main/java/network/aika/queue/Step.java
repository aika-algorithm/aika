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

import network.aika.queue.keys.QueueKey;


/**
 * @author Lukas Molzberger
 */
public abstract class Step<E extends QueueProvider> {

    protected boolean isQueued;
    protected QueueKey queueKey;

    public void setQueued(boolean queued) {
        isQueued = queued;
    }

    public boolean isQueued() {
        return isQueued;
    }

    public QueueKey getQueueKey() {
        return queueKey;
    }

    public abstract Queue getQueue();

    public boolean incrementRound() {
        return false;
    }

    public abstract void createQueueKey(Timestamp timestamp, int round);

    public abstract void process();

    public abstract ProcessingPhase getPhase();

    public static boolean add(Step s) {
        Queue q = s.getQueue();
        if(q == null)
            return false;

        q.addStep(s);
        return true;
    }

    public abstract E getElement();
}
