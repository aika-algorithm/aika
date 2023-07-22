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

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.neurons.*;
import network.aika.elements.synapses.*;
import network.aika.text.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;

import static network.aika.TestUtils.*;
import static network.aika.enums.Scope.INPUT;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Lukas Molzberger
 */
public class MutualExclusionTest {

    private static final Logger log = LoggerFactory.getLogger(MutualExclusionTest.class);

    @Test
    public void testPropagationInput() {
        Model m = new Model();

        TokenNeuron in = new TokenNeuron().init(m, "IN");
        InputInhibitoryNeuron inhib = new InputInhibitoryNeuron().init(m, "I");

        BindingNeuron na = addBindingNeuronInput(m,  "A", 1.0, in, inhib);
        BindingNeuron nb = addBindingNeuronInput(m, "B", 1.5, in, inhib);
        BindingNeuron nc = addBindingNeuronInput(m, "C", 1.2, in, inhib);

        Document doc = new Document(m, "test");

        Config c = getConfig()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        doc.setConfig(c);

        doc.addToken(in, 0, 0, 4);

        doc.postProcessing();
        doc.updateModel();

        log.info("" + doc);

        SortedSet<BindingActivation> nbActs = nb.getActivations(doc);
        Activation nbAct = nbActs.stream().findFirst().orElse(null);

        assertTrue(nbAct.getValue().getValue() > 0.38);

        doc.disconnect();
    }

    @Test
    public void testPropagationSame() {
        Model m = new Model();

        TokenNeuron inA = new TokenNeuron().init(m, "A");
        TokenNeuron inB = new TokenNeuron().init(m, "B");
        TokenNeuron inC = new TokenNeuron().init(m, "C");

        SameInhibitoryNeuron inhib = new SameInhibitoryNeuron().init(m, "I");

        PatternNeuron patternN = new PatternNeuron()
                .init(m, "P");

        patternN.setBias(1.0);

        BindingNeuron na = addBindingNeuronSame(m,  "A", 1.0, inA, inhib, patternN);
        BindingNeuron nb = addBindingNeuronSame(m, "B", 1.5, inB, inhib, patternN);
        BindingNeuron nc = addBindingNeuronSame(m, "C", 1.2, inC, inhib, patternN);

        Document doc = new Document(m, "test");

        Config c = getConfig()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        doc.setConfig(c);

        doc.addToken(inA, 0, 0, 1);
        doc.addToken(inB, 1, 1, 2);
        doc.addToken(inC, 2, 2, 3);

        doc.postProcessing();
        doc.updateModel();

        log.info("" + doc);

        SortedSet<BindingActivation> nbActs = nb.getActivations(doc);
        Activation nbAct = nbActs.stream().findFirst().orElse(null);

        assertTrue(nbAct.getValue().getValue() > 0.38);

        doc.disconnect();
    }

    private static BindingNeuron addBindingNeuronInput(Model m, String label, double bias, TokenNeuron in, InputInhibitoryNeuron inhib) {
        BindingNeuron bn = new BindingNeuron().init(m, label);

        new InputPatternSynapse()
                .setWeight(10.0)
                .init(in, bn)
                .adjustBias();
        new InputNegativeFeedbackSynapse()
                .setWeight(-20.0)
                .init(inhib, bn);

        new InputInhibitorySynapse(INPUT)
                .setWeight(1.0)
                .init(bn, inhib);

        TestUtils.setBias(bn, bias);

        return bn;
    }

    private static BindingNeuron addBindingNeuronSame(Model m, String label, double bias, TokenNeuron in, SameInhibitoryNeuron inhib, PatternNeuron patternN) {
        BindingNeuron bn = new BindingNeuron().init(m, label);

        new InputPatternSynapse()
                .setWeight(10.0)
                .init(in, bn)
                .adjustBias();

        new SameNegativeFeedbackSynapse()
                .setWeight(-20.0)
                .init(inhib, bn);

        new SameInhibitorySynapse(INPUT)
                .setWeight(1.0)
                .init(bn, inhib);

        new PatternSynapse()
                .setWeight(10.0)
                .init(bn, patternN)
                .adjustBias();

        TestUtils.setBias(bn, bias);

        return bn;
    }
}
