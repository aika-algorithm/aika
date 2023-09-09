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
        targetInput = new TargetInput(model, "Entity");
        targetInput.initTargetInput();

        entityPattern = new PatternNeuron(model)
                .setLabel("Abstract Entity")
                .setTargetNet(ENTITY_NET_TARGET)
                .setBias(ENTITY_NET_TARGET)
                .setPersistent(true);

        entityCategory = makeAbstract(entityPattern)
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .getInput()
                .setPersistent(true);

        entityBN = addBindingNeuron(
                phraseModel.getPatternNeuron(),
                "Abstract Entity",
                10.0,
                BINDING_NET_TARGET
        );

        makeAbstract(entityBN)
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addPositiveFeedbackLoop(
                entityBN,
                entityPattern,
                2.5,
                0.0,
                false
        );

        targetInputBN = createTargetInputBindingNeuron();

        targetInput.setTemplateOnly(true);
    }

    protected BindingNeuron createTargetInputBindingNeuron() {
        BindingNeuron bn = targetInput.createTargetInputBindingNeuron(
                entityPattern,
                ENTITY_NET_TARGET
        );

        relEquals = EqualsRelationNeuron.createEqualsRelationNeuron(model, "Equals Rel.: ")
                .setBias(5.0)
                .setPersistent(true);

        addRelation(
                bn,
                entityBN,
                relEquals,
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
        PatternNeuron tEPN = entityPattern;
        PatternNeuron n = tEPN.instantiateTemplate()
                .setLabel(label);

        n.setLabel(label);
        n.setAllowTraining(false);


        BindingNeuron eBN = entityBN;
        PatternNeuron phrasePN = eBN.getInputSynapseByType(InputObjectSynapse.class).getInput();

        PatternNeuron targetInputPN = targetInput.instantiateTargetInput(label);

        BindingNeuron iEBN = instantiateBN(label, n, entityBN, phrasePN);
        BindingNeuron iTIBN = instantiateBN(label, n, targetInputBN, targetInputPN);

        instantiateRelation(iTIBN, iEBN);

        PatternCategorySynapse catSyn = n.getOutputSynapseByType(PatternCategorySynapse.class);
        catSyn.setWeight(0.0);

        return new EntityInstance(n, iEBN, iTIBN, targetInputPN);
    }

    private void instantiateRelation(BindingNeuron iTIBN, BindingNeuron iEBN) {
        SameObjectSynapse tSoSyn = entityBN.getInputSynapseByType(SameObjectSynapse.class);
        SameObjectSynapse soSyn = tSoSyn.instantiateTemplate(iTIBN, iEBN);

        RelationInputSynapse tRelSyn = (RelationInputSynapse) entityBN.getProvider().getSynapseBySynId(tSoSyn.getRelationSynId());
        RelationInputSynapse relSyn = tRelSyn.instantiateTemplate(tRelSyn.getInput(), iEBN);

        soSyn.setRelationSynId(relSyn.getSynapseId());
    }

    private BindingNeuron instantiateBN(String label, PatternNeuron pn, BindingNeuron bn, PatternNeuron io) {
        BindingNeuron ieBN = bn.instantiateTemplate()
                .setLabel(label);

        Synapse s = bn.getInputSynapseByType(InputObjectSynapse.class);
        if(s != null)
            s.instantiateTemplate(io, ieBN);

        bn.getInputSynapseByType(PositiveFeedbackSynapse.class)
                .instantiateTemplate(pn, ieBN);

        entityPattern.getInputSynapseByType(PatternSynapse.class)
                .instantiateTemplate(ieBN, pn);

        return ieBN;
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
