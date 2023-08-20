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
import network.aika.elements.synapses.PatternSynapse;
import network.aika.elements.synapses.PositiveFeedbackSynapse;
import network.aika.elements.synapses.PrimarySameObjectSynapse;
import network.aika.enums.Scope;
import network.aika.meta.TargetInput;
import network.aika.meta.sequences.PhraseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.meta.NetworkMotifs.*;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityModel {

    private static final Logger log = LoggerFactory.getLogger(EntityModel.class);


    protected Model model;
    protected PhraseModel phraseModel;

    protected TargetInput targetInput;

    protected NeuronProvider entityCategory;

    protected NeuronProvider entityPattern;

    protected NeuronProvider entityBN;

    protected NeuronProvider targetInputBN;


    public double entityNetTarget = 0.7;

    protected double bindingNetTarget = 2.5;


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
        targetInput = new TargetInput(model);
        targetInput.initTargetInput();

        entityPattern = model.lookupNeuronByLabel("Abstract Entity", l ->
                new PatternNeuron()
                        .init(model, l)
        ).getProvider(true);

        entityPattern.getNeuron()
                .setBias(entityNetTarget);

        entityCategory = makeAbstract((PatternNeuron) entityPattern.getNeuron())
                .getProvider(true);

        entityBN = addBindingNeuron(
                phraseModel.getPatternNeuron().getNeuron(),
                Scope.SAME,
                "Abstract Entity",
                10.0,
                phraseModel.patternNetTarget,
                bindingNetTarget
        ).getProvider(true);

        makeAbstract((BindingNeuron) entityBN.getNeuron());

        addPositiveFeedbackLoop(
                entityBN.getNeuron(),
                entityPattern.getNeuron(),
                entityNetTarget,
                bindingNetTarget,
                2.5,
                0.0,
                false
        );

        targetInputBN = targetInput.createTargetInputBindingNeuron(
                entityPattern.getNeuron(),
                entityNetTarget
        ).getProvider(true);
    }

    public TokenNeuron addEntity(String entity) {
        return targetInput.addTarget(entity);
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
        BindingNeuron tEBN = entityBN.getNeuron();
        BindingNeuron iEBN = tEBN.instantiateTemplate()
                .init(model, label);

        tEBN.getInputSynapseByType(PositiveFeedbackSynapse.class)
                .instantiateTemplate(pn, iEBN);
        tEBN.getInputSynapseByType(PrimarySameObjectSynapse.class)
                .instantiateTemplate(pn, iEBN);

        entityPattern.getNeuron().getInputSynapseByType(PatternSynapse.class)
                .instantiateTemplate(iEBN, pn);
    }
}
