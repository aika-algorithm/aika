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
import network.aika.elements.synapses.*;
import network.aika.meta.Dictionary;
import network.aika.text.Document;
import network.aika.tokenizer.SimpleWordTokenizer;
import org.junit.jupiter.api.Test;

import static network.aika.TestUtils.*;

/**
 *
 * @author Lukas Molzberger
 */
public class JacksonCookTest {

    protected static double PASSIVE_SYNAPSE_WEIGHT = 0.0;

    private SimpleWordTokenizer tokenizer;

    @Test
    public void testJacksonCook()  {
        setupJacksonCookTest();
    }

    public void setupJacksonCookTest() {
        Model m = new Model();
        Dictionary dict = new Dictionary(m);
        dict.initStaticNeurons();

        SimpleWordTokenizer tokenizer = new SimpleWordTokenizer(dict);

        TokenNeuron jacksonIN = dict.lookupInputToken("Jackson");
        TokenNeuron cookIN = dict.lookupInputToken("Cook");

        LatentRelationNeuron relPT = BeforeRelationNeuron.lookupRelation(m, -1, -1);

        BindingNeuron forenameBN = new BindingNeuron()
                .init(m, "forename (person name)");
        BindingNeuron surnameBN = new BindingNeuron()
                .init(m, "surname (person name)");


        BindingCategoryNeuron forenameBNCat = new BindingCategoryNeuron()
                .init(m, "Forename Category");

        new BindingCategoryInputSynapse()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .init(forenameBNCat, forenameBN);

        BindingCategoryNeuron surnameBNCat = new BindingCategoryNeuron()
                .init(m, "Forename Category");

        new BindingCategoryInputSynapse()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .init(surnameBNCat, surnameBN);


        BindingNeuron jacksonForenameBN = forenameBN.instantiateTemplate()
                .init(m, "jackson (forename)");

        BindingNeuron jacksonJCBN = jacksonForenameBN.instantiateTemplate()
                .init(m, "jackson (jackson cook)");

        new InputPatternSynapse()
                .setWeight(10.0)
                .init(jacksonIN, jacksonJCBN)
                .adjustBias();

        CategoryNeuron jacksonForenameCN = new BindingCategoryNeuron()
                .init(m, "jackson (forename)");
        new BindingCategorySynapse()
                .setWeight(10.0)
                .init(jacksonJCBN, jacksonForenameCN);

        new BindingCategoryInputSynapse()
                .setWeight(10.0)
                .init(jacksonForenameCN, jacksonForenameBN);

        new InputPatternSynapse()
                .setWeight(10.0)
                .init(jacksonIN, jacksonForenameBN)
                .adjustBias();

        CategoryNeuron forenameCN = new BindingCategoryNeuron()
                .init(m, "forename");

        new BindingCategorySynapse()
                .setWeight(10.0)
                .init(jacksonForenameBN, forenameCN);

        BindingNeuron jacksonCityBN = new BindingNeuron()
                .init(m, "jackson (city)");
        new InputPatternSynapse()
                .setWeight(10.0)
                .init(jacksonIN, jacksonCityBN)
                .adjustBias();

        CategoryNeuron cityCN = new BindingCategoryNeuron()
                .init(m, "city");

        new BindingCategorySynapse()
                .setWeight(10.0)
                .init(jacksonCityBN, cityCN);

        BindingNeuron cookSurnameBN =  surnameBN.init(m, "cook (surname)");
        BindingNeuron cookJCBN =  cookSurnameBN.init(m, "cook (jackson cook)");
        new InputPatternSynapse()
                .setWeight(10.0)
                .init(cookIN, cookJCBN)
                .adjustBias();

        CategoryNeuron cookSurnameCN = new BindingCategoryNeuron()
                .init(m, "cook (surname)");
        new BindingCategorySynapse()
                .setWeight(10.0)
                .init(cookJCBN, cookSurnameCN);

        new BindingCategoryInputSynapse()
                .setWeight(10.0)
                .init(cookSurnameCN, cookSurnameBN);

        new InputPatternSynapse()
                .setWeight(10.0)
                .init(cookIN, cookSurnameBN)
                .adjustBias();

        CategoryNeuron surnameCN =  new BindingCategoryNeuron()
                .init(m, "surname");
        new BindingCategorySynapse()
                .setWeight(10.0)
                .init(cookSurnameBN, surnameCN);

        BindingNeuron cookProfessionBN =  new BindingNeuron()
                .init(m, "cook (profession)");
        new InputPatternSynapse()
                .setWeight(10.0)
                .init(cookIN, cookProfessionBN)
                .adjustBias();

        CategoryNeuron professionCN = new BindingCategoryNeuron()
                .init(m, "profession");
        new BindingCategorySynapse()
                .setWeight(10.0)
                .init(cookProfessionBN, professionCN);

        addOuterInhibitoryLoop(
                new OuterInhibitoryNeuron().init(m, "I-jackson"),
                false,
                jacksonForenameBN,
                jacksonCityBN
        );
        addOuterInhibitoryLoop(
                new OuterInhibitoryNeuron().init(m, "I-cook"),
                false,
                cookSurnameBN,
                cookProfessionBN
        );

        jacksonJCBN.setBias(2.0);
        jacksonForenameBN.setBias(2.0);
        jacksonCityBN.setBias(3.0);
        cookJCBN.setBias(2.0);
        cookSurnameBN.setBias(2.0);
        cookProfessionBN.setBias(3.0);

        new BindingCategoryInputSynapse()
                .setWeight(10.0)
                .init(forenameCN, forenameBN);

        new BindingCategoryInputSynapse()
                .setWeight(10.0)
                .init(surnameCN, surnameBN);

        new RelationInputSynapse()
                .setWeight(5.0)
                .init(relPT, surnameBN)
                .adjustBias();

        new SamePatternSynapse()
                .setWeight(10.0)
                .init(forenameBN, surnameBN)
                .adjustBias();

        forenameBN.setBias(2.0);
        surnameBN.setBias(2.0);

        PatternNeuron jacksonCookPattern = initPatternLoop(
                m,
                "jackson cook",
                jacksonJCBN,
                cookJCBN
        );
        jacksonCookPattern.setBias(3.0);

        PatternNeuron personNamePattern = initPatternLoop(
                m,
                "person name",
                forenameBN,
                surnameBN
        );
        personNamePattern.setBias(3.0);

        Document doc = new Document(m, "Jackson Cook");

        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        doc.setConfig(c);

        processTokens(dict, doc, "Jackson", "Cook");

        doc.postProcessing();
        doc.updateModel();
        doc.disconnect();
    }
}
