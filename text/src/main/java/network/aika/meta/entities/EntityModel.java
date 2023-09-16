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
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.EqualsRelationNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.meta.TargetInput;
import network.aika.meta.exceptions.FailedInstantiationException;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.text.GroundRef;
import network.aika.text.Range;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.InstantiationUtil.lookupInstance;
import static network.aika.elements.neurons.Neuron.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.meta.Dictionary.INPUT_TOKEN_NET_TARGET;
import static network.aika.meta.NetworkMotifs.*;
import static network.aika.queue.Phase.ANNEAL;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.queue.keys.QueueKey.MAX_ROUND;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityModel implements Writable {

    private static final Logger log = LoggerFactory.getLogger(EntityModel.class);

    public static final double ENTITY_NET_TARGET = 0.7;

    protected static final double BINDING_NET_TARGET = 2.5;

    public static final String ENTITY_LABEL = "Entity";


    protected Model model;
    protected PhraseModel phraseModel;

    protected TargetInput targetInput;

    protected CategoryNeuron entityCategory;

    protected PatternNeuron entityPattern;

    protected BindingNeuron entityBN;

    protected BindingNeuron targetInputBN;

    protected EqualsRelationNeuron relEquals;

    public record EntityInstance (
            PatternNeuron entityPatternN,
            BindingNeuron entityBN,
            BindingNeuron targetInputBN,
            PatternNeuron targetInputPN
    ) {}


    public EntityModel(PhraseModel pm) {
        this.model = pm.getModel();
        this.phraseModel = pm;
    }

    public PatternNeuron getEntityPattern() {
        return entityPattern;
    }

    public PhraseModel getPhraseModel() {
        return phraseModel;
    }

    public void initStaticNeurons() {
        targetInput = new TargetInput(model, ENTITY_LABEL);
        targetInput.initTargetInput();

        entityPattern = new PatternNeuron(model)
                .setLabel("Abstract " + ENTITY_LABEL)
                .setTargetNet(ENTITY_NET_TARGET)
                .setBias(ENTITY_NET_TARGET)
                .setPersistent(true);

        entityCategory = entityPattern.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .getInput()
                .setPersistent(true);

        entityBN = addBindingNeuron(
                phraseModel.getPatternNeuron(),
                "Abstract " + ENTITY_LABEL,
                10.0,
                BINDING_NET_TARGET
        );

        entityBN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addPositiveFeedbackLoop(
                entityBN,
                entityPattern,
                2.5,
                0.0,
                false
        );

        relEquals = new EqualsRelationNeuron(model, true, true, "Equals Rel.: ")
                .setBias(5.0)
                .setPersistent(true);

        targetInputBN = targetInput.createTargetInputBindingNeuron(entityBN, entityPattern, relEquals);

        targetInput.setTemplateOnly(true);
    }

    public void setTemplateOnly(boolean templateOnly) {
        entityPattern.setTemplateOnly(templateOnly, true);
        entityBN.setTemplateOnly(templateOnly, true);
        targetInputBN.setTemplateOnly(templateOnly, true);
    }

    public PatternNeuron addEntity(String entityLabel) {
        return model.lookupInputNeuron(entityLabel, targetInput.getTargetInput());
    }

    public void addEntityTarget(Document doc, GroundRef groundRef, String entityLabel) {
        doc.addToken(
                addEntity(entityLabel),
                groundRef
        );
    }

    public EntityInstance addEntityPattern(String label, boolean makeAbstract) {
        setTemplateOnly(false);
        targetInput.setTemplateOnly(false);
        phraseModel.getPatternNeuron().setTemplateOnly(true);

        getModel()
                .getConfig()
                .setTrainingEnabled(true)
                .setMetaInstantiationEnabled(true);

        Document doc = new Document(getModel(), label);

//        AIKADebugger.createAndShowGUI(doc);
        doc.setInstantiationCallback((tAct, iAct) -> {
            generateLabel(iAct, label);
            if (makeAbstract) {
                ConjunctiveSynapse s = (ConjunctiveSynapse) iAct.getNeuron().makeAbstract();

                if(targetInput.getTargetInput() == tAct.getNeuron()) {
                    s.setWeight(2.0);
                    s.adjustBias();
                } else
                    s.setWeight(PASSIVE_SYNAPSE_WEIGHT);
            }
        });

        try {
            doc.setFeedbackTriggerRound();

            Range entityPosRange = new Range(0, 1);
            Range entityCharRange = new Range(0, doc.length());

            doc.addToken(phraseModel.getPatternNeuron(), new GroundRef(entityPosRange, entityCharRange));
            doc.addToken(targetInput.getTargetInput(), new GroundRef(entityPosRange, entityCharRange));

            doc.process(MAX_ROUND, INFERENCE);
            doc.anneal();
            doc.process(MAX_ROUND, ANNEAL);
            doc.instantiateTemplates();

            targetInput.setTemplateOnly(true);
            phraseModel.getPatternNeuron().setTemplateOnly(false);

            return new EntityInstance(
                    lookupInstance(doc, entityPattern),
                    lookupInstance(doc, entityBN),
                    lookupInstance(doc, targetInputBN),
                    lookupInstance(doc, targetInput.getTargetInput())
            );
        } catch(Exception e) {
            throw new FailedInstantiationException("entity", e);
        } finally {
            doc.disconnect();
        }
    }

    private void generateLabel(Activation act, String label) {
        act.getNeuron().setLabel(
                act.getTemplate().getLabel().replace(ENTITY_LABEL, label)
        );
    }

    public Model getModel() {
        return phraseModel.getModel();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(entityCategory.getId());
        out.writeLong(entityPattern.getId());
        out.writeLong(entityBN.getId());
        out.writeLong(targetInputBN.getId());
        out.writeLong(relEquals.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        entityCategory = m.lookupNeuronProvider(in.readLong()).getNeuron();
        entityPattern = m.lookupNeuronProvider(in.readLong()).getNeuron();
        entityBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relEquals = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
