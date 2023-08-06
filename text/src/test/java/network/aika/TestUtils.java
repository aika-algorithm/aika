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
import network.aika.meta.NetworkMotivs;
import network.aika.text.Document;

/**
 *
 * @author Lukas Molzberger
 */
public class TestUtils {

    public static void processTokens(Model m, Document doc, String... tokens) {


    }

    public static OuterInhibitoryNeuron addOuterInhibitoryLoop(OuterInhibitoryNeuron inhibN, boolean sameInhibSynapse, BindingNeuron... bns) {
        if(inhibN == null)
            return null;

        for(BindingNeuron bn: bns) {
            NetworkMotivs.addOuterInhibitoryLoop(bn, inhibN, -20.0);
        }
        return inhibN;
    }

    public static InnerInhibitoryNeuron addInnerInhibitoryLoop(InnerInhibitoryNeuron inhibN, boolean sameInhibSynapse, BindingNeuron... bns) {
        if(inhibN == null)
            return null;

        for(BindingNeuron bn: bns) {
            NetworkMotivs.addInnerInhibitoryLoop(bn, inhibN, -20.0);
        }
        return inhibN;
    }

    public static PatternNeuron initPatternLoop(Model m, String label, BindingNeuron... bns) {
        PatternNeuron patternN = new PatternNeuron()
                .init(m, "P-" + label);

        for(BindingNeuron bn: bns) {
            NetworkMotivs.addPositiveFeedbackLoop(bn, patternN, 10.0, 1.0, 2.0, 0.1, false);
        }
        return patternN;
    }
}
