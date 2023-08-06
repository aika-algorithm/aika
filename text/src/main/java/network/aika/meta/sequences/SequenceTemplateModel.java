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
import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.elements.neurons.relations.BeforeRelationNeuron;
import network.aika.elements.synapses.PatternCategorySynapse;
import network.aika.enums.sign.Sign;
import network.aika.meta.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static network.aika.meta.NetworkMotivs.*;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class SequenceTemplateModel {

    private static final Logger log = LoggerFactory.getLogger(SequenceTemplateModel.class);

    protected Model model;

    protected Dictionary dictionary;

    public NeuronProvider relPT;
    public NeuronProvider relNT;

    public NeuronProvider outerInhibitoryN;

    public NeuronProvider primaryBNInhibitoryN;

    protected NeuronProvider inhibCat;

    public NeuronProvider patternN;

    public NeuronProvider primaryBN;

    protected double patternNetTarget = 0.7;

    public static double POS_MARGIN = 1.0;
    public static double NEG_MARGIN_LEFT = 1.2;
    public static double NEG_MARGIN_RIGHT = 1.1;

    public SequenceTemplateModel(Model m, Dictionary dict) {
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


    public void initInputTokenWeights() {
        model.getAllNeurons()
                .map(NeuronProvider::getNeuron)
                .filter(TokenNeuron.class::isInstance)
                .map(TokenNeuron.class::cast)
                .map(n -> n.getOutputSynapseByType(PatternCategorySynapse.class))
                .filter(Objects::nonNull)
                .forEach(this::mapSurprisalToWeight);
    }

    private void mapSurprisalToWeight(PatternCategorySynapse s) {
        double surprisal = s.getInput().getSurprisal(Sign.POS, null, false);

        double weight = 1.0 + (-0.1 * surprisal);
        s.setWeight(weight);

        log.debug("Set category synapse weight for token: " + s.getInput().getLabel() + " (weight: " + weight + " surprisal: " + surprisal + ")");
    }

    public abstract String getPatternType();

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

    public void initStaticNeurons() {
        relPT = BeforeRelationNeuron.lookupRelation(model, -1, -1)
                .getProvider(true);

        relNT = BeforeRelationNeuron.lookupRelation(model, 1, 1)
                .getProvider(true);

        dictionary.initStaticNeurons();
    }

    protected abstract void initTemplateBindingNeurons();

    protected void expandContinueBindingNeurons(
            double patternNetTarget,
            int optionalStart,
            BindingNeuron sylBeginBN,
            int length,
            int dir
    ) {
        BindingNeuron lastSylBN = sylBeginBN;
        int lastPos = 0;
        for(int pos = 1; pos <= length; pos++) {
            if(pos < 2) {
                lastSylBN = createStrongBindingNeuron(
                        patternNetTarget,
                        pos >= optionalStart,
                        dir * pos,
                        lastSylBN
                );
            } else {
                lastSylBN = createWeakBindingNeuron(
                        patternNetTarget,
                        dir * pos,
                        lastSylBN
                );
            }
            lastPos = pos;
        }
    }

    protected BindingNeuron createStrongBindingNeuron(
            double patternNetTarget,
            boolean isOptional,
            int pos,
            BindingNeuron lastBN
    ) {
        double netTarget = 2.5;

        log.info("Strong Binding-Neuron: netTarget:" + netTarget);

        BindingNeuron bn = addBindingNeuron(
                dictionary.getInputToken().getNeuron(),
                "Abstract (S) Pos:" + pos,
                10.0,
                dictionary.getInputPatternNetTarget(),
                netTarget
        );
        makeAbstract(bn);

        addOuterInhibitoryLoop(
                bn,
                outerInhibitoryN.getNeuron(),
                getNegMargin(pos) * -netTarget
        );

        if(pos != 0) {
            LatentRelationNeuron rel = pos > 0 ?
                    relPT.getNeuron() :
                    relNT.getNeuron();

            addRelation(
                    lastBN,
                    bn,
                    rel,
                    5.0,
                    10.0
            );
        } else {
            addInnerInhibitoryLoop(
                    bn,
                    primaryBNInhibitoryN.getNeuron(),
                    getNegMargin(pos) * -netTarget
            );
        }

        addPositiveFeedbackLoop(
                bn,
                patternN.getNeuron(),
                2.5,
                patternNetTarget,
                netTarget,
                0.0,
                isOptional
        );

        log.info("");

        return bn;
    }

    protected BindingNeuron createWeakBindingNeuron(
            double patternNetTarget,
            int pos,
            BindingNeuron lastBN
    ) {
        double weakInputMargin = -0.05;

        double netTarget = 0.5;

        log.info("Weak Binding-Neuron: netTarget:" + netTarget);

        BindingNeuron bn = addBindingNeuron(
                dictionary.getInputToken().getNeuron(),
                "Abstract (W) Pos:" + pos,
                10.0,
                dictionary.getInputPatternNetTarget(),
                netTarget
        );

        makeAbstract(bn);

        addOuterInhibitoryLoop(
                bn,
                outerInhibitoryN.getNeuron(),
                getNegMargin(pos) * -netTarget
        );

        LatentRelationNeuron rel = pos > 0 ?
                relPT.getNeuron() :
                relNT.getNeuron();

        addRelation(
                lastBN,
                bn,
                rel,
                5.0,
                5.0
        );

        addPositiveFeedbackLoop(
                bn,
                patternN.getNeuron(),
                0.5,
                patternNetTarget,
                netTarget,
                weakInputMargin,
                true
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
