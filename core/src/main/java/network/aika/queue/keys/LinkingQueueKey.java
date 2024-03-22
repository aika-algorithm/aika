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

import network.aika.elements.Element;
import network.aika.enums.Trigger;
import network.aika.enums.Scope;
import network.aika.queue.Phase;
import network.aika.queue.Timestamp;

import java.util.Comparator;

import static network.aika.queue.Timestamp.NOT_SET;

/**
 * @author Lukas Molzberger
 */
public class LinkingQueueKey extends FiredQueueKey {

    private Trigger trigger;

    private Scope bsType;

    private static Comparator<LinkingQueueKey> COMPARATOR = Comparator
            .<LinkingQueueKey, Trigger>comparing(k -> k.trigger)
            .thenComparing(k -> k.bsType);


    public LinkingQueueKey(int round, Phase phase, Element element, Trigger tr, Scope bsType, Timestamp currentTimestamp) {
        super(round, phase, element, currentTimestamp);

        this.trigger = tr;
        this.bsType = bsType;
    }

    @Override
    public int compareTo(QueueKey qk) {
        int r = FiredQueueKey.COMPARATOR.compare(this, (FiredQueueKey) qk);
        if(r != 0)
            return r;

        return COMPARATOR.compare(this, (LinkingQueueKey) qk);
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
}
