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
package network.aika.meta;

import network.aika.Model;
import network.aika.enums.Scope;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.TokenActivation;
import network.aika.elements.neurons.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static network.aika.meta.NetworkMotivs.*;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class AbstractTemplateModel {

    private static final Logger log = LoggerFactory.getLogger(AbstractTemplateModel.class);

    protected Model model;

    protected NeuronProvider inputTokenCategory;
    protected NeuronProvider inputToken;
    protected NeuronProvider relPT;
    protected NeuronProvider relNT;

    protected NeuronProvider inhibitoryN;

    protected NeuronProvider inhibCat;

    protected NeuronProvider patternN;

    protected NeuronProvider primaryBN;

    protected double inputPatternNetTarget = 5.0;
    protected double patternNetTarget = 0.7;

    protected static double POS_MARGIN = 1.0;
    protected static double NEG_MARGIN_LEFT = 1.2;
    protected static double NEG_MARGIN_RIGHT = 1.1;

    public AbstractTemplateModel(Model m) {
        model = m;
    }

    public NeuronProvider getInputTokenCategory() {
        return inputTokenCategory;
    }

    public NeuronProvider getRelationPreviousToken() {
        return relPT;
    }

    public NeuronProvider getRelationNextToken() {
        return relNT;
    }

    public NeuronProvider getInhibitoryNeuron() {
        return inhibitoryN;
    }

    public NeuronProvider getInhibitoryCategory() {
        return inhibCat;
    }

    public NeuronProvider getPatternNeuron() {
        return patternN;
    }

    public NeuronProvider getInputToken() {
        return inputToken;
    }

    public boolean evaluatePrimaryBindingActs(Activation act) {
        return false;
    }

    public double getInputPatternNetTarget() {
        return inputPatternNetTarget;
    }

    public void setTokenInputNet(List<TokenActivation> tokenActs) {
        for(TokenActivation tAct: tokenActs) {
            tAct.setNet(inputPatternNetTarget);
        }
    }

    public abstract String getPatternType();

    public void initTemplates() {
        // Abstract
        patternN = new PatternNeuron()
                .init(model, getPatternType())
                .getProvider(true);

        makeAbstract((PatternNeuron) patternN.getNeuron());


        inhibitoryN = new SameInhibitoryNeuron()
                .init(model, "I")
                .getProvider(true);

        makeAbstract((InputInhibitoryNeuron) inhibitoryN.getNeuron());

        log.info(getPatternType() + " Pattern: netTarget:" + patternNetTarget);

        patternN.getNeuron().setBias(patternNetTarget);

        initTemplateBindingNeurons();
    }

    public void initStaticNeurons() {
        relPT = TokenPositionRelationNeuron.lookupRelation(model, -1, -1)
                .getProvider(true);

        relNT = TokenPositionRelationNeuron.lookupRelation(model, 1, 1)
                .getProvider(true);

        inputToken = model.lookupNeuronByLabel("Abstract Input Token", l ->
                new TokenNeuron()
                        .init(model, l)
        ).getProvider(true);

        inputToken.getNeuron()
                .setBias(inputPatternNetTarget);

        inputTokenCategory = makeAbstract((PatternNeuron) inputToken.getNeuron())
                .getProvider(true);

        log.info("Input Token: netTarget:" + inputPatternNetTarget);
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
                        dir * lastPos,
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
            Integer lastPos,
            BindingNeuron lastBN
    ) {
        double netTarget = 2.5;

        log.info("Strong Binding-Neuron: netTarget:" + netTarget);

        BindingNeuron bn = addBindingNeuron(
                inputToken.getNeuron(),
                "Abstract (S) Pos:" + pos,
                10.0,
                inputPatternNetTarget,
                netTarget
        );
        makeAbstract(bn);

        addNegativeFeedbackLoop(
                bn,
                inhibitoryN.getNeuron(),
                getNegMargin(pos) * -netTarget
        );

        if(lastPos == null || lastBN == null) {
            bn.setCallActivationCheckCallback(true);
        } else {
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
                inputToken.getNeuron(),
                "Abstract (W) Pos:" + pos,
                10.0,
                inputPatternNetTarget,
                netTarget
        );

        makeAbstract(bn);

        addNegativeFeedbackLoop(
                bn,
                inhibitoryN.getNeuron(),
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

    public TokenNeuron lookupInputToken(String label) {
        return model.lookupNeuronByLabel(label, l -> {
                    TokenNeuron inputTokenN = inputToken.getNeuron();
                    TokenNeuron n = inputTokenN.instantiateTemplate()
                            .init(model, label);

                    n.setTokenLabel(label);
                    n.setAllowTraining(false);

                    return n;
                }
        );
    }

    private double getNegMargin(int pos) {
        return pos >= 0 ?
                NEG_MARGIN_RIGHT :
                NEG_MARGIN_LEFT;
    }

    public Model getModel() {
        return model;
    }
}
