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
import network.aika.Range;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.NeuronType;
import network.aika.elements.neurons.RefType;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.types.PatternCategoryInputSynapse;
import network.aika.elements.synapses.types.PatternCategorySynapse;
import network.aika.fields.link.FieldLink;
import network.aika.statistic.AverageCoveredSpace;
import network.aika.statistic.NeuronStatistic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.PATTERN;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.MULTI_INPUT;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_SAME;
import static network.aika.elements.neurons.RefType.*;
import static network.aika.utils.ToleranceUtils.TOLERANCE;

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

    private AverageCoveredSpace averageCoveredSpace;

    private NeuronStatistic statistic = new NeuronStatistic(
            this,
            "statistic",
            getConfig().getAlpha(),
            TOLERANCE
    );

    public PatternNeuron(NeuronProvider np) {
        super(np);

        init();
    }

    public PatternNeuron(Model m, RefType rt) {
        super(m, rt);

        init();
    }

    public void init() {
        FieldLink.linkAndConnect(
                getTemplate().averageCoveredSpace,
                statistic
        );
    }

    public static PatternNeuron create(Model m, String label) {
        return new PatternNeuron(m, NEURON_EXTERNAL)
                .setLabel(label)
                .setPersistent(true);
    }

    @Override
    public PatternCategoryNeuron createCategoryNeuron() {
        return new PatternCategoryNeuron(getModel(), CATEGORY)
                .setLabel(getCategoryLabel(getLabel()));
    }

    @Override
    public PatternCategoryInputSynapse createCategoryInputSynapse() {
        return new PatternCategoryInputSynapse();
    }

    public NeuronStatistic getStatistic() {
        return statistic;
    }

    public AverageCoveredSpace getAverageCoveredSpace() {
        if(averageCoveredSpace == null)
            averageCoveredSpace = new AverageCoveredSpace(this, "avgCoveredSpace");
        return averageCoveredSpace;
    }

    public static String getCategoryLabel(String placeholder) {
        return placeholder + CATEGORY_LABEL;
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new PatternCategorySynapse();
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
    public void count(PatternActivation act) {
        Range absRange = act.getAbsoluteCharRange();
        statistic.count(absRange);

        setModified();

        PatternNeuron tn = getTemplate();
        if(tn != null)
            tn.getAverageCoveredSpace()
                    .count(absRange);
    }

    public void setFrequency(double f) {
        statistic.setFrequency(f);
        setModified();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        statistic.write(out);

        out.writeBoolean(averageCoveredSpace != null);
        if(averageCoveredSpace != null)
            averageCoveredSpace.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        statistic.readFields(in);

        if(in.readBoolean())
            getAverageCoveredSpace().readFields(in);
    }
}
