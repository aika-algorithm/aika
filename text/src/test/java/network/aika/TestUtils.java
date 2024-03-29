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

import network.aika.elements.neurons.RefType;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.meta.Dictionary;
import network.aika.meta.NetworkMotifs;
import network.aika.text.TextReference;
import network.aika.text.Range;

import static network.aika.elements.neurons.RefType.NEURON_EXTERNAL;
import static network.aika.elements.neurons.RefType.TEMPLATE_MODEL;

/**
 *
 * @author Lukas Molzberger
 */
public class TestUtils {

    public static void processTokens(Dictionary dict, Document doc, String... tokens) {
        for(int i = 0; i < tokens.length; i++) {
            PatternNeuron n = dict.lookupInputToken(tokens[i]);
            doc.addToken(
                    n,
                    new TextReference(
                            new Range(i, i + 1),
                            new Range(0, 1)
                    )
            );
        }
    }

    public static InhibitoryNeuron addOuterInhibitoryLoop(InhibitoryNeuron inhibN, boolean sameInhibSynapse, BindingNeuron... bns) {
        if(inhibN == null)
            return null;

        for(BindingNeuron bn: bns) {
            NetworkMotifs.addInhibitoryLoop(bn, inhibN, -20.0);
        }
        return inhibN;
    }

    public static PatternNeuron initPatternLoop(Model m, String label, BindingNeuron... bns) {
        PatternNeuron patternN = new PatternNeuron(m, NEURON_EXTERNAL)
                .setLabel("P-" + label);

        for(BindingNeuron bn: bns) {
            NetworkMotifs.addPositiveFeedbackLoop(
                    bn,
                    patternN,
                    10.0,
                    0.1,
                    false,
                    false
            );
        }
        return patternN;
    }
}
