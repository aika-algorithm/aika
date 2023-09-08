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
package network.aika.utils;

import network.aika.elements.neurons.*;
import network.aika.elements.synapses.BindingCategoryInputSynapse;
import network.aika.elements.synapses.InnerInhibitoryCategoryInputSynapse;
import network.aika.elements.synapses.OuterInhibitoryCategoryInputSynapse;
import network.aika.elements.synapses.PatternCategoryInputSynapse;
import network.aika.enums.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Lukas Molzberger
 */
public class NetworkUtils {

    private static final Logger log = LoggerFactory.getLogger(NetworkUtils.class);

    public static double PASSIVE_SYNAPSE_WEIGHT = 0.0;

    private static final String CATEGORY_LABEL = " Category";

    public static BindingCategoryInputSynapse makeAbstract(BindingNeuron n) {
        BindingCategoryNeuron bindingCategory = new BindingCategoryNeuron(n.getModel())
                .setLabel(n.getLabel() + CATEGORY_LABEL);

        bindingCategory.getProvider(true);

        BindingCategoryInputSynapse s = new BindingCategoryInputSynapse()
                .init(bindingCategory, n);

        s.setInitialCategorySynapseWeight(1.0);

        return s;
    }


    public static PatternCategoryInputSynapse makeAbstract(PatternNeuron n) {
        PatternCategoryNeuron patternCategory = new PatternCategoryNeuron(n.getModel())
                .setLabel(n.getLabel() + CATEGORY_LABEL);

        patternCategory.getProvider(true);

        PatternCategoryInputSynapse s = new PatternCategoryInputSynapse()
                .init(patternCategory, n);

        s.setInitialCategorySynapseWeight(1.0);

        return s;
    }

    public static OuterInhibitoryCategoryInputSynapse makeAbstract(OuterInhibitoryNeuron n) {
        InhibitoryCategoryNeuron inhibCategory = new InhibitoryCategoryNeuron(n.getModel(), Scope.INPUT)
                .setLabel(n.getLabel() + CATEGORY_LABEL);

        inhibCategory.getProvider(true);

        OuterInhibitoryCategoryInputSynapse s = new OuterInhibitoryCategoryInputSynapse()
                .init(inhibCategory, n);

        s.setInitialCategorySynapseWeight(1.0);

        return s;
    }

    public static InnerInhibitoryCategoryInputSynapse makeAbstract(InnerInhibitoryNeuron n) {
        InhibitoryCategoryNeuron inhibCategory = new InhibitoryCategoryNeuron(n.getModel(), Scope.SAME)
                .setLabel(n.getLabel() + CATEGORY_LABEL);

        inhibCategory.getProvider(true);

        InnerInhibitoryCategoryInputSynapse s = new InnerInhibitoryCategoryInputSynapse()
                .init(inhibCategory, n);

        s.setInitialCategorySynapseWeight(1.0);

        return s;
    }
}
