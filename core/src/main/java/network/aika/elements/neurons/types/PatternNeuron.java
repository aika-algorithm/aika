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
package network.aika.elements.neurons.types;

import network.aika.Model;
import network.aika.Document;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.NeuronType;
import network.aika.elements.neurons.RefType;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.types.PatternCategoryInputSynapse;
import network.aika.elements.synapses.types.PatternCategorySynapse;
import network.aika.enums.sign.Sign;
import network.aika.statistic.AverageCoveredSpace;
import network.aika.statistic.SampleSpace;
import network.aika.text.Range;
import network.aika.utils.Bound;
import network.aika.utils.Utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.Type.PATTERN;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.MULTI_INPUT;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_SAME;
import static network.aika.elements.neurons.RefType.*;
import static network.aika.enums.sign.Sign.POS;
import static network.aika.text.Range.length;

/**
 *
 * @author Lukas Molzberger
 */
@NeuronType(
        type = PATTERN,
        activationFunction = RECTIFIED_HYPERBOLIC_TANGENT,
        bindingSignalSlots = {SINGLE_SAME, MULTI_INPUT}
)
public class PatternNeuron extends ConjunctiveNeuron<PatternNeuron, PatternActivation> {

    protected double frequency;

    protected SampleSpace sampleSpace = new SampleSpace();

    private AverageCoveredSpace averageCoveredSpace;


    public PatternNeuron(NeuronProvider np) {
        super(np);
    }

    public PatternNeuron(Model m, RefType rt) {
        super(m, rt);
    }

    public static PatternNeuron create(Model m, String label) {
        return new PatternNeuron(m, NEURON_EXTERNAL)
                .setLabel(label)
                .setPersistent(true);
    }

    @Override
    public PatternCategoryInputSynapse makeAbstract() {
        PatternCategoryNeuron patternCategory = new PatternCategoryNeuron(getModel(), CATEGORY)
                .setLabel(getCategoryLabel(getLabel()));

        PatternCategoryInputSynapse s = new PatternCategoryInputSynapse()
                .link(patternCategory, this);

        s.setInitialCategorySynapseWeight(1.0);

        patternCategory.getProvider().decreaseRefCount(CATEGORY);
        return s;
    }

    public static String getCategoryLabel(String placeholder) {
        return placeholder + CATEGORY_LABEL;
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new PatternCategorySynapse();
    }

    @Override
    public PatternActivation createActivation(Document doc) {
        return new PatternActivation(doc.createActivationId(), doc, this);
    }

    @Override
    public PatternCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(PatternCategoryInputSynapse.class);
    }

    @Override
    public PatternCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(PatternCategorySynapse.class);
    }

    public AverageCoveredSpace getAverageCoveredSpace() {
        if(averageCoveredSpace == null)
            averageCoveredSpace = new AverageCoveredSpace();
        return averageCoveredSpace;
    }

    public SampleSpace getSampleSpace() {
        return sampleSpace;
    }

    @Override
    public void count(PatternActivation act) {
        super.count(act);

        double oldN = sampleSpace.getN();

        Range absoluteRange = act.getAbsoluteCharRange();

        sampleSpace.countSkippedInstances(
                absoluteRange,
                getAvgCoveredSpaceFromTemplate(absoluteRange)
        );

        sampleSpace.count();
        frequency += 1.0;

        Double alpha = act.getConfig().getAlpha();
        if (alpha != null)
            applyMovingAverage(
                    Math.pow(alpha, sampleSpace.getN() - oldN)
            );

        sampleSpace.updateLastPosition(absoluteRange);
        setModified();

        PatternNeuron tn = getTemplate();
        if(tn != null)
            tn.getAverageCoveredSpace()
                    .count(absoluteRange);
    }

    public Double getAvgCoveredSpaceFromTemplate(Range absoluteRange) {
        PatternNeuron tn = getTemplate();

        Double result = null;
        if(tn != null)
            result = tn.getAverageCoveredSpace().getAvgCoveredSpace();

        return result != null ? result : length(absoluteRange);
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
        double n = sampleSpace.getN(range, this);
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

        out.writeBoolean(averageCoveredSpace != null);
        if(averageCoveredSpace != null)
            averageCoveredSpace.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        frequency = in.readDouble();
        sampleSpace = SampleSpace.read(in, m);

        if(in.readBoolean())
            averageCoveredSpace = AverageCoveredSpace.read(in, m);
    }
}
