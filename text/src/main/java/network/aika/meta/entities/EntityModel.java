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
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.Synapse;
import network.aika.meta.TemplateModel;
import network.aika.meta.sequences.PhraseModel;
import network.aika.Document;
import network.aika.meta.topics.TopicModel;
import network.aika.text.TextReference;
import network.aika.text.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.Type.*;
import static network.aika.meta.LabelUtil.getAbstractLabel;
import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityModel extends TemplateModel<EntityModel> {

    private static final Logger log = LoggerFactory.getLogger(EntityModel.class);

    public static final double ENTITY_NET_TARGET = 0.7;

    public static final double BINDING_NET_TARGET = 2.5;

    public static final double NEG_MARGIN = 1.1;

    public static final String ENTITY_LABEL = "Entity";


    protected PhraseModel phraseModel;

    protected TopicModel topicModel;

    protected PatternNeuron entityPattern;

    protected BindingNeuron entityBN;

    protected InhibitoryNeuron inhibitoryN;

    public EntityModel(String label, PhraseModel pm, TopicModel tm) {
        this.label = label;
        this.phraseModel = pm;
        this.topicModel = tm;
    }

    public TopicModel getTopicModel() {
        return topicModel;
    }

    @Override
    public void enable() {
        topicModel.enable();

        if(entityBN != null)
            entityBN.setBias(BINDING_NET_TARGET);

        if(entityPattern != null)
            entityPattern.setBias(ENTITY_NET_TARGET);
    }

    @Override
    public void disable() {
        topicModel.disable();

        if(entityBN != null)
            entityBN.setBias(-10.0);

        if(entityPattern != null)
            entityPattern.setBias(-10.0);
    }

    public PatternNeuron getEntityPattern() {
        return entityPattern;
    }

    public BindingNeuron getEntityBN() {
        return entityBN;
    }

    public PhraseModel getPhraseModel() {
        return phraseModel;
    }

    @Override
    public void initTemplateNeurons() {
        entityPattern = new PatternNeuron(getModel())
                .setLabel(getAbstractLabel(PATTERN, label))
                .setTargetNet(ENTITY_NET_TARGET)
                .setBias(ENTITY_NET_TARGET)
                .setPersistent(true);

        entityPattern.makeAbstract()
                .setWeight(getDefaultInputCategorySynapseWeight(entityPattern.getType()))
                .adjustBias()
                .getInput()
                .setPersistent(true);

        addInputObjectSynapse(entityPattern, topicModel.getTopicBindingNeuron(), 10.0);

        entityBN = addBindingNeuron(
                phraseModel.getPatternNeuron(),
                getAbstractLabel(BINDING, label),
                10.0,
                BINDING_NET_TARGET
        );

        entityBN.makeAbstract()
                .setWeight(getDefaultInputCategorySynapseWeight(entityBN.getType()))
                .adjustBias();

        addPositiveFeedbackLoop(
                entityBN,
                entityPattern,
                2.5,
                0.0,
                false,
                false
        );

        inhibitoryN = new InhibitoryNeuron(getModel())
                .setLabel(getAbstractLabel(INHIBITORY, label))
                .setPersistent(true);

        inhibitoryN.makeAbstract()
                .setWeight(1.0);

        addInhibitoryLoop(
                entityBN,
                inhibitoryN,
                NEG_MARGIN * -entityBN.getTargetNet()
        );

        disable();
    }

    @Override
    protected String getLabelPostfix() {
        return " " + ENTITY_LABEL;
    }

    @Override
    public void onInstantiation(Activation tAct, Activation iAct) {
        parent.generateLabel(tAct, iAct, label);
        iAct.getNeuron().makeAbstract()
                .setWeight(getDefaultInputCategorySynapseWeight(tAct.getType()))
                .adjustBias();
    }

    @Override
    public Neuron resolveInstance(Neuron template) {
        return topicModel.resolveInstance(template);
    }

    @Override
    public void mapResults(Document doc) {
        getPhraseModel().getPatternNeuron().setNotInstantiable(false);

        entityPattern = lookupInstance(doc, parent.entityPattern);
        entityPattern.setPersistent(true);

        Synapse s = entityPattern.getOutputSynapse(getTopicModel().getTopicBindingNeuron().getProvider());
        if(s != null)
            s.setNotInstantiable(true);

        entityBN = lookupInstance(doc, parent.entityBN);
        entityBN.setPersistent(true);
    }

    @Override
    public EntityModel createInstanceModel(String label, TemplateModel instM) {
        return new EntityModel(label, phraseModel, (TopicModel) instM);
    }

    public void prepareInstantiation() {
        setNotInstantiable(false);
        phraseModel.getPatternNeuron().setNotInstantiable(true);
    }

    public void prepareExampleDoc(Document doc, String label) {
        Range entityPosRange = new Range(0, 1);
        Range entityCharRange = new Range(0, doc.length());

        doc.addToken(
                phraseModel.getPatternNeuron(),
                new TextReference(entityPosRange, entityCharRange)
        );
    }

    @Override
    public void postProcess(Document doc) {
    }

    public void setNotInstantiable(boolean notInstantiable) {
        entityPattern.setNotInstantiable(notInstantiable, true);
        entityBN.setNotInstantiable(notInstantiable, true);
    }

    public Model getModel() {
        return phraseModel.getModel();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(entityPattern.getId());
        out.writeLong(entityBN.getId());

        out.writeBoolean(inhibitoryN != null);
        if(inhibitoryN != null)
            out.writeLong(inhibitoryN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        entityPattern = m.lookupNeuronProvider(in.readLong()).getNeuron();
        entityBN = m.lookupNeuronProvider(in.readLong()).getNeuron();

        if(in.readBoolean())
            inhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
