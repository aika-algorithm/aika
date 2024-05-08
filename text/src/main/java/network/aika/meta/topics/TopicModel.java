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
package network.aika.meta.topics;

import network.aika.Model;
import network.aika.elements.synapses.types.InputObjectSynapse;
import network.aika.meta.TemplateModel;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.Document;
import network.aika.meta.entities.EntityModel;
import network.aika.text.Range;
import network.aika.text.TextReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.Type.INHIBITORY;
import static network.aika.elements.neurons.RefType.TEMPLATE_MODEL;
import static network.aika.meta.LabelUtil.getAbstractLabel;
import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class TopicModel extends TemplateModel<TopicModel> {

    private static final Logger log = LoggerFactory.getLogger(TopicModel.class);

    public static final String TOPIC_LABEL = "Topic";

    public static final double TOPIC_NET_TARGET = 0.7;

    protected static final double BINDING_NET_TARGET = 2.5;

    public static final double NEG_MARGIN = 1.1;


    protected Model model;

    protected PatternNeuron topicPatternN;

    protected BindingNeuron topicBN;

    protected InhibitoryNeuron inhibitoryN;

    private EntityModel entityModel;

    public TopicModel(String label, Model m) {
        this.label = label;
        model = m;
    }

    public void initModelDependencies(EntityModel em) {
        this.entityModel = em;
    }

    @Override
    public void enable() {
        InputObjectSynapse s = topicBN.getInputSynapseByType(InputObjectSynapse.class);
        if(s != null)
            s.setPropagable(true);
    }

    @Override
    public void disable() {
        InputObjectSynapse s = topicBN.getInputSynapseByType(InputObjectSynapse.class);
        if(s != null)
            s.setPropagable(false);
    }

    @Override
    public Model getModel() {
        return model;
    }

    public BindingNeuron getTopicBindingNeuron() {
        return topicBN;
    }

    public PatternNeuron getTopicPatternNeuron() {
        return topicPatternN;
    }

    @Override
    public void prepareInstantiation() {
        setInstantiable(true);
    }

    @Override
    public void prepareExampleDoc(Document doc, String label) {
        Range entityPosRange = new Range(0, 1);
        Range entityCharRange = new Range(0, doc.length());

        setNotInstantiableForInputEntity(topicBN, false);

        doc.addToken(
                entityModel.getEntityPattern(),
                new TextReference(entityPosRange, entityCharRange)
        );
    }

    @Override
    public void postProcess(Document doc) {
        setNotInstantiableForInputEntity(topicBN, true);
    }

    private void setNotInstantiableForInputEntity(BindingNeuron topicBN, boolean instantiable) {
        InputObjectSynapse ios = topicBN.getInputSynapseByType(InputObjectSynapse.class);
        ios.setInstantiable(instantiable, instantiable);

        entityModel.setInstantiable(instantiable);
    }

    public void setInstantiable(boolean instantiable) {
        topicPatternN.setInstantiable(instantiable);
        topicPatternN.setInputSynapsesInstantiable(instantiable, instantiable);
        topicBN.setInstantiable(instantiable);
        topicBN.setInputSynapsesInstantiable(instantiable, instantiable);
        inhibitoryN.setInstantiable(instantiable);
        inhibitoryN.setInputSynapsesInstantiable(instantiable, instantiable);
    }

    @Override
    public void initTemplateNeurons() {
        topicPatternN = new PatternNeuron(model, TEMPLATE_MODEL)
                .setLabel("Abstract Topic")
                .setTargetNet(TOPIC_NET_TARGET)
                .setBias(TOPIC_NET_TARGET)
                .setPersistent(true)
                .setTypeDescription("Abstract Topic PN");

        topicPatternN.makeAbstract(false)
                .setWeight(getDefaultInputCategorySynapseWeight(topicPatternN.getType()))
                .getInput()
                .setPersistent(true);

        topicBN = addBindingNeuron(
                model,
                "Abstract Topic",
                BINDING_NET_TARGET
        )
                .setTypeDescription("Abstract Entity -> Topic BN");

        topicBN.makeAbstract(false)
                .setWeight(getDefaultInputCategorySynapseWeight(topicBN.getType()))
                .adjustBias()
                .getInput();

        addPositiveFeedbackLoop(
                topicBN,
                topicPatternN,
                2.5,
                0.0,
                false,
                true,
                true
        );

        inhibitoryN = new InhibitoryNeuron(getModel(), TEMPLATE_MODEL)
                .setLabel(getAbstractLabel(INHIBITORY, label))
                .setPersistent(true)
                .setTypeDescription("Abstract Entity -> Topic InhibN");

        inhibitoryN.makeAbstract(false)
                .setWeight(1.0)
                .getInput();

        addInhibitoryLoop(
                topicBN,
                inhibitoryN,
                NEG_MARGIN * -topicBN.getTargetNet(),
                true
        );

        setInstantiable(false);
    }

    @Override
    public void initOuterSynapses() {

    }

    @Override
    protected String getLabelPostfix() {
        return " " + TOPIC_LABEL;
    }

    @Override
    public TopicModel createInstanceModel(String label, TemplateModel instM) {
        return new TopicModel(label, model);
    }

    @Override
    public Neuron resolveInstance(Neuron template, Document doc) {
        if(parent == null)
            return null;

        if(parent.topicPatternN == template)
            return topicPatternN;

        if(parent.topicBN == template)
            return topicBN;

        if(parent.inhibitoryN == template)
            return inhibitoryN;

        return null;
    }

    @Override
    public void mapResults(Document doc) {
        topicPatternN = lookupInstance(doc, parent.topicPatternN);
        topicPatternN.setPersistent(true);

        topicBN = lookupInstance(doc, parent.topicBN);
        topicBN.setPersistent(true);

        inhibitoryN = lookupInstance(doc, parent.inhibitoryN);
        inhibitoryN.setPersistent(true);
        inhibitoryN.setInstantiable(false);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(topicPatternN.getId());
        out.writeLong(topicBN.getId());

        out.writeBoolean(inhibitoryN != null);
        if(inhibitoryN != null)
            out.writeLong(inhibitoryN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        topicPatternN = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
        topicBN = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();

        if(in.readBoolean())
            inhibitoryN = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
    }
}
