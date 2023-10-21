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
import network.aika.elements.neurons.relations.BeforeRelation;
import network.aika.elements.synapses.InputObjectSynapse;
import network.aika.Document;
import network.aika.text.Range;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.meta.NetworkMotifs.*;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.queue.keys.QueueKey.MAX_ROUND;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Lukas Molzberger
 */
public class InnerInhibitionTest {

    private static final Logger log = LoggerFactory.getLogger(InnerInhibitionTest.class);

    protected double inputPatternNetTarget = 5.0;
    protected double patternNetTarget = 0.7;

    @Test
    public void testInnerInhibition1() {
        Model m = new Model();
        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        m.setConfig(c);

        PatternNeuron inA = new PatternNeuron(m).setLabel("A");
        PatternNeuron inX = new PatternNeuron(m).setLabel("X");

        InnerInhibitoryNeuron inhib = new InnerInhibitoryNeuron(m).setLabel("I");

        PatternNeuron patternN = new PatternNeuron(m)
                .setLabel("P");

        patternN.setBias(patternNetTarget);

        BindingNeuron na = addBindingNeuronInner(m,  "A", 2.5, inA, inhib, patternN);
        BindingNeuron nx = addBindingNeuronInner(m,  "X", 2.5, inX, null, patternN);

        LatentRelationNeuron relPT = new LatentRelationNeuron(
                m,
                new BeforeRelation(
                        INPUT,
                        new Range(-300, 0)
                )
        )
                .setLabel("relPT");

        addRelation(na, nx, relPT, 5.0, 10.0, false);

        Document doc = new Document(m, "test");
        AIKADebugger.createAndShowGUI()
                .setDocument(doc)
                .setShowNeurons(false, false);

        doc.setFeedbackTriggerRound();

        doc.addToken(inA, new TextReference(0, 0, 1), inputPatternNetTarget - 2);
        doc.addToken(inA, new TextReference(1, 1, 2), inputPatternNetTarget);
        doc.addToken(inA, new TextReference(2, 2, 3), inputPatternNetTarget - 1);
        doc.addToken(inX, new TextReference(3, 3, 4), inputPatternNetTarget);

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
        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        m.setConfig(c);

        PatternNeuron inA = new PatternNeuron(m).setLabel("A");
        inA.setTargetNet(inputPatternNetTarget);
        PatternNeuron inB = new PatternNeuron(m).setLabel("B");
        inB.setTargetNet(inputPatternNetTarget);
        PatternNeuron inC = new PatternNeuron(m).setLabel("C");
        inC.setTargetNet(inputPatternNetTarget);
        PatternNeuron inX = new PatternNeuron(m).setLabel("X");
        inX.setTargetNet(inputPatternNetTarget);

        InnerInhibitoryNeuron inhib = new InnerInhibitoryNeuron(m).setLabel("I");

        PatternNeuron patternN = new PatternNeuron(m)
                .setLabel("P");

        patternN.setBias(patternNetTarget);

        BindingNeuron na = addBindingNeuronInner(m,  "A", 2.5, inA, inhib, patternN);
        BindingNeuron nb = addBindingNeuronInner(m, "B", 3.0, inB, inhib, patternN);
        BindingNeuron nc = addBindingNeuronInner(m, "C", 2.7, inC, inhib, patternN);
        BindingNeuron nx = addBindingNeuronInner(m,  "X", 2.5, inX, null, patternN);

        LatentRelationNeuron relPT = new LatentRelationNeuron(
                m,
                new BeforeRelation(
                INPUT,
                new Range(-300, 0)
        )
        )
                .setLabel("relPT");

        addRelation(na, nx, relPT, 5.0, 10.0, false);
        addRelation(nb, nx, relPT, 5.0, 10.0, false);
        addRelation(nc, nx, relPT, 5.0, 10.0, false);

        Document doc = new Document(m, "test");
        AIKADebugger.createAndShowGUI()
                .setDocument(doc)
                .setShowNeurons(false, false);

        doc.setFeedbackTriggerRound();

        doc.addToken(inA, new TextReference(0, 0, 1), inputPatternNetTarget);
        doc.addToken(inB, new TextReference(1, 1, 2), inputPatternNetTarget);
        doc.addToken(inC, new TextReference(2, 2, 3), inputPatternNetTarget);
        doc.addToken(inX, new TextReference(3, 3, 4), inputPatternNetTarget);

        doc.process(MAX_ROUND, INFERENCE);

        doc.postProcessing();
        doc.updateModel();

        log.info("" + doc);

        SortedSet<BindingActivation> nbActs = nb.getActivations(doc);
        Activation nbAct = nbActs.stream().findFirst().orElse(null);

        assertTrue(nbAct.getValue().getValue() > 0.38);

        doc.disconnect();
    }

    private BindingNeuron addBindingNeuronInner(Model m, String label, double bindingNetTarget, PatternNeuron in, InnerInhibitoryNeuron inhib, PatternNeuron patternN) {
        BindingNeuron bn = new BindingNeuron(m).setLabel(label);

        new InputObjectSynapse()
                .setWeight(10.0)
                .link(in, bn)
                .adjustBias();

        double patternValueTarget = patternN.getActivationFunction().f(patternNetTarget);

        if(inhib != null)
            addInnerInhibitoryLoop(bn, inhib, getMaxBindingNetTarget(bindingNetTarget, patternValueTarget));

        addPositiveFeedbackLoop(
                bn,
                patternN,
                2.5,
                0.0,
                false,
                false
        );

        bn.setBias(bindingNetTarget);

        return bn;
    }
}
