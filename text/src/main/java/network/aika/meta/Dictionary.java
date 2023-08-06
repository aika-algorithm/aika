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
import network.aika.elements.activations.TokenActivation;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.neurons.TokenNeuron;
import network.aika.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class Dictionary {

    private static final Logger log = LoggerFactory.getLogger(Dictionary.class);


    protected Model model;

    protected NeuronProvider inputTokenCategory;

    protected NeuronProvider inputToken;

    protected double inputPatternNetTarget = 5.0;

    public Dictionary(Model m) {
        this.model = m;
    }

    public double getInputPatternNetTarget() {
        return inputPatternNetTarget;
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
                .setBias(inputPatternNetTarget);

        inputTokenCategory = makeAbstract((PatternNeuron) inputToken.getNeuron())
                .getProvider(true);

        log.info("Input Token: netTarget:" + inputPatternNetTarget);
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

    public void addToken(Document doc, String token, int pos, int begin, int end) {
        TokenNeuron n = lookupInputToken(token);
        doc.addToken(n, pos, begin, end, inputPatternNetTarget);
    }
}
