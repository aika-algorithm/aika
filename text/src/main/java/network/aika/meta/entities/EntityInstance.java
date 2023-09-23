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

import network.aika.InstantiationUtil;
import network.aika.Model;
import network.aika.TemplateModel;
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.synapses.Synapse;
import network.aika.meta.TargetInput;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.neurons.Neuron.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.meta.entities.EntityModel.ENTITY_LABEL;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityInstance extends InstantiationUtil<EntityInstance> implements Writable {

    public PatternNeuron entityPatternN;
    public BindingNeuron entityBN;
    public BindingNeuron targetInputBN;
    public PatternNeuron targetInputPN;

    EntityModel entityModel;

    public EntityInstance(EntityModel entityModel) {
        this.entityModel = entityModel;
    }

    public TemplateModel getTemplateModel() {
        return this.entityModel;
    }

    public PhraseModel getPhraseModel() {
        return entityModel.getPhraseModel();
    }

    public TargetInput getTargetInput() {
        return entityModel.targetInput;
    }

    @Override
    protected Document createDocument(String label) {
        Document doc = new Document(getModel(), label);

//        AIKADebugger.createAndShowGUI()
//               .setDocument(doc);

        doc.setInstantiationCallback((tAct, iAct) -> {
            generateLabel(iAct, label);
            Synapse s = (Synapse) iAct.getNeuron().makeAbstract();
            s.setWeight(PASSIVE_SYNAPSE_WEIGHT);
        });

        return doc;
    }

    private void generateLabel(Activation act, String label) {
        act.getNeuron().setLabel(
                act.getTemplate().getLabel().replace(ENTITY_LABEL, label)
        );
    }

    public void setTemplateOnly(boolean templateOnly) {
        entityBN.setTemplateOnly(templateOnly, true);
    }

    @Override
    protected void mapResults(Document doc) {
        getPhraseModel().getPatternNeuron().setTemplateOnly(false);

        entityPatternN = lookupInstance(doc, entityModel.entityPattern);
        entityBN = lookupInstance(doc, entityModel.entityBN);
        entityBN.setPersistent(true);

        targetInputBN = lookupInstance(doc, entityModel.targetInputBN);
        targetInputPN = lookupInstance(doc, entityModel.targetInput.getTargetInput());
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(entityPatternN.getId());
        out.writeLong(entityBN.getId());
        out.writeLong(targetInputBN.getId());
        out.writeLong(targetInputPN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        entityPatternN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        entityBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputPN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
