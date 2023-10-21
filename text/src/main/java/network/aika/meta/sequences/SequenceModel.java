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
package network.aika.meta.sequences;

import network.aika.Model;
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.ContainsRelation;
import network.aika.elements.neurons.LatentRelationNeuron;
import network.aika.elements.neurons.relations.BeforeRelation;
import network.aika.meta.Dictionary;
import network.aika.text.Range;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class SequenceModel implements Writable {

    private static final Logger log = LoggerFactory.getLogger(SequenceModel.class);

    public static final double PATTERN_NET_TARGET = 0.7;
    public static final double POS_MARGIN = 1.0;
    public static final double NEG_MARGIN_LEFT = 1.2;
    public static final double NEG_MARGIN_RIGHT = 1.1;


    protected Model model;

    protected Dictionary dictionary;

    public LatentRelationNeuron relPT;
    public LatentRelationNeuron relNT;

    protected LatentRelationNeuron relContains;

    public OuterInhibitoryNeuron outerInhibitoryN;

    public InnerInhibitoryNeuron primaryBNInhibitoryN;

    protected OuterInhibitoryCategoryNeuron inhibCat;

    public PatternNeuron sequencePatternN;

    public BindingNeuron primaryBN;

    public BindingNeuron subPhraseBN;


    record BindingNeuronParameters (
            double patternNetTarget,
            double netTarget,
            double iosWeight,
            double sosRelWeight,
            double pfWeight,
            double weakInputMargin,
            String labelPrefix
    ) {}

    public static BindingNeuronParameters PRIMARY_BN_PARAMS = new BindingNeuronParameters(
            PATTERN_NET_TARGET,
            2.5,
            11.0,
            0.0,
            2.5,
            0.0,
            "Primary"
    );

    public static BindingNeuronParameters STRONG_BN_PARAMS = new BindingNeuronParameters(
            PATTERN_NET_TARGET,
            2.5,
            9.0,
            10.0,
            2.5,
            0.0,
            "Strong"
    );

    public static BindingNeuronParameters WEAK_BN_PARAMS = new BindingNeuronParameters(
            PATTERN_NET_TARGET,
            0.5,
            9.0,
            5.0,
            0.5,
            -0.05,
            "Weak"
    );

    public SequenceModel(Model m, Dictionary dict) {
        model = m;
        dictionary = dict;
    }

    public Model getModel() {
        return model;
    }

    public LatentRelationNeuron getRelationPreviousToken() {
        return relPT;
    }

    public LatentRelationNeuron getRelationNextToken() {
        return relNT;
    }

    public OuterInhibitoryNeuron getOuterInhibitoryNeuron() {
        return outerInhibitoryN;
    }

    public OuterInhibitoryCategoryNeuron getInhibitoryCategory() {
        return inhibCat;
    }

    public PatternNeuron getPatternNeuron() {
        return sequencePatternN;
    }

    public abstract String getPatternType();

    public void initStaticNeurons() {
        relPT = new LatentRelationNeuron(
                model,
                new BeforeRelation(
                        INPUT,
                        new Range(0, 0)
                )
        )
                .setLabel("Prev. Token Rel.: -1,-1")
                .setBias(5.0);

        relNT = new LatentRelationNeuron(
                model,
                new BeforeRelation(
                        OUTPUT,
                        new Range(0, 0)
                )
        )
                .setLabel("Next. Token Rel.: 1,1")
                .setBias(5.0);

        // Abstract
        sequencePatternN = PatternNeuron.create(model, getPatternType());
        sequencePatternN.setBias(PATTERN_NET_TARGET);
        sequencePatternN.setTargetNet(PATTERN_NET_TARGET);
        sequencePatternN.makeAbstract()
                .setWeight(10.0)
                .adjustBias();

        outerInhibitoryN = new OuterInhibitoryNeuron(model)
                .setLabel("I")
                .setPersistent(true);

        outerInhibitoryN.makeAbstract()
                .setWeight(1.0);


        primaryBNInhibitoryN = new InnerInhibitoryNeuron(model)
                .setLabel("I")
                .setPersistent(true);

        primaryBNInhibitoryN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        log.info(getPatternType() + " Pattern: netTarget:" + PATTERN_NET_TARGET);

        relContains = new LatentRelationNeuron(
                model,
                new ContainsRelation(OUTPUT)
        )
                .setLabel("Contains Rel.: ")
                .setBias(5.0)
                .setTargetNet(5.0);

        initTemplateBindingNeurons();
    }

    protected abstract void initTemplateBindingNeurons();

    protected BindingNeuron createSubPhraseBindingNeuron() {
        BindingNeuron bn = addBindingNeuron(
                sequencePatternN,
                "Abstract SubPhrase",
                10.0,
                2.5
        );
        bn.makeAbstract()
                .setWeight(DEFAULT_INPUT_CATEGORY_SYNAPSE_WEIGHT)
                .adjustBias();

        addPositiveFeedbackLoop(
                bn,
                sequencePatternN,
                2.5,
                0.0,
                false,
                true
        );

        return bn;
    }

    protected BindingNeuron createPrimaryBindingNeuron() {
        BindingNeuron bn = createBindingNeuron(PRIMARY_BN_PARAMS, 0, true, false);

        double patternValueTarget = sequencePatternN.getActivationFunction()
                .f(PRIMARY_BN_PARAMS.patternNetTarget);

        addInnerInhibitoryLoop(
                bn,
                primaryBNInhibitoryN,
                -getMaxBindingNetTarget(PRIMARY_BN_PARAMS.netTarget, patternValueTarget)
        );

        return bn;
    }

    protected void expandContinueBindingNeurons(
            int optionalStart,
            BindingNeuron sylBeginBN,
            int length,
            int dir
    ) {
        BindingNeuron lastSylBN = sylBeginBN;
        for(int pos = 1; pos <= length; pos++) {
            BindingNeuronParameters p;

            p = pos < 2 ?
                    STRONG_BN_PARAMS :
                    WEAK_BN_PARAMS;

            lastSylBN = createSecondaryBindingNeuron(
                    p,
                    pos >= optionalStart,
                    dir * pos,
                    lastSylBN
            );
        }
    }

    protected BindingNeuron createSecondaryBindingNeuron(
            BindingNeuronParameters p,
            boolean isOptional,
            int pos,
            BindingNeuron lastBN
    ) {
        BindingNeuron bn = createBindingNeuron(p, pos, false, isOptional);

        LatentRelationNeuron rel = pos > 0 ?
                relPT :
                relNT;

        addRelation(
                lastBN,
                bn,
                rel,
                5.0,
                p.sosRelWeight,
                false
        );

        return bn;
    }

    protected BindingNeuron createBindingNeuron(
            BindingNeuronParameters p,
            int pos,
            boolean allowRelaxedMatching,
            boolean isOptional
    ) {
        log.info(p.labelPrefix + " Binding-Neuron: netTarget:" + p.netTarget);

        BindingNeuron bn = addBindingNeuron(
                dictionary.getInputToken(),
                "Abstract (" + p.labelPrefix + ") Pos:" + pos,
                p.iosWeight,
                p.netTarget
        );
        bn.makeAbstract()
                .setWeight(DEFAULT_INPUT_CATEGORY_SYNAPSE_WEIGHT)
                .adjustBias();

        addOuterInhibitoryLoop(
                bn,
                outerInhibitoryN,
                getNegMargin(pos) * -bn.getTargetNet()
        );

        addPositiveFeedbackLoop(
                bn,
                sequencePatternN,
                p.pfWeight,
                p.weakInputMargin,
                allowRelaxedMatching,
                isOptional
        );

        return bn;
    }

    private double getNegMargin(int pos) {
        return pos >= 0 ?
                NEG_MARGIN_RIGHT :
                NEG_MARGIN_LEFT;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(relPT.getId());
        out.writeLong(relNT.getId());
        out.writeLong(relContains.getId());
        out.writeLong(outerInhibitoryN.getId());
        out.writeLong(primaryBNInhibitoryN.getId());
        out.writeLong(inhibCat.getId());
        out.writeLong(sequencePatternN.getId());
        out.writeLong(primaryBN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        relPT = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relNT = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relContains = m.lookupNeuronProvider(in.readLong()).getNeuron();
        outerInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        primaryBNInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        inhibCat = m.lookupNeuronProvider(in.readLong()).getNeuron();
        sequencePatternN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        primaryBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
