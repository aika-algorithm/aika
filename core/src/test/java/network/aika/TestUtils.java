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
package network.aika;

import network.aika.elements.neurons.*;
import network.aika.elements.activations.Activation;
import network.aika.elements.synapses.InputInhibitorySynapse;
import network.aika.elements.synapses.NegativeFeedbackSynapse;
import network.aika.elements.synapses.PatternSynapse;
import network.aika.elements.synapses.PositiveFeedbackSynapse;
import network.aika.text.Document;
import network.aika.elements.activations.TokenActivation;

import java.util.ArrayList;
import java.util.List;

import static network.aika.enums.Scope.INPUT;
import static network.aika.enums.Scope.SAME;
import static network.aika.steps.Phase.INFERENCE;
import static network.aika.steps.keys.QueueKey.MAX_ROUND;


/**
 *
 * @author Lukas Molzberger
 */
public class TestUtils {

    public static void processTokens(Model m, Document doc, Iterable<String> tokens) {
        int i = 0;
        int pos = 0;

        List<TokenActivation> tokenActs = new ArrayList<>();
        for(String t: tokens) {
            int j = i + t.length();

            tokenActs.add(
                    addToken(m, doc, t, pos++, i,  j)
            );

            i = j + 1;
        }

        process(doc, tokenActs);
    }

    public static void process(Document doc, List<TokenActivation> tokenActs) {
        for(TokenActivation tAct: tokenActs) {
            tAct.setNet(10.0);
            doc.process(MAX_ROUND, INFERENCE);
        }

        doc.anneal();

        doc.updateModel();
    }

    public static TokenActivation addToken(Model m, Document doc, String t, Integer pos, int i, int j) {
        return doc.addToken(lookupToken(m, t), pos, i, j);
    }

    public static TokenNeuron lookupToken(Model m, String tokenLabel) {
        return m.lookupNeuronByLabel(tokenLabel, l -> {
            TokenNeuron n = new TokenNeuron();
            n.addProvider(m);

            n.setTokenLabel(l);
            n.setLabel(l);
            n.setAllowTraining(false);
            return n;
        });
    }

    public static Config getConfig() {
        return new Config() {
            public String getLabel(Activation act) {
               // Activation iAct = bs.getOriginActivation();
                Neuron n = act.getNeuron();

                if(n instanceof BindingNeuron) {
                    return "B-"; // + trimPrefix(iAct.getLabel());
                } else if (n instanceof PatternNeuron) {
                    return "P-" + ((Document)act.getThought()).getContent();
                }else if (n instanceof CategoryNeuron) {
                    return "C-" + ((Document)act.getThought()).getContent();
                } else {
                    return "I-"; // + trimPrefix(iAct.getLabel());
                }
            }
        };
    }


    public static InputInhibitoryNeuron addInputInhibitoryLoop(InputInhibitoryNeuron inhibN, boolean sameInhibSynapse, BindingNeuron... bns) {
        if(inhibN == null)
            return null;

        for(BindingNeuron bn: bns) {
            new InputInhibitorySynapse(sameInhibSynapse ? SAME : INPUT)
                    .setWeight(1.0)
                    .init(bn, inhibN);

            new NegativeFeedbackSynapse()
                    .setWeight(-20.0)
                    .init(inhibN, bn);
        }
        return inhibN;
    }

    public static SameInhibitoryNeuron addSameInhibitoryLoop(SameInhibitoryNeuron inhibN, boolean sameInhibSynapse, BindingNeuron... bns) {
        if(inhibN == null)
            return null;

        for(BindingNeuron bn: bns) {
            new InputInhibitorySynapse(sameInhibSynapse ? SAME : INPUT)
                    .setWeight(1.0)
                    .init(bn, inhibN);

            new NegativeFeedbackSynapse()
                    .setWeight(-20.0)
                    .init(inhibN, bn);
        }
        return inhibN;
    }

    public static PatternNeuron initPatternLoop(Model m, String label, BindingNeuron... bns) {
        PatternNeuron patternN = new PatternNeuron()
                .init(m, "P-" + label);

        for(BindingNeuron bn: bns) {
            new PatternSynapse()
                    .setWeight(10.0)
                    .init(bn, patternN)
                    .adjustBias();

            createPositiveFeedbackSynapse(new PositiveFeedbackSynapse(), patternN, bn, 0.0, 10.0);
        }
        return patternN;
    }

    public static void setBias(Neuron n, double bias) {
        n.setBias(bias);
    }

    public static PositiveFeedbackSynapse createPositiveFeedbackSynapse(PositiveFeedbackSynapse s, PatternNeuron input, BindingNeuron output, double weight, double feedbackWeight) {
        s.setInput(input);
        s.setOutput(output);
        s.setWeight(weight);

        s.getPInput().addOutputSynapse(s);
        s.getPOutput().addInputSynapse(s);
        s.getOutput().getBias().receiveUpdate(false, -weight);
        s.getWeight().receiveUpdate(false, feedbackWeight);
        return s;
    }

    public static void setStatistic(PatternNeuron n, double frequency, int N, long lastPosition) {
        n.setFrequency(frequency);
        n.getSampleSpace().setN(N);
        n.getSampleSpace().setLastPosition(lastPosition);
    }
}
