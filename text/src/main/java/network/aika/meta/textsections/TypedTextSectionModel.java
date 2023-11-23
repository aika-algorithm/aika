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
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.neurons.types.LatentRelationNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
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

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.PATTERN;
import static network.aika.meta.LabelUtil.*;
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

    protected BindingNeuron hintBN;

    protected PatternNeuron hintInputPN;

    protected InhibitoryNeuron tsBeginInhibitoryN;

    protected InhibitoryNeuron tsEndInhibitoryN;


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
                .setWeight(getDefaultInputCategorySynapseWeight(tsHeadlineBN.getType()))
                .adjustBias();

        addRelation(
                tsHeadlineBN,
                beginBN,
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
                getAbstractLabel(BINDING, TEXT_SECTION_LABEL + " Hint"),
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
        tsBeginInhibitoryN = new InhibitoryNeuron(model)
                .setLabel("Begin " + TEXT_SECTION_LABEL)
                .setPersistent(true);

        addInhibitoryLoop(
                beginBN,
                tsBeginInhibitoryN,
                NEG_MARGIN_TS * -beginBN.getTargetNet()
        );

        tsEndInhibitoryN = new InhibitoryNeuron(model)
                .setLabel("End " + TEXT_SECTION_LABEL)
                .setPersistent(true);

        addInhibitoryLoop(
                endBN,
                tsEndInhibitoryN,
                NEG_MARGIN_TS * -endBN.getTargetNet()
        );
    }

    public PatternNeuron getHeadlinePattern(String tsType) {
        return getModel().getInputNeuron(
                getAbstractLabel(
                        PATTERN,
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
                getAbstractLabel(
                        PATTERN,
                        getTextSectionLabel(tsType)
                ),
                textSectionPatternN
        );
    }

    public void prepareInstantiation() {
        setTemplateOnly(false);
        phraseModel.getPatternNeuron().setTemplateOnly(true);
        beginInputPN.setTemplateOnly(true);
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
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        headlineEntity.write(out);

        out.writeLong(tsHeadlineBN.getId());
        out.writeLong(hintBN.getId());
        out.writeLong(hintInputPN.getId());
        out.writeLong(tsBeginInhibitoryN.getId());
        out.writeLong(tsEndInhibitoryN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        headlineEntity = new EntityInstance(entityModel);
        headlineEntity.readFields(in, m);

        tsHeadlineBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        hintBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        hintInputPN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        tsBeginInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        tsEndInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
