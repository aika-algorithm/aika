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
import network.aika.meta.textsections.TextSectionInstance;
import network.aika.meta.textsections.TypedTextSectionModel;
import network.aika.parser.ParserPhase;
import network.aika.parser.TrainingParser;
import network.aika.Document;
import network.aika.text.TextReference;
import network.aika.text.Range;
import network.aika.tokenizer.WordTokenizer;
import network.aika.tokenizer.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static network.aika.parser.ParserPhase.COUNTING;
import static network.aika.parser.ParserPhase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class TextSectionTest extends TrainingParser<TestContext> {

    public static final String PROFILE_LABEL = "Profile";
    public static final String TASKS_LABEL = "Tasks";

    String tasksHeadline = "Your Tasks";
    String requirementsHeadline = "Your Profile";

    private Dictionary dictionary;

    private Tokenizer tokenizer;
    private PhraseModel phraseModel;
    private EntityModel entityModel;
    private TypedTextSectionModel textSectionModel;

    private TextSectionInstance profileTS;

    private TextSectionInstance tasksTS;

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

        phraseModel = new PhraseModel(model, dictionary);
        phraseModel.initStaticNeurons();

        entityModel = new EntityModel(phraseModel);
        entityModel.initStaticNeurons();

        textSectionModel = new TypedTextSectionModel(entityModel);
        textSectionModel.initStaticNeurons();

        textSectionModel.getHeadlineEntity().enable();

        profileTS = new TextSectionInstance(textSectionModel)
                .instantiate(PROFILE_LABEL);

        tasksTS = new TextSectionInstance(textSectionModel)
                .instantiate(TASKS_LABEL);

        textSectionModel.getHeadlineEntity().disable();

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
    protected void prepareInputs(Document doc, TestContext context) {
        int[] tokenCounter = new int[1];
        tokenizer.tokenize(doc, context, (n, ref) -> {
                    doc.addToken(
                            dictionary.lookupInputToken(n),
                            ref
                    );
                    tokenCounter[0]++;
                }
        );
    }

    @Override
    protected void prepareTargets(Document doc, TestContext context) {
    }

    @Test
    public void testTextSections() {
        log.info("Start");

        process(tasksHeadline, null, COUNTING);
        process(requirementsHeadline, null, COUNTING);
        process(exampleTxt, null, COUNTING);

        dictionary.initInputTokenWeights();

        tasksTS.enable();
        process(tasksHeadline, new TestContext(TASKS_LABEL, null), TRAINING);
        tasksTS.disable();

        profileTS.enable();
        process(requirementsHeadline, new TestContext(PROFILE_LABEL, null), TRAINING);
        profileTS.disable();

        process(exampleTxt,
                new TestContext(
                        null,
                        List.of(
                                new TextReference(new Range(0, 1), new Range(0, 1))
                        )
                ),
                TRAINING
        );
    }

    @Override
    protected SequenceModel getPhraseModel() {
        return phraseModel;
    }
}
