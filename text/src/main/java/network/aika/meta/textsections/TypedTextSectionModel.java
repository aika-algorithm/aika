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
import network.aika.elements.neurons.relations.EqualsRelationNeuron;
import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.meta.TargetInput;
import network.aika.meta.entities.EntityInstance;
import network.aika.meta.entities.EntityModel;
import network.aika.text.Document;
import network.aika.text.GroundRef;
import network.aika.text.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.neurons.Neuron.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.meta.LabelUtil.getAbstractBindingNeuronLabel;
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

    protected TargetInput targetInput;


    protected PatternNeuron headlinePattern;

    protected BindingNeuron headlineBN;

    protected PatternNeuron headlineTargetInput;

    protected BindingNeuron headlineTargetInputBN;


    protected BindingNeuron tsHeadlineBN;

    protected BindingNeuron hintBN;

    protected OuterInhibitoryNeuron outerTsBeginInhibitoryN;

    protected OuterInhibitoryNeuron outerTsEndInhibitoryN;

    protected BindingNeuron targetInputBN;

    protected EqualsRelationNeuron relBeginEquals;
    protected EqualsRelationNeuron relEndEquals;

    protected EqualsRelationNeuron relBeginEndEquals;


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
        patternN.setTemplateOnly(templateOnly, true);
        headlineBN.setTemplateOnly(templateOnly, true);
        tsHeadlineBN.setTemplateOnly(templateOnly, true);
        beginBN.setTemplateOnly(templateOnly, true);
        endBN.setTemplateOnly(templateOnly, true);
        beginEndBN.setTemplateOnly(templateOnly, true);
        hintBN.setTemplateOnly(templateOnly, true);
        targetInputBN.setTemplateOnly(templateOnly, true);
    }

    public void initStaticNeurons() {
        super.initStaticNeurons();

        targetInput = new TargetInput(model, TEXT_SECTION_LABEL);
        targetInput.initTargetInput();

        log.info("Typed " + TEXT_SECTION_LABEL);

        double netTarget = 2.5;

        EntityInstance headlineEntity = new EntityInstance(entityModel)
                .instantiate(HEADLINE_LABEL, true);

        headlinePattern = headlineEntity.entityPatternN;

        headlineBN = headlineEntity.entityBN
                .setPersistent(true);

        headlineTargetInput = headlineEntity.targetInputPN
                .setPersistent(true);

        headlineTargetInputBN = headlineEntity.targetInputBN
                .setPersistent(true);

        tsHeadlineBN = addBindingNeuron(
                headlineEntity.entityPatternN,
                "Abstr. " + TEXT_SECTION_LABEL + " " + HEADLINE_LABEL,
                10.0,
                bindingNetTarget
        );
        tsHeadlineBN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

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
                patternN,
                2.5,
                0.0,
                false
        );

        hintBN = new BindingNeuron(model)
                .setLabel(getAbstractBindingNeuronLabel(TEXT_SECTION_LABEL + " Hint"))
                .setTargetNet(bindingNetTarget)
                .setPersistent(true);

        hintBN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addPositiveFeedbackLoop(
                hintBN,
                patternN,
                2.5,
                0.0,
                true
        );

        sectionHintRelations(beginBN, relationPT);
        sectionHintRelations(beginEndBN, relationPT);
        sectionHintRelations(endBN, relationNT);

        createTargetInputBindingNeuron();

        outerTsBeginInhibitoryN = new OuterInhibitoryNeuron(model)
                .setLabel("Outer Begin " + TEXT_SECTION_LABEL)
                .setPersistent(true);

        addOuterInhibitoryLoop(
                beginBN,
                outerTsBeginInhibitoryN,
                NEG_MARGIN_TS * -netTarget
        );

        outerTsEndInhibitoryN = new OuterInhibitoryNeuron(model)
                .setLabel("Outer End " + TEXT_SECTION_LABEL)
                .setPersistent(true);

        addOuterInhibitoryLoop(
                endBN,
                outerTsEndInhibitoryN,
                NEG_MARGIN_TS * -netTarget
        );

        targetInput.setTemplateOnly(true);
    }

    public void prepareInstantiation() {
        setTemplateOnly(false);
        targetInput.setTemplateOnly(false);
        TargetInput.setTemplateOnly(headlineTargetInput, headlineTargetInputBN, false);
        phraseModel.getPatternNeuron().setTemplateOnly(true);
        beginInputPN.setTemplateOnly(true);
        endInputPN.setTemplateOnly(true);
    }

    public void prepareExampleDoc(Document doc, String label) {
        String headline = label + " " + HEADLINE_LABEL;
        String textSection = label + " " + TEXT_SECTION_LABEL;

        GroundRef headlineGR = new GroundRef(
                new Range(0, 2),
                new Range(0, headline.length())
        );

        doc.addToken(phraseModel.getPatternNeuron(), headlineGR);
        doc.addToken(headlineTargetInput, headlineGR);

        GroundRef textSectionGR = new GroundRef(
                new Range(2, 4),
                new Range(headline.length(), doc.length())
        );

        doc.addToken(beginInputPN, textSectionGR);
        doc.addToken(endInputPN, textSectionGR);
        doc.addToken(beginEndInputPN, textSectionGR);

        doc.addToken(targetInput.getTargetInput(), textSectionGR);
    }


    private void createTargetInputBindingNeuron() {
        targetInputBN = targetInput.createTargetInputBindingNeuron(patternN);

        addRelation(
                targetInputBN,
                tsHeadlineBN,
                phraseModel.relNT,
                5.0,
                10.0,
                true
        );

        relBeginEquals = createTargetInputRelation(targetInputBN, beginBN, true, false, "Begin Equals Rel.: ");
        relEndEquals = createTargetInputRelation(targetInputBN, endBN, false, true, "End Equals Rel.: ");
        relBeginEndEquals = createTargetInputRelation(targetInputBN, beginEndBN, true, true, "Equals Rel.: ");
    }

    private EqualsRelationNeuron createTargetInputRelation(BindingNeuron tiBN, BindingNeuron bn, boolean compareBegin, boolean compareEnd, String label) {
        EqualsRelationNeuron rel = new EqualsRelationNeuron(model, compareBegin, compareEnd, label)
                .setBias(5.0)
                .setPersistent(true);

        addRelation(
                tiBN,
                bn,
                rel,
                5.0,
                10.0,
                true
        );
        return rel;
    }

    public PatternNeuron addHeadline(String label) {
        return model.lookupInputNeuron(
                getHeadlineLabel(label) + " " + TARGET_INPUT_LABEL,
                headlineTargetInput
        );
    }

    public void addHeadlineTarget(Document doc, GroundRef groundRef, String label) {
        doc.addToken(
                addHeadline(label),
                groundRef
        );
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

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeLong(headlineBN.getId());
        out.writeLong(headlineTargetInput.getId());
        out.writeLong(hintBN.getId());
        out.writeLong(innerTsBeginInhibitoryN.getId());
        out.writeLong(innerTsEndInhibitoryN.getId());
        out.writeLong(outerTsBeginInhibitoryN.getId());
        out.writeLong(outerTsEndInhibitoryN.getId());
        out.writeLong(targetInputBN.getId());
        out.writeLong(relBeginEquals.getId());
        out.writeLong(relEndEquals.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        headlineBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        headlineTargetInput = m.lookupNeuronProvider(in.readLong()).getNeuron();
        hintBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        innerTsBeginInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        innerTsEndInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        outerTsBeginInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        outerTsEndInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relBeginEquals = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relEndEquals = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
