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
import network.aika.elements.neurons.types.*;
import network.aika.elements.relations.ContainsRelation;
import network.aika.elements.relations.BeforeRelation;
import network.aika.elements.synapses.types.InhibitorySynapse;
import network.aika.elements.synapses.types.PrimaryInhibitorySynapse;
import network.aika.meta.Dictionary;
import network.aika.text.Range;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.neurons.RefType.TEMPLATE_MODEL;
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

    public InhibitoryNeuron inhibitoryN;

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
            String labelPrefix,
            boolean isPrimary
    ) {}

    public static BindingNeuronParameters PRIMARY_BN_PARAMS = new BindingNeuronParameters(
            PATTERN_NET_TARGET,
            2.5,
            11.0,
            0.0,
            2.5,
            0.0,
            "Primary",
            true
    );

    public static BindingNeuronParameters STRONG_BN_PARAMS = new BindingNeuronParameters(
            PATTERN_NET_TARGET,
            2.5,
            9.0,
            10.0,
            2.5,
            0.0,
            "Strong",
            false
    );

    public static BindingNeuronParameters WEAK_BN_PARAMS = new BindingNeuronParameters(
            PATTERN_NET_TARGET,
            1.6,
            9.0,
            5.0,
            0.5,
            -0.05,
            "Weak",
            false
    );

    public SequenceModel(Model m) {
        model = m;
    }

    public void initModelDependencies(Dictionary dict) {
        this.dictionary = dict;
    }

    public Model getModel() {
        return model;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public LatentRelationNeuron getRelationPreviousToken() {
        return relPT;
    }

    public LatentRelationNeuron getRelationNextToken() {
        return relNT;
    }

    public InhibitoryNeuron getInhibitoryNeuron() {
        return inhibitoryN;
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
                ),
                TEMPLATE_MODEL
        )
                .setLabel("Prev. Token Rel.: -1,-1")
                .setBias(5.0);

        relNT = new LatentRelationNeuron(
                model,
                new BeforeRelation(
                        OUTPUT,
                        new Range(0, 0)
                ),
                TEMPLATE_MODEL
        )
                .setLabel("Next. Token Rel.: 1,1")
                .setBias(5.0);

        // Abstract
        sequencePatternN = PatternNeuron.create(model, getPatternType());
        sequencePatternN.setBias(PATTERN_NET_TARGET);
        sequencePatternN.setTargetNet(PATTERN_NET_TARGET);
        sequencePatternN.makeAbstract(false)
                .setWeight(10.0)
                .adjustBias();

        inhibitoryN = new InhibitoryNeuron(model, TEMPLATE_MODEL)
                .setLabel("I")
                .setPersistent(true);

        inhibitoryN.makeAbstract(false)
                .setWeight(1.0);

        new PrimaryInhibitorySynapse()
                .setWeight(1.0)
                .link(dictionary.getInputToken(), inhibitoryN);

        log.info(getPatternType() + " Pattern: netTarget:" + PATTERN_NET_TARGET);

        relContains = new LatentRelationNeuron(
                model,
                new ContainsRelation(OUTPUT),
                TEMPLATE_MODEL
        )
                .setLabel("Contains Rel.: ")
                .setBias(5.0)
                .setTargetNet(5.0);

        initTemplateBindingNeurons();
    }

    public void setInstantiable(boolean instantiable) {
        primaryBN.setInstantiable(instantiable);
        sequencePatternN.setInstantiable(instantiable);
        inhibitoryN.setInstantiable(instantiable);
    }

    protected abstract void initTemplateBindingNeurons();

    protected BindingNeuron createSubPhraseBindingNeuron() {
        BindingNeuron bn = addBindingNeuron(
                sequencePatternN,
                "Abstract SubPhrase",
                10.0,
                2.5,
                true
        );
        bn.makeAbstract(false)
                .setWeight(getDefaultInputCategorySynapseWeight(bn.getType()))
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
        return createBindingNeuron(PRIMARY_BN_PARAMS, 0, true, false)
                .setPrimary(true);
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
                p.sosRelWeight
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
                p.netTarget,
                p.isPrimary
        );
        bn.makeAbstract(false)
                .setWeight(getDefaultInputCategorySynapseWeight(bn.getType()))
                .adjustBias();

        addInhibitoryLoop(
                bn,
                inhibitoryN,
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

    public void initOuterSynapses() {

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
        out.writeLong(inhibitoryN.getId());
        out.writeLong(sequencePatternN.getId());
        out.writeLong(primaryBN.getId());
        out.writeLong(subPhraseBN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        relPT = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
        relNT = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
        relContains = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
        inhibitoryN = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
        sequencePatternN = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
        primaryBN = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
        subPhraseBN = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
    }
}
