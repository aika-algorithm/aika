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

import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.neurons.*;
import network.aika.elements.synapses.InnerInhibitorySynapse;
import network.aika.elements.synapses.InnerNegativeFeedbackSynapse;
import network.aika.elements.synapses.InputPatternSynapse;
import network.aika.elements.synapses.PatternSynapse;
import network.aika.meta.NetworkMotivs;
import network.aika.text.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;

import static network.aika.TestUtils.getConfig;
import static network.aika.meta.NetworkMotivs.*;
import static network.aika.steps.Phase.INFERENCE;
import static network.aika.steps.keys.QueueKey.MAX_ROUND;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Lukas Molzberger
 */
public class InnerInhibitionTest {

    private static final Logger log = LoggerFactory.getLogger(InnerInhibitionTest.class);

    @Test
    public void testInnerInhibition1() {
        Model m = new Model();

        TokenNeuron inA = new TokenNeuron().init(m, "A");
        TokenNeuron inX = new TokenNeuron().init(m, "X");

        InnerInhibitoryNeuron inhib = new InnerInhibitoryNeuron().init(m, "I");

        PatternNeuron patternN = new PatternNeuron()
                .init(m, "P");

        patternN.setBias(1.0);

        BindingNeuron na = addBindingNeuronInner(m,  "A", 1.0, inA, inhib, patternN);
        BindingNeuron nx = addBindingNeuronInner(m,  "X", 1.0, inX, null, patternN);

        TokenPositionRelationNeuron relPT = TokenPositionRelationNeuron.lookupRelation(m, -300, -1);

        addRelation(na, nx, relPT, 5.0, 10.0);

        Document doc = new Document(m, "test");
        AIKADebugger.createAndShowGUI(doc, false);

        Config c = getConfig()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        doc.setConfig(c);

        doc.setFeedbackTriggerRound();

        doc.addToken(inA, 0, 0, 1)
                .setNet(10.0);
        doc.addToken(inA, 1, 1, 2)
                .setNet(10.0);
        doc.addToken(inA, 2, 2, 3)
                .setNet(10.0);
        doc.addToken(inX, 3, 3, 4)
                .setNet(10.0);

        doc.process(MAX_ROUND, INFERENCE);

        doc.postProcessing();
        doc.updateModel();

        log.info("" + doc);

        SortedSet<BindingActivation> nbActs = na.getActivations(doc);
        Activation nbAct = nbActs.stream().findFirst().orElse(null);

        assertTrue(nbAct.getValue().getValue() > 0.38);

        doc.disconnect();
    }

    @Test
    public void testInnerInhibition2() {
        Model m = new Model();

        TokenNeuron inA = new TokenNeuron().init(m, "A");
        TokenNeuron inB = new TokenNeuron().init(m, "B");
        TokenNeuron inC = new TokenNeuron().init(m, "C");
        TokenNeuron inX = new TokenNeuron().init(m, "X");

        InnerInhibitoryNeuron inhib = new InnerInhibitoryNeuron().init(m, "I");

        PatternNeuron patternN = new PatternNeuron()
                .init(m, "P");

        patternN.setBias(1.0);

        BindingNeuron na = addBindingNeuronInner(m,  "A", 1.0, inA, inhib, patternN);
        BindingNeuron nb = addBindingNeuronInner(m, "B", 1.5, inB, inhib, patternN);
        BindingNeuron nc = addBindingNeuronInner(m, "C", 1.2, inC, inhib, patternN);
        BindingNeuron nx = addBindingNeuronInner(m,  "X", 1.0, inX, null, patternN);

        TokenPositionRelationNeuron relPT = TokenPositionRelationNeuron.lookupRelation(m, -300, -1);

        addRelation(na, nx, relPT, 5.0, 10.0);
        addRelation(nb, nx, relPT, 5.0, 10.0);
        addRelation(nc, nx, relPT, 5.0, 10.0);

        Document doc = new Document(m, "test");
        AIKADebugger.createAndShowGUI(doc, false);

        Config c = getConfig()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        doc.setConfig(c);

        doc.setFeedbackTriggerRound();

        doc.addToken(inA, 0, 0, 1)
                .setNet(10.0);
        doc.addToken(inB, 1, 1, 2)
                .setNet(10.0);
        doc.addToken(inC, 2, 2, 3)
                .setNet(10.0);
        doc.addToken(inX, 3, 3, 4)
                .setNet(10.0);

        doc.process(MAX_ROUND, INFERENCE);

        doc.postProcessing();
        doc.updateModel();

        log.info("" + doc);

        SortedSet<BindingActivation> nbActs = nb.getActivations(doc);
        Activation nbAct = nbActs.stream().findFirst().orElse(null);

        assertTrue(nbAct.getValue().getValue() > 0.38);

        doc.disconnect();
    }

    private static BindingNeuron addBindingNeuronInner(Model m, String label, double bias, TokenNeuron in, InnerInhibitoryNeuron inhib, PatternNeuron patternN) {
        BindingNeuron bn = new BindingNeuron().init(m, label);

        new InputPatternSynapse()
                .setWeight(10.0)
                .init(in, bn)
                .adjustBias();

        if(inhib != null)
            addInnerNegativeFeedbackLoop(bn, inhib, -5.0);

        addPositiveFeedbackLoop(
                bn,
                patternN,
                3.0,
                1.0,
                2.0,
                0.0,
                false
        );

        TestUtils.setBias(bn, bias);

        return bn;
    }
}
