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


import network.aika.Model;
import network.aika.enums.direction.Direction;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Lukas Molzberger
 */
public class Range implements Writable {
    private long begin;
    private long end;

    private Range() {
    }

    public Range(long begin, long end) {
        this.begin = begin;
        this.end = end;
    }

    public long getPosition(Direction dir) {
        return dir == Direction.INPUT ?
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
        return getAbsoluteRange(referenceRange.getBegin());
    }

    public Range getAbsoluteRange(long offset) {
        return new Range(
                offset + begin,
                offset + end
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

    public static Long getBegin(Range r) {
        return r != null ? r.begin : null;
    }

    public long getEnd() {
        return end;
    }

    public static Long getEnd(Range r) {
        return r != null ? r.end : null;
    }

    public boolean contains(Range r) {
        return begin <= r.begin && r.end <= end;
    }

    public boolean equals(Range r) {
        return begin == r.begin && r.end == end;
    }

    public long length() {
        return end - begin;
    }

    public static Long length(Range r) {
        return r != null ?
                r.length() :
                null;
    }

    public String toString() {
        return "[" + begin + "," + end + "]";
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(begin);
        out.writeLong(end);
    }

    public static Range read(DataInput in, Model m) throws IOException {
        Range r = new Range();
        r.readFields(in, m);
        return r;
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        begin = in.readLong();
        end = in.readLong();
    }
}
