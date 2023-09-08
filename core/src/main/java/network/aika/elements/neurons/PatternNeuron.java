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
package network.aika.elements.neurons;

import network.aika.Model;
import network.aika.Thought;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.synapses.*;
import network.aika.enums.sign.Sign;
import network.aika.statistic.SampleSpace;
import network.aika.text.Range;
import network.aika.utils.Bound;
import network.aika.utils.Utils;
import network.aika.visitor.operator.LinkingOperator;
import network.aika.visitor.pattern.PatternVisitor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.enums.sign.Sign.POS;
import static network.aika.utils.NetworkUtils.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternNeuron extends ConjunctiveNeuron<PatternActivation> {

    protected double frequency;

    protected SampleSpace sampleSpace = new SampleSpace();

    public static NeuronProvider create(Model m, String label) {
        return create(m, label, false);
    }

    public static NeuronProvider create(Model m, String label, boolean abstr) {
        PatternNeuron pn = new PatternNeuron(m)
                .setLabel(label);

        if(abstr)
            makeAbstract(pn)
                    .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        return pn.getProvider();
    }

    public PatternNeuron(Model m) {
        super(m);
    }

    @Override
    public void startVisitor(LinkingOperator c, Activation act, Synapse targetSyn) {
        new PatternVisitor(act.getThought(), c)
                .start(act);
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new PatternCategorySynapse();
    }

    @Override
    public PatternActivation createActivation(Thought t) {
        return new PatternActivation(t.createActivationId(), t, this);
    }

    @Override
    public PatternCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(PatternCategoryInputSynapse.class);
    }

    @Override
    public PatternCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(PatternCategorySynapse.class);
    }

    @Override
    public void updateSumOfLowerWeights() {
    }

    public SampleSpace getSampleSpace() {
        return sampleSpace;
    }

    @Override
    public void count(PatternActivation act) {
        double oldN = sampleSpace.getN();

        Range absoluteRange = act.getAbsoluteCharRange();
        sampleSpace.countSkippedInstances(absoluteRange);

        sampleSpace.count();
        frequency += 1.0;

        Double alpha = act.getConfig().getAlpha();
        if (alpha != null)
            applyMovingAverage(
                    Math.pow(alpha, sampleSpace.getN() - oldN)
            );

        sampleSpace.updateLastPosition(absoluteRange);
        setModified();
    }

    public void applyMovingAverage(double alpha) {
        sampleSpace.applyMovingAverage(alpha);
        frequency *= alpha;
        setModified();
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
        setModified();
    }

    public double getSurprisal(Sign s, Range range, boolean addCurrentInstance) {
        double n = sampleSpace.getN(range);
        double p = getProbability(s, n, addCurrentInstance);
        return -Utils.surprisal(p);
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
        super.write(out);

        out.writeDouble(frequency);
        sampleSpace.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        frequency = in.readDouble();
        sampleSpace = SampleSpace.read(in, m);
    }
}
