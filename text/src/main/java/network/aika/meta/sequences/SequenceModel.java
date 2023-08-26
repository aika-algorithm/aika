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
import network.aika.elements.neurons.relations.ContainsRelationNeuron;
import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.elements.neurons.relations.BeforeRelationNeuron;
import network.aika.elements.synapses.InnerNegativeFeedbackSynapse;
import network.aika.enums.Scope;
import network.aika.enums.direction.Direction;
import network.aika.meta.Dictionary;
import network.aika.meta.TargetInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.meta.Dictionary.INPUT_TOKEN_NET_TARGET;
import static network.aika.meta.NetworkMotifs.*;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class SequenceModel {

    private static final Logger log = LoggerFactory.getLogger(SequenceModel.class);

    public static final double PATTERN_NET_TARGET = 0.7;

    public static final double POS_MARGIN = 1.0;
    public static final double NEG_MARGIN_LEFT = 1.2;
    public static final double NEG_MARGIN_RIGHT = 1.1;


    protected Model model;

    protected Dictionary dictionary;

    protected TargetInput targetInput;

    public NeuronProvider relPT;
    public NeuronProvider relNT;

    protected NeuronProvider relContains;

    public NeuronProvider outerInhibitoryN;

    public NeuronProvider primaryBNInhibitoryN;

    protected NeuronProvider inhibCat;

    public NeuronProvider patternN;

    public NeuronProvider primaryBN;


    record BindingNeuronParameters (
            double patternNetTarget,
            double netTarget,
            double spsRelWeight,
            double pfWeight,
            double weakInputMargin,
            String labelPrefix
    ) {}

    public static BindingNeuronParameters PRIMARY_BN_PARAMS = new BindingNeuronParameters(
            PATTERN_NET_TARGET,
            2.5,
            0.0,
            2.5,
            0.0,
            "Primary"
    );

    public static BindingNeuronParameters STRONG_BN_PARAMS = new BindingNeuronParameters(
            PATTERN_NET_TARGET,
            2.5,
            10.0,
            2.5,
            0.0,
            "Strong"
    );

    public static BindingNeuronParameters WEAK_BN_PARAMS = new BindingNeuronParameters(
            PATTERN_NET_TARGET,
            0.5,
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

    public TargetInput getTargetInput() {
        return targetInput;
    }

    public NeuronProvider getRelationPreviousToken() {
        return relPT;
    }

    public NeuronProvider getRelationNextToken() {
        return relNT;
    }

    public NeuronProvider getOuterInhibitoryNeuron() {
        return outerInhibitoryN;
    }

    public NeuronProvider getInhibitoryCategory() {
        return inhibCat;
    }

    public NeuronProvider getPatternNeuron() {
        return patternN;
    }

    public abstract String getPatternType();

    public void initStaticNeurons() {
        relPT = BeforeRelationNeuron.lookupRelation(model, -1, -1)
                .setBias(5.0)
                .getProvider(true);

        relNT = BeforeRelationNeuron.lookupRelation(model, 1, 1)
                .setBias(5.0)
                .getProvider(true);

        targetInput = new TargetInput(model, "Phrase");
        targetInput.initTargetInput();
        initTemplates();
        targetInput.setTemplateOnly(true);
    }

    protected void initTemplates() {
        // Abstract
        patternN = new PatternNeuron()
                .init(model, getPatternType())
                .getProvider(true);

        makeAbstract((PatternNeuron) patternN.getNeuron());


        outerInhibitoryN = new OuterInhibitoryNeuron()
                .init(model, "I")
                .getProvider(true);

        makeAbstract((OuterInhibitoryNeuron) outerInhibitoryN.getNeuron());


        primaryBNInhibitoryN = new InnerInhibitoryNeuron()
                .init(model, "I")
                .getProvider(true);

        makeAbstract((InnerInhibitoryNeuron) primaryBNInhibitoryN.getNeuron());

        log.info(getPatternType() + " Pattern: netTarget:" + PATTERN_NET_TARGET);

        patternN.getNeuron().setBias(PATTERN_NET_TARGET);

        initTemplateBindingNeurons();
    }

    protected abstract void initTemplateBindingNeurons();

    protected BindingNeuron createPrimaryBindingNeuron() {
        BindingNeuron bn = createBindingNeuron(PRIMARY_BN_PARAMS, 0, false);

        addInnerInhibitoryLoop(
                bn,
                primaryBNInhibitoryN.getNeuron(),
                -(1.5 * PRIMARY_BN_PARAMS.netTarget)
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

    protected BindingNeuron createTargetInputBindingNeuron() {
        BindingNeuron bn = targetInput.createTargetInputBindingNeuron(
                patternN.getNeuron(),
                PATTERN_NET_TARGET
        );

        relContains = ContainsRelationNeuron.lookupRelation(model, Direction.INPUT)
                .setBias(5.0)
                .getProvider(true);

        addRelation(
                bn,
                primaryBN.getNeuron(),
                relContains.getNeuron(),
                5.0,
                10.0,
                true
        );

        return bn;
    }

    protected void applyMarginToInnerNegFeedbackSynapse(BindingNeuron primBN) {
        double margin = primBN.getDeltaBetweenTargetAndMax();
        InnerNegativeFeedbackSynapse innerNegSyn = primBN.getInputSynapseByType(InnerNegativeFeedbackSynapse.class);
        innerNegSyn.setWeight(innerNegSyn.getWeight().getValue() - margin);
    }

    protected BindingNeuron createSecondaryBindingNeuron(
            BindingNeuronParameters p,
            boolean isOptional,
            int pos,
            BindingNeuron lastBN
    ) {
        BindingNeuron bn = createBindingNeuron(p, pos, isOptional);

        LatentRelationNeuron rel = pos > 0 ?
                relPT.getNeuron() :
                relNT.getNeuron();

        addRelation(
                lastBN,
                bn,
                rel,
                5.0,
                p.spsRelWeight,
                false
        );

        return bn;
    }

    protected BindingNeuron createBindingNeuron(
            BindingNeuronParameters p,
            int pos,
            boolean isOptional
    ) {
        log.info(p.labelPrefix + " Binding-Neuron: netTarget:" + p.netTarget);

        BindingNeuron bn = addBindingNeuron(
                dictionary.getInputToken().getNeuron(),
                Scope.INPUT,
                "Abstract (" + p.labelPrefix + ") Pos:" + pos,
                10.0,
                INPUT_TOKEN_NET_TARGET,
                p.netTarget
        );
        makeAbstract(bn);

        addOuterInhibitoryLoop(
                bn,
                outerInhibitoryN.getNeuron(),
                getNegMargin(pos) * -p.netTarget
        );

        addPositiveFeedbackLoop(
                bn,
                patternN.getNeuron(),
                PATTERN_NET_TARGET,
                p.netTarget,
                p.pfWeight,
                p.weakInputMargin,
                isOptional
        );

        return bn;
    }

    private double getNegMargin(int pos) {
        return pos >= 0 ?
                NEG_MARGIN_RIGHT :
                NEG_MARGIN_LEFT;
    }
}
