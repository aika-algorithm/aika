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
package network.aika.meta.sequences;

import network.aika.Model;
import network.aika.elements.neurons.*;
import network.aika.meta.Dictionary;
import network.aika.text.Document;
import network.aika.text.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.meta.Dictionary.INPUT_TOKEN_NET_TARGET;

/**
 *
 * @author Lukas Molzberger
 */
public class PhraseModel extends SequenceModel {

    private static final Logger log = LoggerFactory.getLogger(PhraseModel.class);

    NeuronProvider upperCaseN;

    public PhraseModel(Model m, Dictionary dict) {
        super(m, dict);
    }

    @Override
    public String getPatternType() {
        return "Phrase";
    }

    @Override
    public void initStaticNeurons() {
        super.initStaticNeurons();

        upperCaseN = new PatternCategoryNeuron()
                .init(model, "Upper Case")
                .getProvider(true);
    }

    public void addPhraseTarget(Document doc, int numTokens) {
        doc.addToken(
                targetInput.getTargetInput().getNeuron(),
                new Range(0, numTokens),
                new Range(0, doc.length()),
                INPUT_TOKEN_NET_TARGET
        );
    }

    @Override
    protected void initTemplateBindingNeurons() {
        primaryBN = createPrimaryBindingNeuron()
                .getProvider(true);

        createTargetInputBindingNeuron();

        expandContinueBindingNeurons(
                1,
                primaryBN.getNeuron(),
                5,
                1
        );

        expandContinueBindingNeurons(
                1,
                primaryBN.getNeuron(),
                5,
                -1
        );
    }
}
