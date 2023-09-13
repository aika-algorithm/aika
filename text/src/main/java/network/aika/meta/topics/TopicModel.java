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
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.neurons.relations.EqualsRelationNeuron;
import network.aika.meta.TargetInput;
import network.aika.meta.entities.EntityModel;
import network.aika.text.Document;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static network.aika.elements.neurons.Neuron.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class TopicModel implements Writable {

    private static final Logger log = LoggerFactory.getLogger(TopicModel.class);

    public static final double TOPIC_NET_TARGET = 0.7;

    protected static final double BINDING_NET_TARGET = 2.5;

    protected EntityModel entityModel;

    protected TargetInput targetInput;

    protected Model model;

    protected PatternNeuron topicPatternN;

    protected CategoryNeuron topicPatternCategory;

    protected EqualsRelationNeuron relEquals;

    protected BindingNeuron topicBN;

    protected BindingNeuron targetInputBN;


    public TopicModel(EntityModel entityModel) {
        this.entityModel = entityModel;
        model = entityModel.getModel();
    }

    public void addTargetTopics(Document doc, Set<String> tsLabels) {
        log.info(doc.getContent() + " : " + tsLabels.stream().collect(Collectors.joining(", ")));
    }

    public void initStaticNeurons() {
        targetInput = new TargetInput(model, "Topic");
        targetInput.initTargetInput();

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
                "Abstract Entity",
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
                false
        );

        relEquals = new EqualsRelationNeuron(model, true, true, "Equals Rel.: ")
                .setBias(5.0);
        targetInputBN = targetInput.createTargetInputBindingNeuron(topicBN, topicPatternN, relEquals);

        targetInput.setTemplateOnly(true);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(topicPatternN.getId());
        out.writeLong(topicPatternCategory.getId());
        out.writeLong(relEquals.getId());
        out.writeLong(topicBN.getId());
        out.writeLong(targetInputBN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        topicPatternN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        topicPatternCategory = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relEquals = m.lookupNeuronProvider(in.readLong()).getNeuron();
        topicBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
