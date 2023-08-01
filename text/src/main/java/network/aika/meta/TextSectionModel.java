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
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.neurons.*;
import network.aika.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static network.aika.meta.NetworkMotivs.addPositiveFeedbackLoop;
import static network.aika.meta.NetworkMotivs.addRelation;

/**
 *
 * @author Lukas Molzberger
 */
public class TextSectionModel {

    private static final Logger log = LoggerFactory.getLogger(TextSectionModel.class);

    protected static double NEG_MARGIN_TS_BEGIN = 1.1;
    protected static double NEG_MARGIN_TS_END = 1.1;

    protected static double NEG_MARGIN_TS = 1.1;

    protected PhraseTemplateModel phraseModel;


    protected Model model;

    protected NeuronProvider textSectionRelationPT;
    protected NeuronProvider textSectionRelationNT;

    protected NeuronProvider textSectionPatternN;

    protected NeuronProvider textSectionBeginBN;

    protected NeuronProvider textSectionEndBN;

    protected double bindingNetTarget = 2.5;

    protected double patternNetTarget = 0.7;


    public TextSectionModel(PhraseTemplateModel phraseModel) {
        this.phraseModel = phraseModel;
        model = phraseModel.getModel();
    }

    protected void initTextSectionTemplates() {
        log.info("Text-Section");

        textSectionRelationPT = TokenPositionRelationNeuron.lookupRelation(model, -300, -1)
                .getProvider(true);

        textSectionRelationNT = TokenPositionRelationNeuron.lookupRelation(model, 1, 300)
                .getProvider(true);

        textSectionPatternN = new PatternNeuron()
                .init(model, "Abstract Text-Section")
                .getProvider(true);

        textSectionBeginBN = new BindingNeuron()
                .init(model, "Abstract Text-Section-Begin")
                .getProvider(true);

        textSectionEndBN = new BindingNeuron()
                .init(model, "Abstract Text-Section-End")
                .getProvider(true);

        addRelation(
                textSectionBeginBN.getNeuron(),
                textSectionEndBN.getNeuron(),
                textSectionRelationPT.getNeuron(),
                5.0,
                10.0
        );

        addPositiveFeedbackLoop(
                textSectionBeginBN.getNeuron(),
                textSectionPatternN.getNeuron(),
                2.5,
                patternNetTarget,
                bindingNetTarget,
                0.0,
                false
        );

        addPositiveFeedbackLoop(
                textSectionEndBN.getNeuron(),
                textSectionPatternN.getNeuron(),
                2.5,
                patternNetTarget,
                bindingNetTarget,
                0.0,
                false
        );
    }

    public PatternActivation addTextSection(Document doc, int begin, int end) {
        return new PatternActivation(doc.createActivationId(), doc, textSectionPatternN.getNeuron());
    }
}
