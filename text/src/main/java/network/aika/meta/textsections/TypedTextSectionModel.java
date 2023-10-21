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
import network.aika.TemplateModel;
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.LatentRelationNeuron;
import network.aika.meta.entities.EntityInstance;
import network.aika.meta.entities.EntityModel;
import network.aika.Document;
import network.aika.text.TextReference;
import network.aika.text.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.meta.LabelUtil.getAbstractBindingNeuronLabel;
import static network.aika.meta.LabelUtil.getAbstractPatternLabel;
import static network.aika.meta.NetworkMotifs.*;
import static network.aika.meta.TargetInput.TARGET_INPUT_LABEL;

/**
 *
 * @author Lukas Molzberger
 */
public class TypedTextSectionModel extends TextSectionModel implements TemplateModel {

    private static final Logger log = LoggerFactory.getLogger(TypedTextSectionModel.class);

    public static final String HEADLINE_LABEL = "Headline";


    protected EntityModel entityModel;

    protected EntityInstance headlineEntity;

    protected BindingNeuron tsHeadlineBN;

//    protected BindingNeuron tsTypeBN;

    protected BindingNeuron hintBN;

    protected PatternNeuron hintInputPN;

    protected OuterInhibitoryNeuron outerTsBeginInhibitoryN;

    protected OuterInhibitoryNeuron outerTsEndInhibitoryN;


    public TypedTextSectionModel(EntityModel entityModel) {
        super(entityModel.getPhraseModel());
        this.entityModel = entityModel;
    }

    public Model getModel() {
        return phraseModel.getModel();
    }


    public static String getHeadlineLabel(String label) {
        return label + "-HL";
    }

    public static String getTextSectionLabel(String label) {
        return label + "-TS";
    }

    public void setTemplateOnly(boolean templateOnly) {
        headlineEntity.setTemplateOnly(templateOnly);
        textSectionPatternN.setTemplateOnly(templateOnly, true);
        tsHeadlineBN.setTemplateOnly(templateOnly, true);
        beginBN.setTemplateOnly(templateOnly, true);
        endBN.setTemplateOnly(templateOnly, true);
        beginEndBN.setTemplateOnly(templateOnly, true);
        hintBN.setTemplateOnly(templateOnly, true);
    }

    public void initStaticNeurons() {
        super.initStaticNeurons();

        log.info("Typed " + TEXT_SECTION_LABEL);

        initHeadline();
        initHint();
        initTextSection();
    }

    private void initHeadline() {
        headlineEntity = new EntityInstance(entityModel)
                .instantiate(HEADLINE_LABEL);

        tsHeadlineBN = addBindingNeuron(
                headlineEntity.entityPatternN,
                "Abstr. " + TEXT_SECTION_LABEL + " " + HEADLINE_LABEL,
                10.0,
                bindingNetTarget
        );
        tsHeadlineBN.makeAbstract()
                .setWeight(DEFAULT_INPUT_CATEGORY_SYNAPSE_WEIGHT)
                .adjustBias();

        addRelation(
                tsHeadlineBN,
                beginBN,
                phraseModel.relPT,
                5.0,
                10.0,
                false
        );

        addRelation(
                tsHeadlineBN,
                beginEndBN,
                phraseModel.relPT,
                5.0,
                10.0,
                false
        );

        addPositiveFeedbackLoop(
                tsHeadlineBN,
                textSectionPatternN,
                2.5,
                0.0,
                false,
                false
        );
/*
        tsTypeBN = addBindingNeuron(
                patternN,
                "Abstr. " + TEXT_SECTION_LABEL + " Type",
                10.0,
                bindingNetTarget
        );
        tsTypeBN.makeAbstract()
                .setWeight(2.0);

        addPositiveFeedbackLoop(
                tsTypeBN,
                headlineEntity.entityPatternN,
                2.5,
                0.0,
                false
        );
 */
    }

