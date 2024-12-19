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


import network.aika.queue.ProcessingPhase;
import network.aika.queue.Timestamp;
import network.aika.utils.StringUtils;

import java.util.Comparator;
import java.util.function.Function;

/**
 * @author Lukas Molzberger
 */
public abstract class QueueKey implements Comparable<QueueKey> {

    public static final int MAX_ROUND = Integer.MAX_VALUE;

    public static final Comparator<QueueKey> COMPARATOR = Comparator
            .<QueueKey>comparingInt(k -> k.round)
            .thenComparingInt(k -> k.getPhase().rank())
            .thenComparing(Function.identity())
            .thenComparing(QueueKey::getCurrentTimestamp);

    private final int round;

    private final ProcessingPhase phase;

    private final Timestamp currentTimestamp;

    public QueueKey(int round, ProcessingPhase phase, Timestamp currentTimestamp) {
        this.round = round;
        this.phase = phase;
        this.currentTimestamp = currentTimestamp;
    }

    public int getRound() {
        return round;
    }

    protected String getRoundStr() {
        return StringUtils.roundToString(getRound());
    }

    public ProcessingPhase getPhase() {
        return phase;
    }

    protected String getPhaseStr() {
        return getPhase() + "-" + getPhase();
    }

    public Timestamp getCurrentTimestamp() {
        return currentTimestamp;
    }
}
