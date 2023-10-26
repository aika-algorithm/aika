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

import network.aika.elements.neurons.InhibitoryNeuron;
import network.aika.meta.Dictionary;
import org.junit.jupiter.api.Test;

import static network.aika.TestHelper.initPatternTheCat;
import static network.aika.TestHelper.initPatternTheDog;
import static network.aika.TestUtils.processTokens;

/**
 *
 * @author Lukas Molzberger
 */
public class TheDogAndCatTest {

    @Test
    public void testTheDogAndTheCat()  {
        Model m = new Model();
        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(false);
        m.setConfig(c);

        Dictionary dict = new Dictionary(m);
        dict.initStaticNeurons();

        InhibitoryNeuron inhibNThe = new InhibitoryNeuron(m)
                .setLabel("I-the");

        InhibitoryNeuron inhibNCat = new InhibitoryNeuron(m)
                .setLabel("I-cat");

        InhibitoryNeuron inhibNDog = new InhibitoryNeuron(m)
                .setLabel("I-dog");

        initPatternTheCat(m, dict, inhibNThe, inhibNCat, 3);
        initPatternTheDog(m, dict, inhibNThe, inhibNDog, 3);

        Document doc = new Document(m, "the dog and the cat");

        processTokens(dict, doc, "the", "dog", "and", "the", "cat");

        doc.postProcessing();
        doc.updateModel();

        doc.disconnect();
    }
}
