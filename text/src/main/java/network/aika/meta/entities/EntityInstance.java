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
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.meta.TargetInput;
import network.aika.meta.exceptions.FailedInstantiationException;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.text.GroundRef;
import network.aika.text.Range;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.neurons.Neuron.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.meta.entities.EntityModel.ENTITY_LABEL;
import static network.aika.queue.Phase.ANNEAL;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.queue.keys.QueueKey.MAX_ROUND;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityInstance extends InstantiationUtil implements Writable {

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

    public EntityInstance instantiate(String label, boolean makeAbstract) {
        getTemplateModel().prepareInstantiation();

        getModel()
                .getConfig()
                .setTrainingEnabled(true)
                .setMetaInstantiationEnabled(true);

        Document doc = new Document(getModel(), label);

//       AIKADebugger.createAndShowGUI(doc);
        doc.setInstantiationCallback((tAct, iAct) -> {
            generateLabel(iAct, label);
            if (makeAbstract) {
                ConjunctiveSynapse s = (ConjunctiveSynapse) iAct.getNeuron().makeAbstract();

                if(getTargetInput().getTargetInput() == tAct.getNeuron()) {
                    s.setWeight(2.0);
                    s.adjustBias();
                } else
                    s.setWeight(PASSIVE_SYNAPSE_WEIGHT);
            }
        });

        try {
            doc.setFeedbackTriggerRound();

            getTemplateModel().prepareExampleDoc(doc, label);

            doc.process(MAX_ROUND, INFERENCE);
            doc.anneal();
            doc.process(MAX_ROUND, ANNEAL);
            doc.instantiateTemplates();

            getTargetInput().setTemplateOnly(true);
            getPhraseModel().getTargetInput().setTemplateOnly(true);
            getPhraseModel().getPatternNeuron().setTemplateOnly(false);

            entityPatternN = lookupInstance(doc, entityModel.entityPattern);
            entityBN = lookupInstance(doc, entityModel.entityBN);
            targetInputBN = lookupInstance(doc, entityModel.targetInputBN);
            targetInputPN = lookupInstance(doc, entityModel.targetInput.getTargetInput());

        } catch(Exception e) {
            throw new FailedInstantiationException("entity", e);
        } finally {
            doc.disconnect();
        }

        return this;
    }

    private void generateLabel(Activation act, String label) {
        act.getNeuron().setLabel(
                act.getTemplate().getLabel().replace(ENTITY_LABEL, label)
        );
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
