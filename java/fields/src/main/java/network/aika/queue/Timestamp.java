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

/**
 *
 * @author Lukas Molzberger
 */
public class Timestamp implements Comparable<Timestamp> {

    public static Timestamp MIN = new Timestamp(0);
    public static Timestamp MAX = new Timestamp(Long.MAX_VALUE);
    public static Timestamp NOT_SET = new Timestamp(Long.MAX_VALUE);

    private long timestamp;

    public Timestamp(long ts) {
        this.timestamp = ts;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String toString() {
        if(this == NOT_SET)
            return "NOT_SET";

        if(this == MIN)
            return "MIN";

        if(this == MAX)
            return "MAX";

        return "" + timestamp;
    }

    @Override
    public int compareTo(Timestamp ts) {
        return Long.compare(timestamp, ts.timestamp);
    }
}
