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
import network.aika.meta.textsections.TypedTextSectionModel;
import network.aika.parser.ParserPhase;
import network.aika.parser.TrainingParser;
import network.aika.text.Document;
import network.aika.text.GroundRef;
import network.aika.text.Range;
import network.aika.tokenizer.SimpleWordTokenizer;
import network.aika.tokenizer.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.meta.Dictionary.INPUT_TOKEN_NET_TARGET;
import static network.aika.parser.ParserPhase.COUNTING;
import static network.aika.parser.ParserPhase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class TextSectionTest extends TrainingParser<TestContext> {

    String tasksHeadline = "Your Tasks";
    String requirementsHeadline = "Your Profile";

    private Dictionary dictionary;

    private Tokenizer tokenizer;
    private PhraseModel phraseModel;
    private EntityModel entityModel;
    private TypedTextSectionModel textSectionModel;

    private TypedTextSectionModel.TextSectionInstance profileTS;

    private TypedTextSectionModel.TextSectionInstance tasksTS;

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

        tokenizer = new SimpleWordTokenizer(dictionary);

        phraseModel = new PhraseModel(model, dictionary);
        phraseModel.initStaticNeurons();

        entityModel = new EntityModel(phraseModel);
        entityModel.initStaticNeurons();

        textSectionModel = new TypedTextSectionModel(entityModel);
        textSectionModel.initStaticNeurons();

        profileTS = textSectionModel.addTextSectionType("Profile");
        tasksTS = textSectionModel.addTextSectionType("Tasks");

        model.setN(0);
    }

    @Override
    protected Document initDocument(String txt, TestContext context, ParserPhase phase) {
        Document doc = super.initDocument(txt, context, phase);
        if(phase == TRAINING) { //  && txt.equalsIgnoreCase(exampleTxt)
            AIKADebugger.createAndShowGUI(doc);
        }

        return doc;
    }

    @Override
    protected void prepareInputs(Document doc, TestContext context) {
        int[] tokenCounter = new int[1];
        tokenizer.tokenize(doc, context, (n, pos, begin, end) -> {
                    doc.addToken(n, pos, begin, end, INPUT_TOKEN_NET_TARGET);
                    tokenCounter[0]++;
                }
        );

        phraseModel.addPhraseTarget(doc, tokenCounter[0]);

        if(context != null && context.getTextSectionType() != null) {
            textSectionModel
                    .addHeadlineTarget(
                            doc,
                            new GroundRef(
                                    new Range(0, tokenCounter[0]),
                                    new Range(0, doc.length())
                            ),
                            context.getTextSectionType()
                    );
        }
    }

    @Test
    public void testTextSections() {
        log.info("Start");

        process(tasksHeadline, null, COUNTING);
        process(requirementsHeadline, null, COUNTING);
        process(exampleTxt, null, COUNTING);

        dictionary.initInputTokenWeights();

        process(tasksHeadline, new TestContext(tasksHeadline, "Task-HL"), TRAINING);
        process(requirementsHeadline, new TestContext(requirementsHeadline, "Requi.-HL"), TRAINING);
        process(exampleTxt, null, TRAINING);
    }

    @Override
    protected SequenceModel getPhraseModel() {
        return phraseModel;
    }
}
