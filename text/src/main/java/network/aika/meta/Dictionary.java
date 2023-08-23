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
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.neurons.TokenNeuron;
import network.aika.elements.synapses.PatternCategorySynapse;
import network.aika.enums.sign.Sign;
import network.aika.text.Document;
import network.aika.text.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class Dictionary {

    private static final Logger log = LoggerFactory.getLogger(Dictionary.class);

    public static final double INPUT_TOKEN_NET_TARGET = 5.0;

    protected Model model;

    protected NeuronProvider inputTokenCategory;

    protected NeuronProvider inputToken;


    public Dictionary(Model m) {
        this.model = m;
    }

    public NeuronProvider getInputTokenCategory() {
        return inputTokenCategory;
    }

    public NeuronProvider getInputToken() {
        return inputToken;
    }

    public void initStaticNeurons() {
        inputToken = model.lookupNeuronByLabel("Abstract Input Token", l ->
                new TokenNeuron()
                        .init(model, l)
        ).getProvider(true);

        inputToken.getNeuron()
                .setBias(INPUT_TOKEN_NET_TARGET);

        inputTokenCategory = makeAbstract((PatternNeuron) inputToken.getNeuron())
                .getProvider(true);

        log.info("Input Token: netTarget:" + INPUT_TOKEN_NET_TARGET);
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
        if(!(s.getInput() instanceof TokenNeuron))
            return;

        TokenNeuron tn = (TokenNeuron) s.getInput();
        if(tn.getSampleSpace().getN() == 0)
            return;

        Range r = new Range(0, tn.getTokenLabel().length());

        double surprisal = s.getInput().getSurprisal(Sign.POS, r, false);

        double weight = 1.0 + (-0.1 * surprisal);
        s.setWeight(weight);

        log.debug("Set category synapse weight for token: " + s.getInput().getLabel() + " (weight: " + weight + " surprisal: " + surprisal + ")");
    }

    public TokenNeuron lookupInputToken(String label) {
        return model.lookupNeuronByLabel(label, l ->
                createTokenNeuron(label)
        );
    }

    protected TokenNeuron createTokenNeuron(String label) {
        TokenNeuron inputTokenN = inputToken.getNeuron();
        TokenNeuron n = inputTokenN.instantiateTemplate()
                .init(model, label);

        n.setTokenLabel(label);
        n.setAllowTraining(false);

        return n;
    }

    public void addToken(Document doc, String token, int pos, int begin, int end) {
        TokenNeuron n = lookupInputToken(token);
        doc.addToken(n, pos, begin, end, INPUT_TOKEN_NET_TARGET);
    }
}
