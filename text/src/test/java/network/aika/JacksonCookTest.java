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
import network.aika.elements.neurons.types.*;
import network.aika.elements.relations.BeforeRelation;
import network.aika.elements.synapses.types.*;
import network.aika.meta.Dictionary;
import network.aika.text.Range;
import network.aika.tokenizer.WordTokenizer;
import org.junit.jupiter.api.Test;

import static network.aika.TestUtils.*;
import static network.aika.elements.neurons.RefType.NEURON_EXTERNAL;
import static network.aika.enums.direction.Direction.INPUT;

/**
 *
 * @author Lukas Molzberger
 */
public class JacksonCookTest {

    protected static double PASSIVE_SYNAPSE_WEIGHT = 0.0;

    private WordTokenizer tokenizer;

    @Test
    public void testJacksonCook()  {
        setupJacksonCookTest();
    }

    public void setupJacksonCookTest() {
        Model m = new Model();
        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        m.setConfig(c);

        Dictionary dict = new Dictionary(m);
        dict.initStaticNeurons();

        WordTokenizer tokenizer = new WordTokenizer();

        PatternNeuron jacksonIN = dict.lookupInputToken("Jackson");
        PatternNeuron cookIN = dict.lookupInputToken("Cook");

        LatentRelationNeuron relPT = new LatentRelationNeuron(m,
                new BeforeRelation(
                        INPUT,
                        new Range(0, 0)
                ),
                NEURON_EXTERNAL
        )
                .setLabel("relPT");

        BindingNeuron forenameBN = new BindingNeuron(m, NEURON_EXTERNAL)
                .setLabel("forename (person name)");
        BindingNeuron surnameBN = new BindingNeuron(m, NEURON_EXTERNAL)
                .setLabel("surname (person name)");


        BindingCategoryNeuron forenameBNCat = new BindingCategoryNeuron(m, NEURON_EXTERNAL)
                .setLabel("Forename Category");

        new BindingCategoryInputSynapse()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .link(forenameBNCat, forenameBN);

        BindingCategoryNeuron surnameBNCat = new BindingCategoryNeuron(m, NEURON_EXTERNAL)
                .setLabel("Forename Category");

        new BindingCategoryInputSynapse()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .link(surnameBNCat, surnameBN);


        BindingNeuron jacksonForenameBN = forenameBN.instantiateTemplate()
                .setLabel("jackson (forename)");

        BindingNeuron jacksonJCBN = jacksonForenameBN.instantiateTemplate()
                .setLabel("jackson (jackson cook)");

        new InputObjectSynapse()
                .setWeight(10.0)
                .link(jacksonIN, jacksonJCBN)
                .adjustBias();

        CategoryNeuron jacksonForenameCN = new BindingCategoryNeuron(m, NEURON_EXTERNAL)
                .setLabel("jackson (forename)");
        new BindingCategorySynapse()
                .setWeight(10.0)
                .link(jacksonJCBN, jacksonForenameCN);

        new BindingCategoryInputSynapse()
                .setWeight(10.0)
                .link(jacksonForenameCN, jacksonForenameBN);

        new InputObjectSynapse()
                .setWeight(10.0)
                .link(jacksonIN, jacksonForenameBN)
                .adjustBias();

        CategoryNeuron forenameCN = new BindingCategoryNeuron(m, NEURON_EXTERNAL)
                .setLabel("forename");

        new BindingCategorySynapse()
                .setWeight(10.0)
                .link(jacksonForenameBN, forenameCN);

        BindingNeuron jacksonCityBN = new BindingNeuron(m, NEURON_EXTERNAL)
                .setLabel("jackson (city)");
        new InputObjectSynapse()
                .setWeight(10.0)
                .link(jacksonIN, jacksonCityBN)
                .adjustBias();

        CategoryNeuron cityCN = new BindingCategoryNeuron(m, NEURON_EXTERNAL)
                .setLabel("city");

        new BindingCategorySynapse()
                .setWeight(10.0)
                .link(jacksonCityBN, cityCN);

        BindingNeuron cookSurnameBN =  surnameBN.setLabel("cook (surname)");
        BindingNeuron cookJCBN =  cookSurnameBN.setLabel("cook (jackson cook)");
        new InputObjectSynapse()
                .setWeight(10.0)
                .link(cookIN, cookJCBN)
                .adjustBias();

        CategoryNeuron cookSurnameCN = new BindingCategoryNeuron(m, NEURON_EXTERNAL)
                .setLabel("cook (surname)");
        new BindingCategorySynapse()
                .setWeight(10.0)
                .link(cookJCBN, cookSurnameCN);

        new BindingCategoryInputSynapse()
                .setWeight(10.0)
                .link(cookSurnameCN, cookSurnameBN);

        new InputObjectSynapse()
                .setWeight(10.0)
                .link(cookIN, cookSurnameBN)
                .adjustBias();

        CategoryNeuron surnameCN =  new BindingCategoryNeuron(m, NEURON_EXTERNAL)
                .setLabel("surname");
        new BindingCategorySynapse()
                .setWeight(10.0)
                .link(cookSurnameBN, surnameCN);

        BindingNeuron cookProfessionBN =  new BindingNeuron(m, NEURON_EXTERNAL)
                .setLabel("cook (profession)");
        new InputObjectSynapse()
                .setWeight(10.0)
                .link(cookIN, cookProfessionBN)
                .adjustBias();

        CategoryNeuron professionCN = new BindingCategoryNeuron(m, NEURON_EXTERNAL)
                .setLabel("profession");
        new BindingCategorySynapse()
                .setWeight(10.0)
                .link(cookProfessionBN, professionCN);

        addOuterInhibitoryLoop(
                new InhibitoryNeuron(m, NEURON_EXTERNAL)
                        .setLabel("I-jackson"),
                false,
                jacksonForenameBN,
                jacksonCityBN
        );
        addOuterInhibitoryLoop(
                new InhibitoryNeuron(m, NEURON_EXTERNAL)
                        .setLabel("I-cook"),
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
                .link(forenameCN, forenameBN);

        new BindingCategoryInputSynapse()
                .setWeight(10.0)
                .link(surnameCN, surnameBN);

        new RelationInputSynapse()
                .setWeight(5.0)
                .link(relPT, surnameBN)
                .adjustBias();

        new SameObjectSynapse()
                .setWeight(10.0)
                .link(forenameBN, surnameBN)
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

        processTokens(dict, doc, "Jackson", "Cook");

        doc.process();
        doc.disconnect();
    }
}
