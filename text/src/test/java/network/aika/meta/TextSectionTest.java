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

import network.aika.Model;
import network.aika.debugger.AIKADebugger;
import network.aika.meta.sequences.SequenceTemplateModel;
import network.aika.meta.sequences.PhraseTemplateModel;
import network.aika.meta.textsections.TypedTextSectionModel;
import network.aika.parser.ParserPhase;
import network.aika.parser.TrainingParser;
import network.aika.text.Document;
import network.aika.tokenizer.SimpleWordTokenizer;
import network.aika.tokenizer.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
    private PhraseTemplateModel templateModel;
    private TypedTextSectionModel textSectionModel;

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

        dictionary = new Dictionary(model);
        tokenizer = new SimpleWordTokenizer(dictionary);

        templateModel = new PhraseTemplateModel(model, dictionary);
        templateModel.initStaticNeurons();

        textSectionModel = new TypedTextSectionModel(templateModel);

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
        tokenizer.tokenize(doc, context, (n, pos, begin, end) ->
                doc.addToken(n, pos, begin, end, dictionary.getInputPatternNetTarget())
        );

        if(context != null && context.getHeadlineTargetString() != null) {
            textSectionModel.getHeadlineModel()
                    .addTargetTSHeadline(
                            doc,
                            Set.of(context.getHeadlineTargetLabel()),
                            0,
                            doc.length()
                    );
        }
    }

    @Test
    public void testTextSections() {
        log.info("Start");

        process(tasksHeadline, null, COUNTING);
        process(requirementsHeadline, null, COUNTING);
        process(exampleTxt, null, COUNTING);

        templateModel.initInputTokenWeights();
        templateModel.initTemplates();
        textSectionModel.initTextSectionTemplates();

        process(tasksHeadline, new TestContext(tasksHeadline, "Task-HL"), TRAINING);
        process(requirementsHeadline, new TestContext(requirementsHeadline, "Requi.-HL"), TRAINING);
        process(exampleTxt, null, TRAINING);
    }

    @Override
    protected SequenceTemplateModel getTemplateModel() {
        return templateModel;
    }

}
