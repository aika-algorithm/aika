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

import network.aika.Model;
import network.aika.elements.neurons.*;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.text.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class HeadlineModel {

    private static final Logger log = LoggerFactory.getLogger(TypedTextSectionModel.class);


    protected Model model;

    private PhraseModel phraseModel;

    protected NeuronProvider headlineBN;

    protected double headlineInputPatternNetTarget = 5.0;

    public HeadlineModel(PhraseModel phraseModel) {
        this.phraseModel = phraseModel;
        this.model = phraseModel.getModel();
    }

    public void addTargetTSHeadline(Document doc, Set<String> headlineLabels, Range tokenPosRange, Range charRange) {
        log.info(doc.getContent() + " : " + headlineLabels.stream().collect(Collectors.joining(", ")));

        headlineLabels.forEach(label -> {
            TokenNeuron targetInputN = phraseModel.createTargetInputNeuron(label);

            doc.addToken(
                    targetInputN,
                    tokenPosRange,
                    charRange,
                    phraseModel.getDictionary().getInputPatternNetTarget()
            );
        });
    }

    protected void initHeadlineTemplates() {
        double netTarget = 2.5;

        headlineBN = addBindingNeuron(
                phraseModel.getPatternNeuron().getNeuron(),
                "Text-Section-Headline",
                10.0,
                headlineInputPatternNetTarget,
                netTarget
        ).getProvider(true);

    }
}
