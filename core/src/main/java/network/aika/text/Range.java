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
package network.aika.text;


/**
 *
 * @author Lukas Molzberger
 */
public class Range {
    private long begin;
    private long end;

    public Range(long begin, long end) {
        this.begin = begin;
        this.end = end;
    }

    public long getPosition(Slot s) {
        return s == Slot.BEGIN ?
                begin :
                end;
    }

    public static Range join(Range a, Range b) {
        if(a == null)
            return b;
        if(b == null)
            return a;

        return new Range(
                Math.min(a.begin, b.begin),
                Math.max(a.end, b.end)
        );
    }

    public Range getAbsoluteRange(Range referenceRange) {
        return new Range(
                referenceRange.getBegin() + begin,
                referenceRange.getBegin() + end
        );
    }

    public Range limit(Range referenceRange) {
        return new Range(
                Math.max(referenceRange.begin, Math.min(begin, referenceRange.end)),
                Math.max(referenceRange.begin, Math.min(end, referenceRange.end))
        );
    }

    public long getBegin() {
        return begin;
    }

    public long getEnd() {
        return end;
    }

    public boolean contains(Range r) {
        return begin <= r.begin && r.end <= end;
    }

    public long length() {
        return end - begin;
    }

    public String toString() {
        return "[" + begin + "," + end + "]";
    }
}
