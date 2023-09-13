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


    protected EntityModel entityModel;

    protected TargetInput targetInput;


    protected BindingNeuron headlineBN;

    protected PatternNeuron headlineTargetInput;


    protected BindingNeuron tsHeadlineBN;

    protected BindingNeuron textSectionHintBN;

    protected InnerInhibitoryNeuron tsBeginInhibitoryN;

    protected InnerInhibitoryNeuron tsEndInhibitoryN;

    protected OuterInhibitoryNeuron tsInhibitoryN;

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

    private void generateLabel(Activation act, String label) {
        act.getNeuron().setLabel(
                act.getTemplate().getLabel().replace("Headline", label)
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

        String headline = label + " Headline";
        String textSection = label + " Text-Section";

        Document doc = new Document(getModel(), headline + textSection);

        AIKADebugger.createAndShowGUI(doc);
        doc.setInstantiationCallback(act -> {
            generateLabel(act, label);

            if(isPartOfHeadline(act) || isHint(act)) {
                ConjunctiveSynapse s = (ConjunctiveSynapse) act.getNeuron().makeAbstract();

                if (targetInput.getTargetInput() == act.getNeuron().getTemplate()) {
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

    private boolean isPartOfHeadline(Activation act) {
        String l = act.getTemplate().getLabel();
        return l.contains("Headline") && !l.contains("Text-Section");
    }

    private boolean isHint(Activation act) {
        return act.getTemplate().getLabel().contains("Hint");
    }

    public void initStaticNeurons() {
        super.initStaticNeurons();

        targetInput = new TargetInput(model, "Text Section");
        targetInput.initTargetInput();

        log.info("Typed Text-Section");

        textSectionHintBN = new BindingNeuron(model)
                .setLabel("Abstr. Text-Section-Hint")
                .setPersistent(true);

        double netTarget = 2.5;

        EntityModel.EntityInstance headlineEntity = entityModel.addEntityPattern("Headline", true);

        headlineBN = headlineEntity.entityBN()
                .setPersistent(true);

        headlineTargetInput = headlineEntity.targetInputPN()
                .setPersistent(true);

        tsHeadlineBN = addBindingNeuron(
                headlineEntity.entityPatternN(),
                "Abstr. Text-Section Headline",
                10.0,
                bindingNetTarget
        );

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
                .setLabel("Abstract Text-Section Hint")
                .setTargetNet(bindingNetTarget)
                .setPersistent(true);

        addPositiveFeedbackLoop(
                textSectionHintBN,
                patternN,
                2.5,
                0.0,
                false
        );

        sectionHintRelations(beginBN, relationPT);
        sectionHintRelations(endBN, relationNT);

        createTargetInputBindingNeuron();

        tsBeginInhibitoryN = new InnerInhibitoryNeuron(model)
                .setLabel("I TS Begin")
                .setPersistent(true);

        addInnerInhibitoryLoop(
                beginBN,
                tsBeginInhibitoryN,
                NEG_MARGIN_TS_BEGIN * -netTarget
        );

        tsEndInhibitoryN = new InnerInhibitoryNeuron(model)
                .setLabel("I TS End")
                .setPersistent(true);

        addInnerInhibitoryLoop(
                endBN,
                tsEndInhibitoryN,
                NEG_MARGIN_TS_END * -netTarget
        );

        tsInhibitoryN = new OuterInhibitoryNeuron(model)
                .setLabel("I TS")
                .setPersistent(true);

        addOuterInhibitoryLoop(
                textSectionHintBN,
                tsInhibitoryN,
                NEG_MARGIN_TS * -netTarget
        );

        addOuterInhibitoryLoop(
                beginBN,
                tsInhibitoryN,
                NEG_MARGIN_TS * -netTarget
        );

        addOuterInhibitoryLoop(
                endBN,
                tsInhibitoryN,
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
        out.writeLong(tsBeginInhibitoryN.getId());
        out.writeLong(tsEndInhibitoryN.getId());
        out.writeLong(tsInhibitoryN.getId());
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
        tsBeginInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        tsEndInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        tsInhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relBeginEquals = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relEndEquals = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
