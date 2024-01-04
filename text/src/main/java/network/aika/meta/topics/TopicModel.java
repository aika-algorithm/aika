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
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.meta.TemplateModel;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.meta.entities.EntityModel;
import network.aika.Document;
import network.aika.text.Range;
import network.aika.text.TextReference;
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
import static network.aika.meta.entities.EntityModel.ENTITY_LABEL;

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


    public TopicModel(String label, Model m) {
        this.label = label;
        model = m;
    }

    @Override
    public void enable() {
        if(topicBN != null)
            topicBN.setBias(BINDING_NET_TARGET);
    }

    @Override
    public void disable() {
        if(topicBN != null)
            topicBN.setBias(-10.0);
    }

    @Override
    public Model getModel() {
        return model;
    }

    public BindingNeuron getTopicBindingNeuron() {
        return topicBN;
    }

    @Override
    public void prepareInstantiation() {
        setNotInstantiable(false);
    }

    @Override
    public void prepareExampleDoc(Document doc, String label) {
        Range entityPosRange = new Range(0, 1);
        Range entityCharRange = new Range(0, doc.length());

        doc.addBindingActivation(
                topicBN,
                new TextReference(entityPosRange, entityCharRange)
        );
    }

    public void setNotInstantiable(boolean notInstantiable) {
        topicPatternN.setNotInstantiable(notInstantiable, true);
        topicBN.setNotInstantiable(notInstantiable, true);
    }

    @Override
    public void initTemplateNeurons() {
        topicPatternN = new PatternNeuron(model)
                .setLabel("Abstract Topic")
                .setBias(TOPIC_NET_TARGET)
                .setPersistent(true);

        topicPatternN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .getInput()
                .setPersistent(true);

        topicBN = addBindingNeuron(
                model,
                "Abstract Topic",
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
                .setLabel(getAbstractLabel(INHIBITORY, label))
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
    protected String getLabelPostfix() {
        return " " + TOPIC_LABEL;
    }


    @Override
    public TopicModel createInstanceModel(String label, TemplateModel instM) {
        return new TopicModel(label, model);
    }

    @Override
    public Neuron resolveInstance(Neuron template) {
        if(parent == null)
            return null;

        if(parent.topicPatternN == template)
            return topicPatternN;

        if(parent.topicBN == template)
            return topicBN;

        return null;
    }

    @Override
    public void mapResults(Document doc) {
        topicPatternN = lookupInstance(doc, parent.topicPatternN);
        topicPatternN.setPersistent(true);

        topicBN = lookupInstance(doc, parent.topicBN);
        topicBN.setPersistent(true);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(topicPatternN.getId());
        out.writeLong(topicBN.getId());
        out.writeLong(inhibitoryN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        topicPatternN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        topicBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        inhibitoryN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
