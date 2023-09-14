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
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.EqualsRelationNeuron;
import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.meta.TargetInput;
import network.aika.meta.entities.EntityModel;
import network.aika.meta.exceptions.FailedInstantiationException;
import network.aika.text.Document;
import network.aika.text.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.InstantiationUtil.lookupInstance;
import static network.aika.elements.neurons.Neuron.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.meta.Dictionary.INPUT_TOKEN_NET_TARGET;
import static network.aika.meta.NetworkMotifs.*;
import static network.aika.queue.Phase.ANNEAL;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.queue.keys.QueueKey.MAX_ROUND;

/**
 *
 * @author Lukas Molzberger
 */
public class TypedTextSectionModel extends TextSectionModel {

    private static final Logger log = LoggerFactory.getLogger(TypedTextSectionModel.class);

    public static final String HEADLINE_LABEL = "Headline";


    protected EntityModel entityModel;

    protected TargetInput targetInput;


    protected BindingNeuron headlineBN;

    protected PatternNeuron headlineTargetInput;


    protected BindingNeuron tsHeadlineBN;

    protected BindingNeuron textSectionHintBN;

    protected InnerInhibitoryNeuron innerTsBeginInhibitoryN;

    protected InnerInhibitoryNeuron innerTsEndInhibitoryN;

    protected OuterInhibitoryNeuron outerTsBeginInhibitoryN;

    protected OuterInhibitoryNeuron outerTsEndInhibitoryN;

    protected BindingNeuron targetInputBN;

    protected EqualsRelationNeuron relBeginEquals;
    protected EqualsRelationNeuron relEndEquals;

    public TypedTextSectionModel(EntityModel entityModel) {
        super(entityModel.getPhraseModel());
        this.entityModel = entityModel;
    }

    public Model getModel() {
        return phraseModel.getModel();
    }

    private void generateLabel(Activation tAct, Activation iAct, String label) {
        iAct.getNeuron().setLabel(
                tAct.getLabel()
                        .replace(HEADLINE_LABEL, label + "-HL")
                        .replace(TEXT_SECTION_LABEL, label + "-TS")
        );
    }

    public PatternNeuron addTextSectionType(String label) {
        targetInput.setTemplateOnly(false);
        phraseModel.getPatternNeuron().setTemplateOnly(true);
        beginInputPN.setTemplateOnly(true);
        endInputPN.setTemplateOnly(true);

        getModel()
                .getConfig()
                .setTrainingEnabled(true)
                .setMetaInstantiationEnabled(true);

        String headline = label + " " + HEADLINE_LABEL;
        String textSection = label + " " + TEXT_SECTION_LABEL;

        Document doc = new Document(getModel(), headline + textSection);

        AIKADebugger.createAndShowGUI(doc);
        doc.setInstantiationCallback((tAct, iAct) -> {
            generateLabel(tAct, iAct, label);

            if(isPartOfHeadline(tAct) || isHint(tAct)) {
                ConjunctiveSynapse s = (ConjunctiveSynapse) iAct.getNeuron().makeAbstract();

                if (targetInput.getTargetInput() == tAct.getNeuron()) {
                    s.setWeight(2.0);
                    s.adjustBias();
                } else
                    s.setWeight(PASSIVE_SYNAPSE_WEIGHT);
            }
        });

        try {
            doc.setFeedbackTriggerRound();

            Range headlinePosRange = new Range(0, 2);
            Range headlineCharRange = new Range(0, headline.length());

            doc.addToken(phraseModel.getPatternNeuron(), headlinePosRange, headlineCharRange);
            doc.addToken(headlineTargetInput, headlinePosRange, headlineCharRange);

            Range textSectionPosRange = new Range(2, 4);
            Range textSectionCharRange = new Range(headline.length(), doc.length());

            doc.addToken(beginInputPN, textSectionPosRange, textSectionCharRange);
            doc.addToken(endInputPN, textSectionPosRange, textSectionCharRange);
            doc.addToken(targetInput.getTargetInput(), textSectionPosRange, textSectionCharRange);

            doc.process(MAX_ROUND, INFERENCE);
            doc.anneal();
            doc.process(MAX_ROUND, ANNEAL);
            doc.instantiateTemplates();

            targetInput.setTemplateOnly(true);
            phraseModel.getPatternNeuron().setTemplateOnly(false);

            return lookupInstance(doc, targetInput.getTargetInput());
        } catch(Exception e) {
            throw new FailedInstantiationException("entity", e);
        } finally {
            doc.disconnect();
        }
    }

    private boolean isPartOfHeadline(Activation tAct) {
        String l = tAct.getLabel();
        return l.contains(HEADLINE_LABEL) && !l.contains(TEXT_SECTION_LABEL);
    }

    private boolean isHint(Activation tAct) {
        return tAct.getLabel().contains("Hint");
    }

    public void initStaticNeurons() {
        super.initStaticNeurons();

        targetInput = new TargetInput(model, TEXT_SECTION_LABEL);
        targetInput.initTargetInput();

        log.info("Typed " + TEXT_SECTION_LABEL);

        double netTarget = 2.5;

        EntityModel.EntityInstance headlineEntity = entityModel.addEntityPattern(HEADLINE_LABEL, true);

        headlineBN = headlineEntity.entityBN()
                .setPersistent(true);

        headlineTargetInput = headlineEntity.targetInputPN()
                .setPersistent(true);

        tsHeadlineBN = addBindingNeuron(
                headlineEntity.entityPatternN(),
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

        textSectionHintBN = new BindingNeuron(model)
                .setLabel("Abstract " + TEXT_SECTION_LABEL + " Hint")
                .setTargetNet(bindingNetTarget)
                .setPersistent(true);

        textSectionHintBN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addPositiveFeedbackLoop(
                textSectionHintBN,
                patternN,
                2.5,
                0.0,
                true
        );

        sectionHintRelations(beginBN, relationPT);
        sectionHintRelations(endBN, relationNT);

        createTargetInputBindingNeuron();

        innerTsBeginInhibitoryN = new InnerInhibitoryNeuron(model)
                .setLabel("Inner " + TEXT_SECTION_LABEL + " Begin")
                .setPersistent(true);

        innerTsBeginInhibitoryN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addInnerInhibitoryLoop(
                beginBN,
                innerTsBeginInhibitoryN,
                NEG_MARGIN_TS_BEGIN * -netTarget
        );

        innerTsEndInhibitoryN = new InnerInhibitoryNeuron(model)
                .setLabel("Inner " + TEXT_SECTION_LABEL + " End")
                .setPersistent(true);

        innerTsEndInhibitoryN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addInnerInhibitoryLoop(
                endBN,
                innerTsEndInhibitoryN,
                NEG_MARGIN_TS_END * -netTarget
        );

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

        relBeginEquals = new EqualsRelationNeuron(model, true, true, "Equals Rel.: ")
                .setBias(5.0)
                .setPersistent(true);

        addRelation(
                targetInputBN,
                beginBN,
                relBeginEquals,
                5.0,
                10.0,
                true
        );

        relEndEquals = new EqualsRelationNeuron(model, true, true, "Equals Rel.: ")
                .setBias(5.0)
                .setPersistent(true);

        addRelation(
                targetInputBN,
                endBN,
                relEndEquals,
                5.0,
                10.0,
                true
        );
    }

    public PatternNeuron addHeadline(String headlineLabel) {
        return model.lookupInputNeuron(headlineLabel, headlineTargetInput);
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
                textSectionHintBN,
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
        textSectionHintBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        innerTsBeginInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        innerTsEndInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        outerTsBeginInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        outerTsEndInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relBeginEquals = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relEndEquals = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
