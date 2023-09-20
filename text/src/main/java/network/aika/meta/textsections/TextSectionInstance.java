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
package network.aika.meta.textsections;

import network.aika.InstantiationUtil;
import network.aika.Model;
import network.aika.TemplateModel;
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.neurons.Neuron.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.meta.textsections.TextSectionModel.TEXT_SECTION_LABEL;
import static network.aika.meta.textsections.TypedTextSectionModel.*;

/**
 *
 * @author Lukas Molzberger
 */
public class TextSectionInstance extends InstantiationUtil<TextSectionInstance> implements Writable {

    TypedTextSectionModel tsModel;

    public TextSectionInstance(TypedTextSectionModel tsModel) {
        this.tsModel = tsModel;
    }

    @Override
    public TemplateModel getTemplateModel() {
        return tsModel;
    }

    public Model getModel() {
        return tsModel.getModel();
    }

    public PhraseModel getPhraseModel() {
        return tsModel.phraseModel;
    }

    @Override
    protected Document createDocument(String label) {
        String headline = label + " " + HEADLINE_LABEL;
        String textSection = label + " " + TEXT_SECTION_LABEL;

        Document doc = new Document(getModel(), headline + " " + textSection);

        AIKADebugger.createAndShowGUI()
               .setDocument(doc);

        doc.setInstantiationCallback((tAct, iAct) -> {
            generateLabel(tAct, iAct, label);

            if(isPartOfHeadline(tAct) || isHint(tAct)) {
                ConjunctiveSynapse s = (ConjunctiveSynapse) iAct.getNeuron().makeAbstract();
                s.setWeight(PASSIVE_SYNAPSE_WEIGHT);
            }
        });

        return doc;
    }

    @Override
    protected void mapResults(Document doc) {
        getPhraseModel().getPatternNeuron().setTemplateOnly(false);
    }

    private void generateLabel(Activation tAct, Activation iAct, String label) {
        iAct.getNeuron().setLabel(
                tAct.getLabel()
                        .replace(HEADLINE_LABEL, getHeadlineLabel(label))
                        .replace(TEXT_SECTION_LABEL, getTextSectionLabel(label))
        );
    }


    private boolean isPartOfHeadline(Activation tAct) {
        String l = tAct.getLabel();
        return l.contains(HEADLINE_LABEL) && !l.contains(TEXT_SECTION_LABEL);
    }

    private boolean isHint(Activation tAct) {
        return tAct.getLabel().contains("Hint");
    }

    @Override
    public void write(DataOutput out) throws IOException {
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
    }
}
