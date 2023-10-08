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
package network.aika.elements.synapses;

import network.aika.Model;
import network.aika.elements.Type;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.links.PatternLink;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.enums.Scope;
import network.aika.text.Range;
import network.aika.statistic.SampleSpace;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.enums.sign.Sign;
import network.aika.utils.Bound;
import network.aika.utils.Utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Collectors;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.PATTERN;
import static network.aika.fields.Fields.isTrue;
import static network.aika.enums.sign.Sign.NEG;
import static network.aika.enums.sign.Sign.POS;


/**
 *
 * @author Lukas Molzberger
 */
public class PatternSynapse extends ConjunctiveSynapse<
        PatternSynapse,
        BindingNeuron,
        PatternNeuron,
        PatternLink,
        BindingActivation,
        PatternActivation
        >
{

    protected double frequencyIPosOPos;
    protected double frequencyIPosONeg;
    protected double frequencyINegOPos;

    protected SampleSpace sampleSpace = new SampleSpace();

    @Override
    public Type getInputType() {
        return BINDING;
    }

    @Override
    public Type getOutputType() {
        return PATTERN;
    }

    @Override
    public Scope getScope() {
        return Scope.SAME;
    }

    @Override
    public PatternLink createLink(BindingActivation input, PatternActivation output) {
        checkAlreadyLinkedToPattern(input);

        return new PatternLink(this, input, output);
    }

    private static void checkAlreadyLinkedToPattern(BindingActivation input) {
        if(input == null)
            return;

        if(input.getOutputLinksByType(PatternLink.class).count() > 0) {
            log.warn("Already linked to Pattern: " +
                    input.getOutputLinksByType(PatternLink.class)
                            .map(Link::getOutput)
                            .map(Activation::toString)
                            .collect(Collectors.joining(", "))
            );
        }
    }

    @Override
    public void delete() {
        super.delete();
        getInput().delete();
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
        setModified();
    }

    public void applyMovingAverage(double alpha) {
        sampleSpace.applyMovingAverage(alpha);
        frequencyIPosOPos *= alpha;
        frequencyIPosONeg *= alpha;
        frequencyINegOPos *= alpha;
        setModified();
    }

    public void updateFrequencyForIandO(boolean inputActive,boolean outputActive){
        if(inputActive && outputActive) {
            frequencyIPosOPos += 1.0;
            setModified();
        } else if(inputActive) {
            frequencyIPosONeg += 1.0;
            setModified();
        } else if(outputActive) {
            frequencyINegOPos += 1.0;
            setModified();
        }
    }

    @Override
    public void count(PatternLink l) {
        double oldN = sampleSpace.getN();

        if(l.getInput() == null)
            return; // TODO: fix

        boolean inputActive = isTrue(l.getInputPatternNet(), 0.0);
        boolean outputActive = isTrue(l.getOutput().getNet(), 0.0);

        Range absoluteRange = l.getInput().getAbsoluteCharRange();
        if(absoluteRange == null)
            return;

        sampleSpace.countSkippedInstances(absoluteRange);

        sampleSpace.count();

        if(outputActive) {
            Double alpha = l.getConfig().getAlpha();
            if (alpha != null)
                applyMovingAverage(
                        Math.pow(alpha, sampleSpace.getN() - oldN)
                );
        }

        updateFrequencyForIandO(inputActive,outputActive);
        sampleSpace.updateLastPosition(absoluteRange);
    }

    public double getSurprisal(Sign inputSign, Sign outputSign, Range range, boolean addCurrentInstance) {
        double n = sampleSpace.getN(range);
        double probability = getProbability(inputSign, outputSign, n, addCurrentInstance);
        return -Utils.surprisal(probability);
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
        super.write(out);

        out.writeDouble(frequencyIPosOPos);
        out.writeDouble(frequencyIPosONeg);
        out.writeDouble(frequencyINegOPos);

        sampleSpace.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        frequencyIPosOPos = in.readDouble();
        frequencyIPosONeg = in.readDouble();
        frequencyINegOPos = in.readDouble();

        sampleSpace = SampleSpace.read(in, m);
    }
}
