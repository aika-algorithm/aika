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
package network.aika.meta.sequences;

import network.aika.Model;
import network.aika.elements.neurons.*;
import network.aika.meta.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class PhraseModel extends SequenceModel {

    private static final Logger log = LoggerFactory.getLogger(PhraseModel.class);


    NeuronProvider upperCaseN;

    protected NeuronProvider targetCategoryCat;

    protected NeuronProvider targetCategoryPattern;

    protected double targetCategoryNetTarget = 5.0;


    public PhraseModel(Model m, Dictionary dict) {
        super(m, dict);
    }


    public PatternNeuron addTargetCategory(String category) {
        return model.lookupNeuronByLabel(category, l ->
                createTargetCategoryPattern(category)
        );
    }

    protected PatternNeuron createTargetCategoryPattern(String label) {
        PatternNeuron tcpN = targetCategoryPattern.getNeuron();
        PatternNeuron n = tcpN.instantiateTemplate()
                .init(model, label);

        n.setLabel(label);
        n.setAllowTraining(false);

        return n;
    }

    @Override
    public void initStaticNeurons() {
        super.initStaticNeurons();

        targetCategoryPattern = model.lookupNeuronByLabel("Abstract Target Category", l ->
                new PatternNeuron()
                        .init(model, l)
        ).getProvider(true);

        targetCategoryPattern.getNeuron()
                .setBias(targetCategoryNetTarget);

        targetCategoryCat = makeAbstract((PatternNeuron) targetCategoryPattern.getNeuron())
                .getProvider(true);


        upperCaseN = new PatternCategoryNeuron()
                .init(model, "Upper Case")
                .getProvider(true);
    }

    @Override
    public String getPatternType() {
        return "Phrase";
    }

    @Override
    protected void initTemplateBindingNeurons() {
        primaryBN = createPrimaryBindingNeuron()
                .getProvider(true);

        createTargetInputBindingNeuron();

        expandContinueBindingNeurons(
                1,
                primaryBN.getNeuron(),
                5,
                1
        );

        expandContinueBindingNeurons(
                1,
                primaryBN.getNeuron(),
                5,
                -1
        );

        applyMarginToInnerNegFeedbackSynapse(primaryBN.getNeuron());
    }
}
