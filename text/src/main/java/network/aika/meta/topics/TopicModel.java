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
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.neurons.relations.EqualsRelationNeuron;
import network.aika.enums.Scope;
import network.aika.meta.TargetInput;
import network.aika.meta.entities.EntityModel;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static network.aika.meta.NetworkMotifs.*;
import static network.aika.meta.entities.EntityModel.ENTITY_NET_TARGET;
import static network.aika.utils.NetworkUtils.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.utils.NetworkUtils.makeAbstract;

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

    protected NeuronProvider topicPatternN;

    protected NeuronProvider topicPatternCategory;

    protected NeuronProvider relEquals;

    protected NeuronProvider topicBN;

    protected NeuronProvider targetInputBN;


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

        topicPatternN = new PatternNeuron()
                .init(model, "Abstract Topic")
                .getProvider(true);

        topicPatternN.getNeuron()
                .setBias(TOPIC_NET_TARGET);

        topicPatternCategory = makeAbstract((PatternNeuron) topicPatternN.getNeuron())
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .getPInput();


        topicBN = addBindingNeuron(
                entityModel.getEntityPattern().getNeuron(),
                Scope.SAME,
                "Abstract Entity",
                10.0,
                ENTITY_NET_TARGET,
                BINDING_NET_TARGET
        ).getProvider(true);

        makeAbstract((BindingNeuron) topicBN.getNeuron())
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addPositiveFeedbackLoop(
                topicBN.getNeuron(),
                topicPatternN.getNeuron(),
                ENTITY_NET_TARGET,
                BINDING_NET_TARGET,
                2.5,
                0.0,
                false
        );

        targetInputBN = createTargetInputBindingNeuron()
                .getProvider(true);

        targetInput.setTemplateOnly(true);
    }

    protected BindingNeuron createTargetInputBindingNeuron() {
        BindingNeuron bn = targetInput.createTargetInputBindingNeuron(
                topicPatternN.getNeuron(),
                TOPIC_NET_TARGET
        );

        relEquals = EqualsRelationNeuron.createEqualsRelationNeuron(model, "Equals Rel.: ")
                .setBias(5.0)
                .getProvider(true);

        addRelation(
                bn,
                topicBN.getNeuron(),
                relEquals.getNeuron(),
                5.0,
                10.0,
                true
        );

        return bn;
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
        topicPatternN = m.lookupNeuronProvider(in.readLong());
        topicPatternCategory = m.lookupNeuronProvider(in.readLong());
        relEquals = m.lookupNeuronProvider(in.readLong());
        topicBN = m.lookupNeuronProvider(in.readLong());
        targetInputBN = m.lookupNeuronProvider(in.readLong());
    }
}
