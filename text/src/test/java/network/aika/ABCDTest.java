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
import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.elements.neurons.relations.BeforeRelationNeuron;
import network.aika.elements.synapses.InputObjectSynapse;
import network.aika.elements.synapses.RelationInputSynapse;
import network.aika.elements.synapses.SameObjectSynapse;
import network.aika.meta.Dictionary;
import network.aika.text.Document;
import org.junit.jupiter.api.Test;

import static network.aika.TestUtils.initPatternLoop;
import static network.aika.TestUtils.processTokens;


/**
 *
 * @author Lukas Molzberger
 */
public class ABCDTest {

    /**
     *
     */
    @Test
    public void testABCD() throws InterruptedException {
        Model m = new Model();

        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        m.setConfig(c);

        Dictionary dict = new Dictionary(m);
        dict.initStaticNeurons();

        PatternNeuron a_IN = dict.lookupInputToken("a");

        PatternNeuron b_IN = dict.lookupInputToken("b");
        PatternNeuron c_IN = dict.lookupInputToken("c");
        PatternNeuron d_IN = dict.lookupInputToken("d");

        // Pattern ab
        BindingNeuron a_abBN = new BindingNeuron().init(m, "a (ab)");
        BindingNeuron b_abBN = new BindingNeuron().init(m, "b (ab)");

        LatentRelationNeuron relPT = BeforeRelationNeuron.lookupRelation(m, -1, -1);

        new RelationInputSynapse()
                .setWeight(10.0)
                .init(relPT, b_abBN)
                .adjustBias();
        new SameObjectSynapse()
                .setWeight(11.0)
                .init(a_abBN, b_abBN)
                .adjustBias();

        new InputObjectSynapse()
                .setWeight(10.0)
                .init(a_IN, a_abBN)
                .adjustBias();
        a_abBN.setBias(2.5);

        new InputObjectSynapse()
                .setWeight(10.0)
                .init(b_IN, b_abBN)
                .adjustBias();

        PatternNeuron abPattern = initPatternLoop(m, "ab", a_abBN, b_abBN);
        abPattern.setBias(3.0);

        // Pattern bc
        BindingNeuron b_bcBN = new BindingNeuron().init(m, "b (bc)");
        BindingNeuron c_bcBN = new BindingNeuron().init(m, "c (bc)");

        new RelationInputSynapse()
                .setWeight(10.0)
                .init(relPT, c_bcBN)
                .adjustBias();
        new SameObjectSynapse()
                .setWeight(11.0)
                .init(b_bcBN, c_bcBN)
                .adjustBias();

        new InputObjectSynapse()
                .setWeight(10.0)
                .init(b_IN, b_bcBN)
                .adjustBias();
        TestUtils.addOuterInhibitoryLoop(new OuterInhibitoryNeuron().init(m, "I-b"), false, b_abBN, b_bcBN);
        b_abBN.setBias(3.0);
        b_bcBN.setBias(2.5);

        new InputObjectSynapse()
                .setWeight(10.0)
                .init(c_IN, c_bcBN)
                .adjustBias();
        c_bcBN.setBias(3.0);

        PatternNeuron bcPattern = initPatternLoop(m, "bc", b_bcBN, c_bcBN);
        bcPattern.setBias(3.0);

        // Pattern bcd
        BindingNeuron bc_bcdBN = new BindingNeuron().init(m, "bc (bcd)");
        BindingNeuron d_bcdBN = new BindingNeuron().init(m, "d (bcd)");
        new RelationInputSynapse()
                .setWeight(10.0)
                .init(c_bcBN, bc_bcdBN)
                .adjustBias();

        new RelationInputSynapse()
                .setWeight(10.0)
                .init(relPT, d_bcdBN)
                .adjustBias();

        new SameObjectSynapse()
                .setWeight(11.0)
                .init(bc_bcdBN, d_bcdBN)
                .adjustBias();

        new InputObjectSynapse()
                .setWeight(10.0)
                .init(bcPattern, bc_bcdBN)
                .adjustBias();
        bc_bcdBN.setBias(2.5);

        new InputObjectSynapse()
                .setWeight(10.0)
                .init(d_IN, d_bcdBN)
                .adjustBias();
        d_bcdBN.setBias(3.0);

        PatternNeuron bcdPattern = initPatternLoop(m, "bcd", bc_bcdBN, d_bcdBN);
        bcdPattern.setBias(3.0);


        Document doc = new Document(m, "abcd");

        processTokens(dict, doc, "a", "b", "c", "d");

        doc.postProcessing();
        doc.updateModel();

        Thread.sleep(100);
    }
}