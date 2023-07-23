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

import network.aika.elements.activations.TokenActivation;
import network.aika.elements.neurons.*;
import network.aika.text.Document;

import java.util.ArrayList;
import java.util.List;

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
}
