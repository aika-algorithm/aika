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

import network.aika.Document;
import network.aika.InstantiationModel;
import network.aika.Model;
import network.aika.TemplateModel;
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.meta.entities.EntityModel;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.meta.NetworkMotifs.getDefaultInputCategorySynapseWeight;
import static network.aika.meta.entities.EntityModel.ENTITY_LABEL;
import static network.aika.meta.topics.TopicModel.TOPIC_LABEL;

/**
 *
 * @author Lukas Molzberger
 */
public class TopicInstance extends InstantiationModel<TopicInstance> implements Writable {

    public PatternNeuron topicPatternN;
    public BindingNeuron topicBN;

    TopicModel topicModel;

    public TopicInstance(TopicModel topicModel) {
        this.topicModel = topicModel;
    }

    public TemplateModel getTemplateModel() {
        return this.topicModel;
    }

    @Override
    protected Document createDocument(String label) {
        Document doc = new Document(getModel(), label);

        boolean flag = false;

//        if(flag)
            AIKADebugger.createAndShowGUI()
                    .setDocument(doc);

        doc.setInstantiationCallback((tAct, iAct) -> {
            generateLabel(tAct, iAct, label);
            iAct.getNeuron().makeAbstract()
                    .setWeight(getDefaultInputCategorySynapseWeight(tAct.getType()))
                    .adjustBias();
        });

        return doc;
    }

    private void generateLabel(Activation tAct, Activation iAct, String label) {
        iAct.getNeuron().setLabel(
                tAct.getLabel()
                        .replace(TOPIC_LABEL, label + " " + TOPIC_LABEL)
                        .replace(ENTITY_LABEL, label + " " + ENTITY_LABEL)
        );
    }

    public EntityModel getEntityModel() {
        return topicModel.getEntityModel();
    }


    public void setNotInstantiable(boolean notInstantiable) {
        topicBN.setNotInstantiable(notInstantiable, true);
    }

    public PatternNeuron getTopicPatternNeuron() {
        return topicPatternN;
    }

    public BindingNeuron getTopicBindingNeuron() {
        return topicBN;
    }

    @Override
    protected void mapResults(Document doc) {
        getEntityModel().getEntityPattern().setNotInstantiable(false);

        topicPatternN = lookupInstance(doc, topicModel.topicPatternN);
        topicPatternN.setPersistent(true);

        topicBN = lookupInstance(doc, topicModel.topicBN);
        topicBN.setPersistent(true);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(topicPatternN.getId());
        out.writeLong(topicBN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        topicPatternN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        topicBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
