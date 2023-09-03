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
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.EqualsRelationNeuron;
import network.aika.elements.synapses.*;
import network.aika.enums.Scope;
import network.aika.meta.TargetInput;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.text.Range;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.meta.Dictionary.INPUT_TOKEN_NET_TARGET;
import static network.aika.meta.NetworkMotifs.*;
import static network.aika.utils.NetworkUtils.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityModel implements Writable {

    private static final Logger log = LoggerFactory.getLogger(EntityModel.class);

    public static final double ENTITY_NET_TARGET = 0.7;

    protected static final double BINDING_NET_TARGET = 2.5;


    protected Model model;
    protected PhraseModel phraseModel;

    protected TargetInput targetInput;

    protected NeuronProvider entityCategory;

    protected NeuronProvider entityPattern;

    protected NeuronProvider entityBN;

    protected NeuronProvider targetInputBN;

    protected NeuronProvider relEquals;

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

    public NeuronProvider getEntityPattern() {
        return entityPattern;
    }

    public PhraseModel getPhraseModel() {
        return phraseModel;
    }

    public void initStaticNeurons() {
        targetInput = new TargetInput(model, "Entity");
        targetInput.initTargetInput();

        entityPattern = new PatternNeuron()
                .init(model, "Abstract Entity")
                .getProvider(true);

        entityPattern.getNeuron()
                .setBias(ENTITY_NET_TARGET);

        entityCategory = makeAbstract((PatternNeuron) entityPattern.getNeuron())
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .getPInput();

        entityBN = addBindingNeuron(
                phraseModel.getPatternNeuron().getNeuron(),
                "Abstract Entity",
                10.0,
                BINDING_NET_TARGET
        ).getProvider(true);

        makeAbstract((BindingNeuron) entityBN.getNeuron())
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addPositiveFeedbackLoop(
                entityBN.getNeuron(),
                entityPattern.getNeuron(),
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
                entityPattern.getNeuron(),
                ENTITY_NET_TARGET
        );

        relEquals = EqualsRelationNeuron.createEqualsRelationNeuron(model, "Equals Rel.: ")
                .setBias(5.0)
                .getProvider(true);

        addRelation(
                bn,
                entityBN.getNeuron(),
                relEquals.getNeuron(),
                5.0,
                10.0,
                true
        );

        return bn;
    }

    public PatternNeuron addEntity(String entityLabel) {
        return targetInput.addTarget(entityLabel);
    }

    public void addEntityTarget(Document doc, Range posRange, Range charRange, String entityLabel) {
        doc.addToken(
                addEntity(entityLabel),
                posRange,
                charRange,
                INPUT_TOKEN_NET_TARGET
        );
    }

    public EntityInstance addEntityPattern(String label) {
        PatternNeuron tEPN = entityPattern.getNeuron();
        PatternNeuron n = tEPN.instantiateTemplate()
                .init(model, label);

        n.setLabel(label);
        n.setAllowTraining(false);

        BindingNeuron iEBN = instantiateBN(label, n, entityBN.getNeuron());
        BindingNeuron iTIBN = instantiateBN(label, n, targetInputBN.getNeuron());

        PatternNeuron targetInputPN = targetInput.instantiateTargetInput(label);

        Synapse s = iTIBN.getInputSynapseByType(InputObjectSynapse.class);
        if(s != null)
            s.instantiateTemplate(targetInputPN, iTIBN);

        return new EntityInstance(n, iEBN, iTIBN, targetInputPN);
    }

    private BindingNeuron instantiateBN(String label, PatternNeuron pn, BindingNeuron bn) {
        BindingNeuron iEBN = bn.instantiateTemplate()
                .init(model, label);

        bn.getInputSynapseByType(PositiveFeedbackSynapse.class)
                .instantiateTemplate(pn, iEBN);

        Synapse s = bn.getInputSynapseByType(InputObjectSynapse.class);
        if(s != null)
            s.instantiateTemplate(pn, iEBN);

        entityPattern.getNeuron().getInputSynapseByType(PatternSynapse.class)
                .instantiateTemplate(iEBN, pn);

        return iEBN;
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
        entityCategory = m.lookupNeuronProvider(in.readLong());
        entityPattern = m.lookupNeuronProvider(in.readLong());
        entityBN = m.lookupNeuronProvider(in.readLong());
        targetInputBN = m.lookupNeuronProvider(in.readLong());
        relEquals = m.lookupNeuronProvider(in.readLong());
    }
}