    private void initHint() {
        hintInputPN = createTextSectionInput(TEXT_SECTION_LABEL + " Hint");

        hintBN = addBindingNeuron(
                hintInputPN,
                getAbstractBindingNeuronLabel(TEXT_SECTION_LABEL + " Hint"),
                10.0,
                bindingNetTarget
        );

        hintBN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addPositiveFeedbackLoop(
                hintBN,
                textSectionPatternN,
                2.5,
                0.0,
                false,
                true
        );

        sectionHintRelations(beginBN, relationPT);
        sectionHintRelations(beginEndBN, relationPT);
        sectionHintRelations(endBN, relationNT);
    }

    private void sectionHintRelations(BindingNeuron fromBN, LatentRelationNeuron relN) {
        addRelation(
                fromBN,
                hintBN,
                relN,
                5.0,
                10.0,
                false
        );
    }

    private void initTextSection() {
        outerTsBeginInhibitoryN = new OuterInhibitoryNeuron(model)
                .setLabel("Outer Begin " + TEXT_SECTION_LABEL)
                .setPersistent(true);

        addOuterInhibitoryLoop(
                beginBN,
                outerTsBeginInhibitoryN,
                NEG_MARGIN_TS * -beginBN.getTargetNet()
        );

        outerTsEndInhibitoryN = new OuterInhibitoryNeuron(model)
                .setLabel("Outer End " + TEXT_SECTION_LABEL)
                .setPersistent(true);

        addOuterInhibitoryLoop(
                endBN,
                outerTsEndInhibitoryN,
                NEG_MARGIN_TS * -endBN.getTargetNet()
        );
    }

    public PatternNeuron getHeadlinePattern(String tsType) {
        return getModel().getInputNeuron(
                getAbstractPatternLabel(
                        getHeadlineLabel(tsType)
                ),
                headlineEntity.entityPatternN
        );
    }

    public PatternNeuron addHeadline(String label) {
        return model.lookupInputNeuron(
                getHeadlineLabel(label) + " " + TARGET_INPUT_LABEL,
                headlineEntity.targetInputPN
        );
    }

    public void addHeadlineTarget(Document doc, TextReference textReference, String label) {
        doc.addToken(
                addHeadline(label),
                textReference
        );
    }

    public PatternNeuron getTextSectionPattern(String tsType) {
        return getModel().getInputNeuron(
                getAbstractPatternLabel(
                        getTextSectionLabel(tsType)
                ),
                textSectionPatternN
        );
    }

    public void prepareInstantiation() {
        setTemplateOnly(false);
        phraseModel.getPatternNeuron().setTemplateOnly(true);
        beginInputPN.setTemplateOnly(true);
        beginEndInputPN.setTemplateOnly(true);
        endInputPN.setTemplateOnly(true);
    }

    public void prepareExampleDoc(Document doc, String label) {
        String headline = label + " " + HEADLINE_LABEL;
        int offset = headline.length() + 1;
        String txt = doc.getContent().substring(offset);

        TextReference headlineGR = new TextReference(
                new Range(0, 2),
                new Range(0, headline.length())
        );

        doc.addToken(phraseModel.getPatternNeuron(), headlineGR);
        doc.addToken(headlineEntity.targetInputPN, headlineGR);

        TextReference textSectionGR = new TextReference(
                new Range(2, 4),
                new Range(offset, doc.length())
        );

        doc.addToken(hintInputPN,
                new TextReference(
                        new Range(2, 3),
                        new Range(offset, offset + txt.indexOf(" "))
                )
        );

        doc.addToken(beginInputPN, textSectionGR);
        doc.addToken(endInputPN, textSectionGR);
        doc.addToken(beginEndInputPN, textSectionGR);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        headlineEntity.write(out);
        out.writeLong(hintBN.getId());
        out.writeLong(innerTsBeginInhibitoryN.getId());
        out.writeLong(innerTsEndInhibitoryN.getId());
        out.writeLong(outerTsBeginInhibitoryN.getId());
        out.writeLong(outerTsEndInhibitoryN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        headlineEntity = new EntityInstance(entityModel);
        headlineEntity.readFields(in, m);

        hintBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        innerTsBeginInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        innerTsEndInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        outerTsBeginInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        outerTsEndInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
