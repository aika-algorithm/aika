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
package network.aika.queue.keys;

import network.aika.Element;
import network.aika.queue.ProcessingPhase;
import network.aika.queue.Timestamp;

import java.util.Comparator;

import static network.aika.queue.Timestamp.NOT_SET;

/**
 * @author Lukas Molzberger
 */
public class FiredQueueKey extends QueueKey {

    protected static Comparator<FiredQueueKey> COMPARATOR = Comparator
            .<FiredQueueKey, Timestamp>comparing(k -> k.fired)
            .thenComparing(k -> k.created);

    private final Timestamp created;
    private final Timestamp fired;

    public FiredQueueKey(int round, ProcessingPhase phase, Element element, Timestamp currentTimestamp) {
        super(round, phase, currentTimestamp);
        this.created = element.getCreated();
        this.fired = element.getFired();
    }

    public Timestamp getFired() {
        return fired;
    }

    public Timestamp getCreated() {
        return created;
    }

    @Override
    public String toString() {
        String firedStr = getFired() == NOT_SET ?
                "NOT_FIRED" : "" +
                getFired();

        return "[r:" + getRoundStr() +
                ",p:" + getPhaseStr() +
                ",f:" + firedStr +
                ",c:" + getCreated() +
                ",ts:" + getCurrentTimestamp() +
                "]";
    }

    @Override
    public int compareTo(QueueKey qk) {
        return COMPARATOR.compare(this, (FiredQueueKey) qk);
    }
}
