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
package network.aika.meta.entities;

import network.aika.Model;
import network.aika.TemplateModel;
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.ContainsRelation;
import network.aika.enums.direction.Direction;
import network.aika.meta.TargetInput;
import network.aika.meta.sequences.PhraseModel;
import network.aika.Document;
import network.aika.text.TextReference;
import network.aika.text.Range;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.meta.LabelUtil.getAbstractBindingNeuronLabel;
import static network.aika.meta.LabelUtil.getAbstractPatternLabel;
import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityModel implements TemplateModel, Writable {

    private static final Logger log = LoggerFactory.getLogger(EntityModel.class);

    public static final double ENTITY_NET_TARGET = 0.7;

    protected static final double BINDING_NET_TARGET = 2.5;

    public static final double NEG_MARGIN = 1.1;

    public static final String ENTITY_LABEL = "Entity";


    protected Model model;
    protected PhraseModel phraseModel;

    protected TargetInput targetInput;

    protected CategoryNeuron entityCategory;

    protected PatternNeuron entityPattern;

    protected BindingNeuron entityBN;

    protected InhibitoryNeuron outerInhibitoryN;

    protected BindingNeuron targetInputBN;

    protected LatentRelationNeuron targetInputRelation;


    public EntityModel(PhraseModel pm) {
        this.model = pm.getModel();
        this.phraseModel = pm;
    }

    public PatternNeuron getEntityPattern() {
        return entityPattern;
    }

    public PhraseModel getPhraseModel() {
        return phraseModel;
    }

    public void initStaticNeurons() {
        targetInput = new TargetInput(model, ENTITY_LABEL);
        targetInput.initTargetInput();

        entityPattern = new PatternNeuron(model)
                .setLabel(getAbstractPatternLabel(ENTITY_LABEL))
                .setTargetNet(ENTITY_NET_TARGET)
                .setBias(ENTITY_NET_TARGET)
                .setPersistent(true);

        entityCategory = entityPattern.makeAbstract()
                .setWeight(DEFAULT_INPUT_CATEGORY_SYNAPSE_WEIGHT)
                .adjustBias()
                .getInput()
                .setPersistent(true);

        entityBN = addBindingNeuron(
                phraseModel.getPatternNeuron(),
                getAbstractBindingNeuronLabel(ENTITY_LABEL),
                10.0,
                BINDING_NET_TARGET
        );

        entityBN.makeAbstract()
                .setWeight(DEFAULT_INPUT_CATEGORY_SYNAPSE_WEIGHT)
                .adjustBias();

        addPositiveFeedbackLoop(
                entityBN,
                entityPattern,
                2.5,
                0.0,
                false,
                false
        );

        outerInhibitoryN = new InhibitoryNeuron(model)
                .setLabel("I")
                .setPersistent(true);

        outerInhibitoryN.makeAbstract()
                .setWeight(1.0);

        addOuterInhibitoryLoop(
                entityBN,
                outerInhibitoryN,
                NEG_MARGIN * -entityBN.getTargetNet()
        );

        targetInputRelation = new LatentRelationNeuron(
                model,
                new ContainsRelation(Direction.OUTPUT)
        )
                .setBias(5.0)
                .setLabel("Contains Rel.: ")
                .setPersistent(true);

        targetInputBN = targetInput.createTargetInputBindingNeuron(entityBN, entityPattern, targetInputRelation);

        targetInput.setTemplateOnly(true);
    }

    public void prepareInstantiation() {
        setTemplateOnly(false);
        targetInput.setTemplateOnly(false);
        phraseModel.getPatternNeuron().setTemplateOnly(true);
    }

    public void prepareExampleDoc(Document doc, String label) {
        Range entityPosRange = new Range(0, 1);
        Range entityCharRange = new Range(0, doc.length());

        doc.addToken(phraseModel.getPatternNeuron(), new TextReference(entityPosRange, entityCharRange));
        doc.addToken(targetInput.getTargetInput(), new TextReference(entityPosRange, entityCharRange));
    }

    public PatternNeuron getInstancePattern(String entityType) {
        return getModel().getInputNeuron(entityType, entityPattern);
    }

    public void setTemplateOnly(boolean templateOnly) {
        entityPattern.setTemplateOnly(templateOnly, true);
        entityBN.setTemplateOnly(templateOnly, true);
        targetInputBN.setTemplateOnly(templateOnly, true);
    }

    public PatternNeuron addEntity(String entityLabel) {
        return model.lookupInputNeuron(entityLabel, targetInput.getTargetInput());
    }

    public void addEntityTarget(Document doc, TextReference textReference, String entityLabel) {
        doc.addToken(
                addEntity(entityLabel),
                textReference
        );
    }

    public Model getModel() {
        return phraseModel.getModel();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(entityCategory.getId());
        out.writeLong(entityPattern.getId());
        out.writeLong(entityBN.getId());
        out.writeLong(targetInputBN.getId());
        out.writeLong(targetInputRelation.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        entityCategory = m.lookupNeuronProvider(in.readLong()).getNeuron();
        entityPattern = m.lookupNeuronProvider(in.readLong()).getNeuron();
        entityBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputRelation = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
