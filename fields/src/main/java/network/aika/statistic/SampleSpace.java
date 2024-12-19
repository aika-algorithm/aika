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
package network.aika.statistic;

import network.aika.Range;
import network.aika.utils.FieldWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * The <a href="https://en.wikipedia.org/wiki/Sample_space}">Sample Space</a> keeps track of the number of
 * training instances a certain neuron or synapse has encountered. The Sample Space is used
 * to convert the counted frequencies to probabilities.
 *
 * @author Lukas Molzberger
 */
public class SampleSpace implements FieldWritable {

    private static final Logger log = LoggerFactory.getLogger(SampleSpace.class);

    private double N = 0;
    private Long lastPosition;

    public SampleSpace() {
    }

    public double getN() {
        return N;
    }

    public double getN(Range range, Double avgCoveredSpace) {
        if(range == null)
            return N;

        return N + getInactiveInstancesSinceLastPos(range, avgCoveredSpace);
    }

    public void setN(int N) {
        this.N = N;
    }

    public Long getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Long lastPosition) {
        this.lastPosition = lastPosition;
    }

    public void applyMovingAverage(double alpha) {
        N *= alpha;
    }

    public void countSkippedInstances(Range range, double avgCoveredSpace) {
        N += getInactiveInstancesSinceLastPos(range, avgCoveredSpace);
    }

    public void count() {
        N += 1;
    }

    public void updateLastPosition(Range absoluteRange) {
        lastPosition = absoluteRange.getEnd();
    }

    public double getInactiveInstancesSinceLastPos(Range absoluteRange, double avgCoveredSpace) {
        if(absoluteRange == null || lastPosition == null)
            return 0;

        long x = (absoluteRange.getBegin() - lastPosition);
        if(x < 0) {
            log.warn("Inactive instances are not allowed to be negative: " + x);
            return 0;
        }

        return x / avgCoveredSpace;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(N);
        out.writeBoolean(lastPosition != null);
        if(lastPosition != null)
            out.writeLong(lastPosition);
    }

    public static SampleSpace read(DataInput in) throws IOException {
        SampleSpace sampleSpace = new SampleSpace();
        sampleSpace.readFields(in);
        return sampleSpace;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        N = in.readDouble();
        if(in.readBoolean())
            lastPosition = in.readLong();
    }

    public String toString(Range r) {
        return "N:" + getN(r, null) + " lastPosition:" + (lastPosition != null ? lastPosition : "X");
    }

    public String toString() {
        return toString(null);
    }
}
