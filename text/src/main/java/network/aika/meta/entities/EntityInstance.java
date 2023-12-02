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

import network.aika.InstantiationModel;
import network.aika.Model;
import network.aika.TemplateModel;
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.meta.sequences.PhraseModel;
import network.aika.Document;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.meta.NetworkMotifs.getDefaultInputCategorySynapseWeight;
import static network.aika.meta.entities.EntityModel.BINDING_NET_TARGET;
import static network.aika.meta.entities.EntityModel.ENTITY_LABEL;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityInstance extends InstantiationModel<EntityInstance> implements Writable {

    public PatternNeuron entityPatternN;
    public BindingNeuron entityBN;

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

    @Override
    protected Document createDocument(String label) {
        Document doc = new Document(getModel(), label);

        boolean flag = false;

        if(flag)
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
                tAct.getLabel().replace(ENTITY_LABEL, label)
        );
    }

    public void setTemplateOnly(boolean templateOnly) {
        entityBN.setTemplateOnly(templateOnly, true);
    }

    @Override
    public void enable() {
        entityBN.setBias(BINDING_NET_TARGET);
    }

    @Override
    public void disable() {
        entityBN.setBias(-10.0);
    }

    public PatternNeuron getEntityPatternNeuron() {
        return entityPatternN;
    }

    public BindingNeuron getEntityBindingNeuron() {
        return entityBN;
    }

    @Override
    protected void mapResults(Document doc) {
        getPhraseModel().getPatternNeuron().setTemplateOnly(false);

        entityPatternN = lookupInstance(doc, entityModel.entityPattern);
        entityPatternN.setPersistent(true);

        entityBN = lookupInstance(doc, entityModel.entityBN);
        entityBN.setPersistent(true);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(entityPatternN.getId());
        out.writeLong(entityBN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        entityPatternN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        entityBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
