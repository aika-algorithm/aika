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
import network.aika.enums.sign.Sign;
import network.aika.fields.AbstractFunction;
import network.aika.fields.FieldObject;
import network.aika.fields.link.FieldLink;
import network.aika.utils.Bound;
import network.aika.utils.FieldWritable;
import network.aika.utils.StatisticUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.enums.sign.Sign.POS;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronStatistic extends AbstractFunction implements FieldWritable {

    private Double alpha;

    protected double frequency;

    protected SampleSpace sampleSpace = new SampleSpace();

    public NeuronStatistic(FieldObject ref, String label, Double alpha, Double tolerance) {
        super(ref, label, tolerance);

        this.alpha = alpha;
    }

    @Override
    protected double computeUpdate(FieldLink fl, double u) {
        return 0;
    }

    public SampleSpace getSampleSpace() {
        return sampleSpace;
    }

    public void count(Range absoluteRange) {
        double oldN = sampleSpace.getN();

        sampleSpace.countSkippedInstances(
                absoluteRange,
                getInputValueByArg(0)
        );

        sampleSpace.count();
        frequency += 1.0;

        if (alpha != null)
            applyMovingAverage(
                    Math.pow(alpha, sampleSpace.getN() - oldN)
            );

        sampleSpace.updateLastPosition(absoluteRange);
    }

    public void applyMovingAverage(double alpha) {
        sampleSpace.applyMovingAverage(alpha);
        frequency *= alpha;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getFrequency(Sign s, double n) {
        return s == POS ?
                frequency :
                n - frequency;
    }

    public void setFrequency(double f) {
        frequency = f;
    }

    public double getSurprisal(Sign s, Range range, boolean addCurrentInstance) {
        double n = sampleSpace.getN(
                range,
                getInputValueByArg(0)
        );
        double p = getProbability(
                s,
                n,
                addCurrentInstance
        );
        return -StatisticUtils.surprisal(p);
    }

    public double getProbability(Sign s, double n, boolean addCurrentInstance) {
        double f = getFrequency(s, n);

        if(addCurrentInstance) {
            f += 1.0;
            n += 1.0;
        }

        return Bound.UPPER.probability(f, n);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(frequency);
        sampleSpace.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        frequency = in.readDouble();
        sampleSpace = SampleSpace.read(in);
    }
}
