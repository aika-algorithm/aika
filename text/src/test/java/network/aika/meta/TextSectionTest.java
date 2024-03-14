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
package network.aika.meta;

import network.aika.Config;
import network.aika.Model;
import network.aika.debugger.AIKADebugger;
import network.aika.meta.entities.EntityModel;
import network.aika.meta.sequences.SequenceModel;
import network.aika.meta.sequences.PhraseModel;
import network.aika.meta.topics.TopicModel;
import network.aika.parser.Parser;
import network.aika.parser.ParserPhase;
import network.aika.Document;
import network.aika.text.TextReference;
import network.aika.text.Range;
import network.aika.tokenizer.WordTokenizer;
import network.aika.tokenizer.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static network.aika.meta.entities.EntityModel.ENTITY_LABEL;
import static network.aika.meta.topics.TopicModel.TOPIC_LABEL;
import static network.aika.parser.ParserPhase.COUNTING;
import static network.aika.parser.ParserPhase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class TextSectionTest extends Parser<TestContext> {

    public static final String HEADLINE_LABEL = "Headline";


    public static final String PROFILE_LABEL = "Profile";
    public static final String SKILL_LABEL = "Skill";
    public static final String PROFILE_HEADLINE_LABEL = "Profile-HL";

    public static final String TASKS_LABEL = "Tasks";

    String tasksHeadlineLabel = "Your Tasks";
    String profileHeadlineLabel = "Your Profile";

    private Dictionary dictionary;

    private Tokenizer tokenizer;
    private PhraseModel phraseModel;
    private EntityModel entityModel;

    protected EntityModel headlineEntity;

    private TopicModel topicModel;

    private TopicModel profileTopic;

    private EntityModel profileHeadline;

    private EntityModel skillEntity;

    private TopicModel tasksTopic;

    private EntityModel tasksHeadline;


/*    private String exampleTxt = "Java Softwaredeveloper\n" +
            " \n" +
            "Bla bla \n" +
            "\n" +
            tasksHeadline + "\n" +
            "<p> Bla programming testing bla \n" +
            "<p/>\n" +
            requirementsHeadline + "\n" +
            "<p> Bla java solr bla \n" +
            "<p/>\n";
*/
    private String exampleTxt = tasksHeadline + "\n" +
            "<p> Bla programming testing bla \n" +
            "<p/>\n";

    @BeforeEach
    public void init() {
        Model model = new Model();
        model.setConfig(
                new Config()
                        .setAlpha(null)
                        .setLearnRate(0.01)
                        .setCountingEnabled(true)
        );

        dictionary = new Dictionary(model);
        dictionary.initStaticNeurons();

        tokenizer = new WordTokenizer();

        topicModel = new TopicModel(TOPIC_LABEL, model);
        phraseModel = new PhraseModel(model);
        entityModel = new EntityModel(ENTITY_LABEL);

        phraseModel.initModelDependencies(dictionary, entityModel);
        entityModel.initModelDependencies(phraseModel, topicModel);
        topicModel.initModelDependencies(entityModel);

        topicModel.initTemplateNeurons();
        phraseModel.initStaticNeurons();
        entityModel.initTemplateNeurons();

        topicModel.initOuterSynapses();
        phraseModel.initOuterSynapses();
        entityModel.initOuterSynapses();

        headlineEntity = entityModel.instantiate(HEADLINE_LABEL, topicModel);

        profileTopic = topicModel.instantiate(PROFILE_LABEL, null);
        skillEntity = entityModel.instantiate(SKILL_LABEL, profileTopic);
        profileHeadline = headlineEntity.instantiate(PROFILE_HEADLINE_LABEL, profileTopic);

        tasksTopic = topicModel.instantiate(TASKS_LABEL, null);
        tasksHeadline = headlineEntity.instantiate(TASKS_LABEL, tasksTopic);

        model.setN(0);
    }

    @Override
    protected Document initDocument(String txt, TestContext context, ParserPhase phase) {
        Document doc = super.initDocument(txt, context, phase);
        if(phase == TRAINING) { // && doc.getId() == 9) { //  && txt.equalsIgnoreCase(exampleTxt)
            AIKADebugger.createAndShowGUI()
                    .setDocument(doc);
        }

        return doc;
    }

    @Override
    protected void prepareInputs(Document doc) {
        int[] tokenCounter = new int[1];
        tokenizer.tokenize(doc, (n, ref) -> {
                    doc.addToken(
                            dictionary.lookupInputToken(n),
                            ref
                    );
                    tokenCounter[0]++;
                }
        );
    }

    @Test
    public void testTextSections() {
        LOG.info("Start");

        process(tasksHeadlineLabel, null, COUNTING, null);
        process(profileHeadlineLabel, null, COUNTING, null);
        process(exampleTxt, null, COUNTING, null);

        dictionary.initInputTokenWeights();

        tasksHeadline.enable();
        tasksTopic.enable();
        process(tasksHeadlineLabel, new TestContext(TASKS_LABEL, null), TRAINING, null);
        tasksHeadline.disable();
        tasksTopic.disable();

        profileHeadline.enable();
        profileTopic.enable();
        process(profileHeadlineLabel, new TestContext(PROFILE_LABEL, null), TRAINING, null);
        profileHeadline.disable();
        profileTopic.disable();

        process(exampleTxt,
                new TestContext(
                        null,
                        List.of(
                                new TextReference(new Range(0, 1), new Range(0, 1))
                        )
                ),
                TRAINING,
                null
        );
    }

    @Override
    protected SequenceModel getPhraseModel() {
        return phraseModel;
    }
}
