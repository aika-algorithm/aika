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
import network.aika.elements.synapses.PatternCategorySynapse;
import network.aika.enums.direction.Direction;
import network.aika.enums.direction.Input;
import network.aika.enums.sign.Sign;
import network.aika.meta.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static network.aika.meta.NetworkMotifs.*;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class SequenceModel {

    private static final Logger log = LoggerFactory.getLogger(SequenceModel.class);

    protected Model model;

    protected Dictionary dictionary;

    protected NeuronProvider targetInput;

    protected NeuronProvider targetInputCategory;

    public NeuronProvider relPT;
    public NeuronProvider relNT;

    protected NeuronProvider relContains;

    public NeuronProvider outerInhibitoryN;

    public NeuronProvider primaryBNInhibitoryN;

    protected NeuronProvider inhibCat;

    public NeuronProvider patternN;

    public NeuronProvider primaryBN;

    public static double patternNetTarget = 0.7;

    protected double targetInputNetTarget = 5.0;

    public static double POS_MARGIN = 1.0;
    public static double NEG_MARGIN_LEFT = 1.2;
    public static double NEG_MARGIN_RIGHT = 1.1;


    record BindingNeuronParameters (
            double patternNetTarget,
            double netTarget,
            double spsRelWeight,
            double pfWeight,
            double weakInputMargin,
            String labelPrefix
    ) {}

    public static BindingNeuronParameters PRIMARY_BN_PARAMS = new BindingNeuronParameters(
            patternNetTarget,
            2.5,
            0.0,
            2.5,
            0.0,
            "Primary"
    );

    public static BindingNeuronParameters STRONG_BN_PARAMS = new BindingNeuronParameters(
            patternNetTarget,
            2.5,
            10.0,
            2.5,
            0.0,
            "Strong"
    );

    public static BindingNeuronParameters WEAK_BN_PARAMS = new BindingNeuronParameters(
            patternNetTarget,
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

        initTargetInput();
    }

    protected void initTargetInput() {
        targetInput = model.lookupNeuronByLabel("Abstract Target Input", l ->
                new TokenNeuron()
                        .init(model, l)
        ).getProvider(true);

        targetInput.getNeuron()
                .setBias(targetInputNetTarget);

        targetInputCategory = makeAbstract((PatternNeuron) targetInput.getNeuron())
                .getProvider(true);
    }


    public void initTemplates() {
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

        log.info(getPatternType() + " Pattern: netTarget:" + patternNetTarget);

        patternN.getNeuron().setBias(patternNetTarget);

        initTemplateBindingNeurons();
    }

    protected abstract void initTemplateBindingNeurons();

    protected BindingNeuron createPrimaryBindingNeuron() {
        BindingNeuron bn = createBindingNeuron(PRIMARY_BN_PARAMS, 0, false);

        addInnerInhibitoryLoop(
                bn,
                primaryBNInhibitoryN.getNeuron(),
                -(PRIMARY_BN_PARAMS.netTarget + 0.1)
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

            lastSylBN = createSecundaryBindingNeuron(
                    p,
                    pos >= optionalStart,
                    dir * pos,
                    lastSylBN
            );
        }
    }

    public TokenNeuron createTargetInputNeuron(String label) {
        TokenNeuron targetInputN = targetInput.getNeuron();
        TokenNeuron n = targetInputN.instantiateTemplate()
                .init(model, label);

        n.setTokenLabel(label);
        n.setAllowTraining(false);

        return n;
    }

    protected BindingNeuron createTargetInputBindingNeuron() {
        double netTarget = 2.5;

        BindingNeuron bn = addBindingNeuron(
                targetInput.getNeuron(),
                "Abstract Target Input",
                10.0,
                dictionary.getInputPatternNetTarget(),
                netTarget
        );
        makeAbstract(bn);

        addPositiveFeedbackLoop(
                bn,
                patternN.getNeuron(),
                patternNetTarget,
                netTarget,
                2.5,
                0.0,
                false
        );

        relContains = ContainsRelationNeuron.lookupRelation(model, Direction.INPUT)
                .setBias(5.0)
                .getProvider(true);

        addRelation(
                bn,
                primaryBN.getNeuron(),
                relContains.getNeuron(),
                5.0,
                10.0
        );

        return bn;
    }

    protected void applyMarginToInnerNegFeedbackSynapse(BindingNeuron primBN) {
        double margin = primBN.getDeltaBetweenTargetAndMax();
        InnerNegativeFeedbackSynapse innerNegSyn = primBN.getInputSynapseByType(InnerNegativeFeedbackSynapse.class);
        innerNegSyn.setWeight(innerNegSyn.getWeight().getValue() - margin);
    }

    protected BindingNeuron createSecundaryBindingNeuron(
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
                p.spsRelWeight
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
                "Abstract (" + p.labelPrefix + ") Pos:" + pos,
                10.0,
                dictionary.getInputPatternNetTarget(),
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
                patternNetTarget,
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

    public Model getModel() {
        return model;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }
}
