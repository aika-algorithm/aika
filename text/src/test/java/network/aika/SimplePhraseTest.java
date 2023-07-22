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

import network.aika.text.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static network.aika.TestUtils.addToken;
import static network.aika.TestUtils.getConfig;

/**
 *
 * @author Lukas Molzberger
 */
public class SimplePhraseTest {

    private static final Logger log = LoggerFactory.getLogger(SimplePhraseTest.class);


    public String[] phrases = new String[]{
            "der Hund",
            "die Katze",
            "der Vogel",
            "das Pferd",
            "die Maus",
            "der Elefant",
            "der Löwe",
            "das Pony",
            "die Spinne",
            "der Jaguar"
    };

    @Test
    public void simplePhraseTest() {
        Model model = new Model();

        Config c = getConfig()
                        .setAlpha(0.99)
                        .setLearnRate(0.1)
                        .setTrainingEnabled(false);

        Random r = new Random(1);

        for (int k = 0; k < 1000; k++) {
            String phrase = phrases[r.nextInt(phrases.length)];
            System.out.println("  " + phrase);

            Document doc = new Document(model, phrase);
            doc.setConfig(c);
            c.setTrainingEnabled(k > 100);

            int i = 0;
            int pos = 0;
            for(String t: doc.getContent().split(" ")) {
                int j = i + t.length();
                addToken(model, doc, t, pos++, i, j);

                i = j + 1;
            }

            doc.postProcessing();
            doc.updateModel();

            log.info("" + doc);

            doc.disconnect();
        }
    }
}
