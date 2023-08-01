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

import network.aika.suspension.FSSuspensionCallback;
import network.aika.text.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static network.aika.TestUtils.addToken;

/**
 *
 * @author Lukas Molzberger
 */
public class TestFsModel {

    @Test
    public void testOpenModel() throws IOException {
        Model m = new Model(
                new FSSuspensionCallback(new File("/Users/lukas.molzberger/models").toPath(), "AIKA-2.0-10", true)
        );

        m.open(false);

        {
            Document doc = generateDocument(m, "arbeit fair arbeitsvermittlung ", true);

            doc.postProcessing();
            doc.updateModel();

            doc.disconnect();
        }


        {
            Document doc = generateDocument(m, "arbeit fair arbeitsvermittlung ", false);

            doc.postProcessing();
            doc.updateModel();

            doc.disconnect();
        }

        m.close();
    }

    private Document generateDocument(Model m, String txt, boolean train) {
        Document doc = new Document(m, txt);

        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(train);
        doc.setConfig(c);

        int i = 0;
        int pos = 0;
        for(String t: doc.getContent().split(" ")) {
            int j = i + t.length();
            addToken(m, doc, t, pos++, i, j);
            i = j + 1;
        }
        return doc;
    }
}
