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
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.synapses.Synapse;
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

    private PhraseTemplateModel templateModel;
    private Tokenizer tokenizer;

/*    private String exampleTxt = "Java Softwaredeveloper\n" +
            " \n" +
            "Bla bla \n" +
            "\n" +
            tasksHeadline + "\n" +
            "Bla programming testing bla \n" +
            "\n" +
            requirementsHeadline + "\n" +
            "Bla java solr bla \n" +
            "\n";
*/
    private String exampleTxt = tasksHeadline + "\n" +
            "Bla programming testing bla \n" +
            "\n";

    @BeforeEach
    public void init() {
        Model model = new Model();

        templateModel = new PhraseTemplateModel(model);
        templateModel.initStaticNeurons();

        model.setN(0);

        tokenizer = new SimpleWordTokenizer(templateModel);
    }

    @Override
    protected Document initDocument(String txt, TestContext context, ParserPhase phase) {
        Document doc = super.initDocument(txt, context, phase);
        if(phase == TRAINING && txt.equalsIgnoreCase(exampleTxt)) {
            AIKADebugger.createAndShowGUI(doc);
        }

        return doc;
    }
/*
    @Override
    public boolean check(Synapse s, Activation iAct) {
       return iAct.getTokenPos() == 0 &&
                ((currentContext != null && currentContext.isHeadlineTarget()) == templateModel.textSectionModel.isHeadlinePrimaryInput((BindingNeuron) s.getOutput()));
    }
*/
    @Test
    public void testTextSections() {
        log.info("Start");

        process(tasksHeadline, null, COUNTING);
        process(requirementsHeadline, null, COUNTING);
        process(exampleTxt, null, COUNTING);

        templateModel.initTemplates();

        process(tasksHeadline, new TestContext(tasksHeadline, "Task-HL"), TRAINING);
        process(requirementsHeadline, new TestContext(requirementsHeadline, "Requi.-HL"), TRAINING);
        process(exampleTxt, null, TRAINING);
    }

    @Override
    protected void addTargets(Document doc, TestContext context) {
        if(context != null && context.getHeadlineTargetString() != null) {
            templateModel.getTextSectionModel()
                    .addTargetTSHeadline(
                            doc,
                            Set.of(context.getHeadlineTargetLabel()),
                            0,
                            doc.length()
                    );
        }
    }

    @Override
    protected AbstractTemplateModel getTemplateModel() {
        return templateModel;
    }

    @Override
    public Tokenizer getTokenizer() {
        return tokenizer;
    }
}
