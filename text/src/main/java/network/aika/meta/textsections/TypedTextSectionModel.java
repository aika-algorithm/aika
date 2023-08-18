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

import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class TypedTextSectionModel extends TextSectionModel {

    private static final Logger log = LoggerFactory.getLogger(TypedTextSectionModel.class);

    protected NeuronProvider headlineBN;

    protected NeuronProvider textSectionHintBN;

    protected NeuronProvider tsBeginInhibitoryN;

    protected NeuronProvider tsEndInhibitoryN;

    protected NeuronProvider tsInhibitoryN;

    public TypedTextSectionModel(PhraseModel phraseModel) {
        super(phraseModel);
    }

    public void addTargetTextSections(Document doc, Set<String> tsLabels) {
        log.info(doc.getContent() + " : " + tsLabels.stream().collect(Collectors.joining(", ")));
    }

    public void initTextSectionTemplates() {
        super.initTextSectionTemplates();

        log.info("Typed Text-Section");

        textSectionHintBN = new BindingNeuron()
                .init(model, "Text-Section-Hint")
                .getProvider(true);

        double netTarget = 2.5;

        headlineBN = addBindingNeuron(
                phraseModel.getPatternNeuron().getNeuron(),
                "Text-Section-Headline",
                10.0,
                phraseModel.patternNetTarget,
                netTarget
        ).getProvider(true);

        addPositiveFeedbackLoop(
                headlineBN.getNeuron(),
                patternN.getNeuron(),
                patternNetTarget,
                bindingNetTarget,
                2.5,
                0.0,
                false
        );

        addRelation(
                headlineBN.getNeuron(),
                beginBN.getNeuron(),
                phraseModel.relPT.getNeuron(),
                5.0,
                10.0
        );

        textSectionHintBN = new BindingNeuron()
                .init(model, "Abstract Text-Section Hint")
                .getProvider(true);

        addPositiveFeedbackLoop(
                textSectionHintBN.getNeuron(),
                patternN.getNeuron(),
                2.5,
                patternNetTarget,
                bindingNetTarget,
                0.0,
                false
        );

        sectionHintRelations(beginBN.getNeuron(), relationPT.getNeuron());
        sectionHintRelations(endBN.getNeuron(), relationNT.getNeuron());


        tsBeginInhibitoryN = new InnerInhibitoryNeuron()
                .init(model, "I TS Begin")
                .getProvider(true);


        addInnerInhibitoryLoop(
                beginBN.getNeuron(),
                tsBeginInhibitoryN.getNeuron(),
                NEG_MARGIN_TS_BEGIN * -netTarget
        );

        tsEndInhibitoryN = new InnerInhibitoryNeuron()
                .init(model, "I TS End")
                .getProvider(true);

        addInnerInhibitoryLoop(
                endBN.getNeuron(),
                tsBeginInhibitoryN.getNeuron(),
                NEG_MARGIN_TS_END * -netTarget
        );

        tsInhibitoryN = new OuterInhibitoryNeuron()
                .init(model, "I TS")
                .getProvider(true);

        addOuterInhibitoryLoop(
                textSectionHintBN.getNeuron(),
                tsInhibitoryN.getNeuron(),
                NEG_MARGIN_TS * -netTarget
        );

        addOuterInhibitoryLoop(
                beginBN.getNeuron(),
                tsInhibitoryN.getNeuron(),
                NEG_MARGIN_TS * -netTarget
        );

        addOuterInhibitoryLoop(
                endBN.getNeuron(),
                tsInhibitoryN.getNeuron(),
                NEG_MARGIN_TS * -netTarget
        );
    }

    private void sectionHintRelations(BindingNeuron fromBN, LatentRelationNeuron relN) {
        addRelation(
                fromBN,
                textSectionHintBN.getNeuron(),
                relN,
                5.0,
                10.0
        );
    }
}
