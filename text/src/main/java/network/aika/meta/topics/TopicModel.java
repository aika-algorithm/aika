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
import network.aika.TemplateModel;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.meta.entities.EntityModel;
import network.aika.Document;
import network.aika.text.Range;
import network.aika.text.TextReference;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static network.aika.elements.Type.INHIBITORY;
import static network.aika.meta.LabelUtil.getAbstractLabel;
import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class TopicModel implements TemplateModel, Writable {

    private static final Logger log = LoggerFactory.getLogger(TopicModel.class);

    public static final String TOPIC_LABEL = "Topic";

    public static final double TOPIC_NET_TARGET = 0.7;

    protected static final double BINDING_NET_TARGET = 2.5;

    public static final double NEG_MARGIN = 1.1;

    protected EntityModel entityModel;

    protected Model model;

    protected PatternNeuron topicPatternN;

    protected CategoryNeuron topicPatternCategory;

    protected BindingNeuron topicBN;

    protected BindingNeuron targetInputBN;

    protected InhibitoryNeuron inhibitoryN;


    public TopicModel(EntityModel entityModel) {
        this.entityModel = entityModel;
        model = entityModel.getModel();
    }

    public EntityModel getEntityModel() {
        return entityModel;
    }

    public void addTargetTopics(Document doc, Set<String> tsLabels) {
        log.info(doc.getContent() + " : " + tsLabels.stream().collect(Collectors.joining(", ")));
    }

    @Override
    public boolean stepFilter(Neuron n) {
        return n == topicBN ||
                n == topicPatternN ||
                n == entityModel.getEntityPattern() ||
                n == entityModel.getEntityBN();
    }

    @Override
    public Model getModel() {
        return entityModel.getModel();
    }

    @Override
    public void prepareInstantiation() {
        setNotInstantiable(false);
        entityModel.getEntityPattern().setNotInstantiable(false);
        entityModel.getPhraseModel().getPatternNeuron().setNotInstantiable(true);
    }

    @Override
    public void prepareExampleDoc(Document doc, String label) {
        Range entityPosRange = new Range(0, 1);
        Range entityCharRange = new Range(0, doc.length());

        doc.addToken(
                entityModel.getPhraseModel().getPatternNeuron(),
                new TextReference(entityPosRange, entityCharRange)
        );
    }

    public void setNotInstantiable(boolean notInstantiable) {
        topicPatternN.setNotInstantiable(notInstantiable, true);
        topicBN.setNotInstantiable(notInstantiable, true);
    }

    @Override
    public void initStaticNeurons() {
        topicPatternN = new PatternNeuron(model)
                .setLabel("Abstract Topic")
                .setBias(TOPIC_NET_TARGET)
                .setPersistent(true);

        topicPatternCategory = topicPatternN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .getInput()
                .setPersistent(true);

        topicBN = addBindingNeuron(
                entityModel.getEntityPattern(),
                "Abstract Topic",
                10.0,
                BINDING_NET_TARGET
        );

        topicBN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addPositiveFeedbackLoop(
                topicBN,
                topicPatternN,
                2.5,
                0.0,
                false,
                true
        );

        inhibitoryN = new InhibitoryNeuron(getModel())
                .setLabel(getAbstractLabel(INHIBITORY, TOPIC_LABEL))
                .setPersistent(true);

        inhibitoryN.makeAbstract()
                .setWeight(1.0);

        addInhibitoryLoop(
                topicBN,
                inhibitoryN,
                NEG_MARGIN * -topicBN.getTargetNet()
        );
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(topicPatternN.getId());
        out.writeLong(topicPatternCategory.getId());
        out.writeLong(topicBN.getId());
        out.writeLong(targetInputBN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        topicPatternN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        topicPatternCategory = m.lookupNeuronProvider(in.readLong()).getNeuron();
        topicBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
