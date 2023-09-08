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
import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.meta.TargetInput;
import network.aika.meta.entities.EntityModel;
import network.aika.text.Document;
import network.aika.text.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.meta.Dictionary.INPUT_TOKEN_NET_TARGET;
import static network.aika.meta.NetworkMotifs.*;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class TypedTextSectionModel extends TextSectionModel {

    private static final Logger log = LoggerFactory.getLogger(TypedTextSectionModel.class);


    protected EntityModel entityModel;

    protected TargetInput targetInput;


    protected NeuronProvider headlineBN;

    protected NeuronProvider headlineTargetInput;


    protected NeuronProvider tsHeadlineBN;

    protected NeuronProvider textSectionHintBN;

    protected NeuronProvider tsBeginInhibitoryN;

    protected NeuronProvider tsEndInhibitoryN;

    protected NeuronProvider tsInhibitoryN;

    protected NeuronProvider targetInputBN;

    public TypedTextSectionModel(EntityModel entityModel) {
        super(entityModel.getPhraseModel());
        this.entityModel = entityModel;
    }

    public PatternNeuron addTextSection(String headlineTarget) {
        return targetInput.addTarget(headlineTarget);
    }

    public void addTextSectionTarget(Document doc, Range posRange, Range charRange, String tsTarget) {
        log.info(doc.getContent() + " : " + tsTarget);

        doc.addToken(
                addTextSection(tsTarget),
                posRange,
                charRange,
                INPUT_TOKEN_NET_TARGET
        );
    }

    public void initStaticNeurons() {
        super.initStaticNeurons();

        targetInput = new TargetInput(model, "Text Section");
        targetInput.initTargetInput();

        log.info("Typed Text-Section");

        textSectionHintBN = new BindingNeuron(model)
                .setLabel("Abstr. Text-Section-Hint")
                .getProvider(true);

        double netTarget = 2.5;

        EntityModel.EntityInstance headlineEntity = entityModel.addEntityPattern("Abstr. Text-Section-Headline");

        headlineBN = headlineEntity.entityBN()
                .getProvider(true);

        headlineTargetInput = headlineEntity.targetInputPN()
                .getProvider(true);

        makeAbstract((PatternNeuron) headlineTargetInput.getNeuron())
                .setWeight(2.0)
                .adjustBias();

        tsHeadlineBN = addBindingNeuron(
                headlineEntity.entityPatternN(),
                "Abstr. Text-Section Headline",
                10.0,
                bindingNetTarget
        )
                .getProvider(true);

        addRelation(
                tsHeadlineBN.getNeuron(),
                beginBN.getNeuron(),
                phraseModel.relPT.getNeuron(),
                5.0,
                10.0,
                false
        );

        addPositiveFeedbackLoop(
                tsHeadlineBN.getNeuron(),
                patternN.getNeuron(),
                2.5,
                0.0,
                false
        );

        textSectionHintBN = new BindingNeuron(model)
                .setLabel("Abstract Text-Section Hint")
                .setTargetNet(bindingNetTarget)
                .getProvider(true);

        addPositiveFeedbackLoop(
                textSectionHintBN.getNeuron(),
                patternN.getNeuron(),
                2.5,
                0.0,
                false
        );

        sectionHintRelations(beginBN.getNeuron(), relationPT.getNeuron());
        sectionHintRelations(endBN.getNeuron(), relationNT.getNeuron());

        targetInputBN = targetInput.createTargetInputBindingNeuron(
                patternN.getNeuron(),
                patternNetTarget
        ).getProvider(true);

        tsBeginInhibitoryN = new InnerInhibitoryNeuron(model)
                .setLabel("I TS Begin")
                .getProvider(true);


        addInnerInhibitoryLoop(
                beginBN.getNeuron(),
                tsBeginInhibitoryN.getNeuron(),
                NEG_MARGIN_TS_BEGIN * -netTarget
        );

        tsEndInhibitoryN = new InnerInhibitoryNeuron(model)
                .setLabel("I TS End")
                .getProvider(true);

        addInnerInhibitoryLoop(
                endBN.getNeuron(),
                tsBeginInhibitoryN.getNeuron(),
                NEG_MARGIN_TS_END * -netTarget
        );

        tsInhibitoryN = new OuterInhibitoryNeuron(model)
                .setLabel("I TS")
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

        targetInput.setTemplateOnly(true);
    }

    public PatternNeuron addHeadline(String headlineLabel) {
        return model.lookupInputNeuron(headlineLabel, headlineTargetInput.getNeuron());
    }

    public void addHeadlineTarget(Document doc, Range posRange, Range charRange, String headlineLabel) {
        doc.addToken(
                addHeadline(headlineLabel),
                posRange,
                charRange,
                INPUT_TOKEN_NET_TARGET
        );
    }

    private void sectionHintRelations(BindingNeuron fromBN, LatentRelationNeuron relN) {
        addRelation(
                fromBN,
                textSectionHintBN.getNeuron(),
                relN,
                5.0,
                10.0,
                false
        );
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeLong(headlineBN.getId());
        out.writeLong(headlineTargetInput.getId());
        out.writeLong(textSectionHintBN.getId());
        out.writeLong(tsBeginInhibitoryN.getId());
        out.writeLong(tsEndInhibitoryN.getId());
        out.writeLong(tsInhibitoryN.getId());
        out.writeLong(targetInputBN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        headlineBN = m.lookupNeuronProvider(in.readLong());
        headlineTargetInput = m.lookupNeuronProvider(in.readLong());
        textSectionHintBN = m.lookupNeuronProvider(in.readLong());
        tsBeginInhibitoryN = m.lookupNeuronProvider(in.readLong());
        tsEndInhibitoryN = m.lookupNeuronProvider(in.readLong());
        tsInhibitoryN = m.lookupNeuronProvider(in.readLong());
        targetInputBN = m.lookupNeuronProvider(in.readLong());
    }
}
