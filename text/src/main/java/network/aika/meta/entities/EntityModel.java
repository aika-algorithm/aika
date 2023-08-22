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
import network.aika.elements.synapses.*;
import network.aika.enums.Scope;
import network.aika.meta.TargetInput;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.text.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.meta.Dictionary.INPUT_TOKEN_NET_TARGET;
import static network.aika.meta.NetworkMotifs.*;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityModel {

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
                .getProvider(true);

        entityBN = addBindingNeuron(
                phraseModel.getPatternNeuron().getNeuron(),
                Scope.SAME,
                "Abstract Entity",
                10.0,
                phraseModel.PATTERN_NET_TARGET,
                BINDING_NET_TARGET
        ).getProvider(true);

        makeAbstract((BindingNeuron) entityBN.getNeuron());

        addPositiveFeedbackLoop(
                entityBN.getNeuron(),
                entityPattern.getNeuron(),
                ENTITY_NET_TARGET,
                BINDING_NET_TARGET,
                2.5,
                0.0,
                false
        );

        targetInputBN = targetInput.createTargetInputBindingNeuron(
                entityPattern.getNeuron(),
                ENTITY_NET_TARGET
        ).getProvider(true);

        new SameObjectSynapse()
                .setWeight(10.0)
                .init(targetInputBN.getNeuron(), entityBN.getNeuron())
                .adjustBias(targetInput.bindingNetTarget);

        targetInput.setTemplateOnly(true);
    }

    public TokenNeuron addEntity(String entityLabel) {
        return targetInput.addTarget(entityLabel);
    }

    public void addEntityTarget(Document doc, String entityLabel) {
        doc.addToken(
                addEntity(entityLabel),
                null,
                new Range(0, doc.length()),
                INPUT_TOKEN_NET_TARGET
        );
    }

    public PatternNeuron addEntityPattern(String label) {
        PatternNeuron tEPN = entityPattern.getNeuron();
        PatternNeuron n = tEPN.instantiateTemplate()
                .init(model, label);

        n.setLabel(label);
        n.setAllowTraining(false);

        instantiateBN(label, n, entityBN.getNeuron());
        instantiateBN(label, n, targetInputBN.getNeuron());

        return n;
    }

    private void instantiateBN(String label, PatternNeuron pn, BindingNeuron bn) {
        BindingNeuron iEBN = bn.instantiateTemplate()
                .init(model, label);

        bn.getInputSynapseByType(PositiveFeedbackSynapse.class)
                .instantiateTemplate(pn, iEBN);

        Synapse s = bn.getInputSynapseByType(PrimarySameObjectSynapse.class);
        if(s != null)
            s.instantiateTemplate(pn, iEBN);

        entityPattern.getNeuron().getInputSynapseByType(PatternSynapse.class)
                .instantiateTemplate(iEBN, pn);
    }
}
