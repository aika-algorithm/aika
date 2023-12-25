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


import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.meta.sequences.SequenceModel;
import network.aika.Document;
import network.aika.queue.Step;
import network.aika.queue.steps.Anneal;
import network.aika.queue.steps.Fired;
import network.aika.text.TextReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.function.Predicate;

import static network.aika.meta.LabelUtil.generateTemplateInstanceLabels;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Parser<C extends Context> {

    protected static final Logger log = LoggerFactory.getLogger(Parser.class);

    protected Document initDocument(String txt, C context, ParserPhase phase) {
        Document doc = new Document(getPhraseModel().getModel(), txt);

        doc.setInstantiationCallback((tAct, iAct) ->
                generateTemplateInstanceLabels(iAct)
        );

        return doc;
    }

    protected abstract SequenceModel getPhraseModel();

    protected AIKADebugger debugger = null;

    protected Predicate<Step> getStepFilter(C context, ParserPhase phase) {
        return switch(phase) {
            case COUNTING -> getCountingStepFilter(context);
            case TRAINING -> getTrainingStepFilter(context);
            case INFERENCE -> null;
        };
    }

    private Predicate<Step> getCountingStepFilter(C context) {
        return s ->
                !(s instanceof Fired &&
                        ((Fired)s).getElement().getActivation().getNeuron() == getPhraseModel().getDictionary().getInputToken());
    }

    private Predicate<Step> getTrainingStepFilter(C context) {
        return s ->
                !(s instanceof Anneal &&
                        isTargetActivation(
                                context,
                                ((Anneal) s).getElement()
                        ));
    }

    private boolean isTargetActivation(C context, BindingActivation act) {
        PatternActivation pAct = act.getSamePatternActivation();
        if(pAct == null)
            return false;

        TextReference tr = pAct.getTextReference();
        if(tr == null)
            return false;

        Document doc = act.getDocument();

        return tr.getCharRange().getBegin() == 0 &&
                tr.getCharRange().getEnd() == doc.length();
    }

    protected abstract void prepareInputs(Document doc, C context);

    public Document process(String txt, C context, ParserPhase phase) {
        Document doc = initDocument(txt, context, phase);

        try {
            prepareInputs(doc, context);

            doc.process(getStepFilter(context, phase));

        } catch(Exception e) {
            log.warn("Error while training:", e);
        } finally {
            doc.disconnect();
        }

        return doc;
    }
}
