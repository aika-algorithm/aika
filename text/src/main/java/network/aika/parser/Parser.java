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


import network.aika.Context;
import network.aika.callbacks.InstantiationCallback;
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.Neuron;
import network.aika.meta.sequences.SequenceModel;
import network.aika.Document;
import network.aika.queue.Step;
import network.aika.queue.steps.Anneal;
import network.aika.queue.steps.Fired;
import network.aika.text.TextReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.function.Consumer;
import java.util.function.Predicate;

import static network.aika.meta.LabelUtil.generateTemplateInstanceLabels;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Parser<C extends Context> {

    protected static final Logger LOG = LoggerFactory.getLogger(Parser.class);

    protected Document initDocument(String txt, C context, ParserPhase phase) {
        Document doc = new Document(getPhraseModel().getModel(), txt);
        doc.setContext(context);

        doc.setInstantiationCallback(new InstantiationCallback() {
            @Override
            public Neuron resolveInstance(Neuron template, Document doc) {
                return null;
            }

            @Override
            public void onInstantiation(Activation tAct, Activation iAct) {
                generateTemplateInstanceLabels(iAct);
            }
        });
        return doc;
    }

    protected abstract SequenceModel getPhraseModel();

    protected AIKADebugger debugger = null;

    protected Predicate<Step> getStepFilter(Document doc, ParserPhase phase) {
        return switch(phase) {
            case COUNTING -> getCountingStepFilter(doc);
            case TRAINING -> getTrainingStepFilter(doc);
            case INFERENCE -> null;
        };
    }

    private Predicate<Step> getCountingStepFilter(Document doc) {
        return s ->
                !(s instanceof Fired &&
                        ((Fired)s).getElement().getActivation().getNeuron() == getPhraseModel().getDictionary().getInputToken());
    }

    private Predicate<Step> getTrainingStepFilter(Document doc) {
        return s ->
                !(s instanceof Anneal &&
                        isTargetActivation(
                                ((Anneal) s).getElement()
                        ));
    }

    private boolean isTargetActivation(BindingActivation act) {
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

    protected abstract void prepareInputs(Document doc);

    public void process(String txt, C context, ParserPhase phase, Consumer<Document> mapResults) {
        Document doc = initDocument(txt, context, phase);

        try {
            prepareInputs(doc);

            doc.process(getStepFilter(doc, phase));

            getPhraseModel().getModel().process();

            if(mapResults != null)
                mapResults.accept(doc);
        } catch(Exception e) {
            handleException(context, e);
        } finally {
            try {
                doc.disconnect();
            } catch (Exception e) {
                LOG.error("Exception while disconnecting document: ", e);
                
                handleException(context, e);
            }
        }
    }

    protected void handleException(C context, Exception e) {
        LOG.warn("Error while processing:", e);
    }
}
