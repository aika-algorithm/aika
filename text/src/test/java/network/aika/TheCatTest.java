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
import network.aika.elements.neurons.OuterInhibitoryNeuron;
import network.aika.meta.Dictionary;
import network.aika.text.Document;
import org.junit.jupiter.api.Test;

import static network.aika.TestHelper.initPatternTheCat;
import static network.aika.TestUtils.processTokens;


/**
 *
 * @author Lukas Molzberger
 */
public class TheCatTest {

    @Test
    public void testTheBlackCat()  {
        for(int variant = 0; variant < 4; variant++) {
            performTest(variant);
        }
    }

    private void performTest(int variant) {
        Model m = new Model();
        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        m.setConfig(c);

        Dictionary dict = new Dictionary(m);
        dict.initStaticNeurons();

        OuterInhibitoryNeuron inhibNThe = null; //new InhibitoryNeuron().init(m, "I-the");
        OuterInhibitoryNeuron inhibNCat = null; //new InhibitoryNeuron().init(m, "I-cat");
        initPatternTheCat(m, dict, inhibNThe, inhibNCat, variant);

        Document doc = new Document(m, "the cat");

        AIKADebugger.createAndShowGUI(doc);

        processTokens(dict, doc, "the", "cat");

        doc.postProcessing();
        doc.updateModel();

        doc.disconnect();
    }
}
