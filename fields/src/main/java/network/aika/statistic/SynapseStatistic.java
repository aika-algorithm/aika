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

import static network.aika.enums.sign.Sign.NEG;
import static network.aika.enums.sign.Sign.POS;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseStatistic extends AbstractFunction implements FieldWritable {

    private Double alpha;

    protected double frequencyIPosOPos;
    protected double frequencyIPosONeg;
    protected double frequencyINegOPos;

    protected SampleSpace sampleSpace = new SampleSpace();

    public SynapseStatistic(FieldObject ref, String label, Double alpha, Double tolerance) {
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

    public double getFrequency(Sign inputSign, Sign outputSign, double n) {
        if(inputSign == POS && outputSign == POS) {
            return frequencyIPosOPos;
        } else if(inputSign == POS && outputSign == NEG) {
            return frequencyIPosONeg;
        } else if(inputSign == NEG && outputSign == POS) {
            return frequencyINegOPos;
        }

        //TODO:
        return Math.max(n - (frequencyIPosOPos + frequencyIPosONeg + frequencyINegOPos), 0);
    }

    public void setFrequency(Sign inputSign, Sign outputSign, double f) {
        if(inputSign == POS && outputSign == POS) {
            frequencyIPosOPos = f;
        } else if(inputSign == POS && outputSign == NEG) {
            frequencyIPosONeg = f;
        } else if(inputSign == NEG && outputSign == POS) {
            frequencyINegOPos = f;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void applyMovingAverage(double alpha) {
        sampleSpace.applyMovingAverage(alpha);
        frequencyIPosOPos *= alpha;
        frequencyIPosONeg *= alpha;
        frequencyINegOPos *= alpha;
    }

    public void updateFrequencyForIandO(boolean inputActive,boolean outputActive){
        if(inputActive && outputActive) {
            frequencyIPosOPos += 1.0;
        } else if(inputActive) {
            frequencyIPosONeg += 1.0;
        } else if(outputActive) {
            frequencyINegOPos += 1.0;
        }
    }

    public void count(Range absoluteRange, boolean inputActive, boolean outputActive) {

        double oldN = sampleSpace.getN();


        if(absoluteRange == null)
            return;

        sampleSpace.countSkippedInstances(
                absoluteRange,
                getInputValueByArg(0)
        );

        sampleSpace.count();

        if(outputActive) {
            if (alpha != null)
                applyMovingAverage(
                        Math.pow(alpha, sampleSpace.getN() - oldN)
                );
        }

        updateFrequencyForIandO(inputActive,outputActive);
        sampleSpace.updateLastPosition(absoluteRange);
    }

    public double getSurprisal(Sign inputSign, Sign outputSign, Range range, boolean addCurrentInstance) {
        double n = sampleSpace.getN(
                range,
                getInputValueByArg(0)
        );
        double probability = getProbability(inputSign, outputSign, n, addCurrentInstance);
        return -StatisticUtils.surprisal(probability);
    }

    public double getProbability(Sign inputSign, Sign outputSign, double n, boolean addCurrentInstance) {
        double frequency = getFrequency(inputSign, outputSign, n);

        // Add the current instance
        if(addCurrentInstance) {
            frequency += 1.0;
            n += 1.0;
        }

        return Bound.UPPER.probability(frequency, n);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(frequencyIPosOPos);
        out.writeDouble(frequencyIPosONeg);
        out.writeDouble(frequencyINegOPos);

        sampleSpace.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        frequencyIPosOPos = in.readDouble();
        frequencyIPosONeg = in.readDouble();
        frequencyINegOPos = in.readDouble();

        sampleSpace = SampleSpace.read(in);
    }
}