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
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.PatternCategoryNeuron;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.synapses.PositiveFeedbackSynapse;
import network.aika.meta.Dictionary;
import network.aika.meta.sequences.PhraseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.meta.NetworkMotifs.*;
import static network.aika.meta.sequences.SequenceModel.POS_MARGIN;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class EntityModel {

    private static final Logger log = LoggerFactory.getLogger(EntityModel.class);


    protected Model model;
    protected PhraseModel phraseModel;

    protected NeuronProvider entityCategory;

    protected NeuronProvider entityPattern;

    protected NeuronProvider entityBN;

    protected double entityNetTarget = 0.7;

    protected double bindingNetTarget = 2.5;


    public EntityModel(PhraseModel pm) {
        this.model = pm.getModel();
        this.phraseModel = pm;
    }

    public PatternNeuron addEntity(String entity) {
        return model.lookupNeuronByLabel(entity, l ->
                createEntityPattern(entity)
        );
    }

    protected PatternNeuron createEntityPattern(String label) {
        PatternNeuron tcpN = entityPattern.getNeuron();
        PatternNeuron n = tcpN.instantiateTemplate()
                .init(model, label);

        n.setLabel(label);
        n.setAllowTraining(false);

        return n;
    }

    public void initStaticNeurons() {
        entityPattern = model.lookupNeuronByLabel("Abstract Entity", l ->
                new PatternNeuron()
                        .init(model, l)
        ).getProvider(true);

        entityPattern.getNeuron()
                .setBias(entityNetTarget);

        entityCategory = makeAbstract((PatternNeuron) entityPattern.getNeuron())
                .getProvider(true);


        BindingNeuron bn = new BindingNeuron()
                .init(model, "Abstract Entity");
        bn.setBias(bindingNetTarget);
        makeAbstract(bn);
        entityBN = bn.getProvider(true);


        addPositiveFeedbackLoop(
                bn,
                entityPattern.getNeuron(),
                entityNetTarget,
                bindingNetTarget,
                2.5,
                0.0,
                false
        );

        addPositiveFeedbackSynapse(
                bn,
                phraseModel.getPatternNeuron().getNeuron(),
                phraseModel.patternNetTarget,
                bindingNetTarget
        );
    }
}
