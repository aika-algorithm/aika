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
package network.aika.parser;


import network.aika.Config;
import network.aika.debugger.AIKADebugger;
import network.aika.meta.sequences.SequenceTemplateModel;
import network.aika.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static network.aika.parser.ParserPhase.TRAINING;
import static network.aika.steps.Phase.ANNEAL;
import static network.aika.steps.Phase.INFERENCE;
import static network.aika.steps.keys.QueueKey.MAX_ROUND;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Parser<C extends Context> {

    protected static final Logger log = LoggerFactory.getLogger(Parser.class);

    protected Document initDocument(String txt, C context, ParserPhase phase) {
        Document doc = new Document(getTemplateModel().getModel(), txt);

        Config conf = new Config()
                .setAlpha(null)
                .setLearnRate(0.01)
                .setTrainingEnabled(phase == TRAINING)
                .setMetaInstantiationEnabled(phase == TRAINING)
                .setCountingEnabled(true);

        doc.setConfig(conf);

        return doc;
    }

    protected abstract SequenceTemplateModel getTemplateModel();

    protected AIKADebugger debugger = null;

    public Document process(String txt, C context, ParserPhase phase) {
        Document doc = initDocument(txt, context, phase);

        try {
            infer(doc, context, phase);
            anneal(doc);
        } catch(Exception e) {
            log.warn("Error while training:", e);
        } finally {
            doc.disconnect();
        }

        return doc;
    }

    protected void infer(Document doc, C context, ParserPhase phase) {
        if(phase == ParserPhase.TRAINING) {
    //        debugger = AIKADebugger.createAndShowGUI(doc);
        }

        doc.setFeedbackTriggerRound();

        prepareInputs(doc, context);

        doc.process(MAX_ROUND, INFERENCE);
    }

    protected abstract void prepareInputs(Document doc, C context);

    public void anneal(Document doc) {
        doc.anneal();
        doc.process(MAX_ROUND, ANNEAL);
    }

    protected static void waitForClick(AIKADebugger debugger) {
        if(debugger != null)
            debugger.getStepManager().waitForClick();
    }
}
