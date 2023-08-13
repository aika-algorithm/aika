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
import network.aika.elements.synapses.InhibitoryCategoryInputSynapse;
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

    protected static double PASSIVE_SYNAPSE_WEIGHT = 0.0;

    private static final String CATEGORY_LABEL = " Category";

    public static BindingCategoryNeuron makeAbstract(BindingNeuron n) {
        BindingCategoryNeuron bindingCategory = new BindingCategoryNeuron()
                .init(n.getModel(), n.getLabel() + CATEGORY_LABEL);

        bindingCategory.getProvider(true);

        new BindingCategoryInputSynapse()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .init(bindingCategory, n);

        return bindingCategory;
    }


    public static PatternCategoryNeuron makeAbstract(PatternNeuron n) {
        PatternCategoryNeuron patternCategory = new PatternCategoryNeuron()
                .init(n.getModel(), n.getLabel() + CATEGORY_LABEL);

        patternCategory.getProvider(true);

        new PatternCategoryInputSynapse()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT)
                .init(patternCategory, n);

        return patternCategory;
    }

    public static InhibitoryCategoryNeuron makeAbstract(OuterInhibitoryNeuron n) {
        InhibitoryCategoryNeuron inhibCategory = new InhibitoryCategoryNeuron(Scope.INPUT)
                .init(n.getModel(), n.getLabel() + CATEGORY_LABEL);

        inhibCategory.getProvider(true);

        new InhibitoryCategoryInputSynapse()
                .setWeight(1.0)
                .init(inhibCategory, n);

        return inhibCategory;
    }

    public static InhibitoryCategoryNeuron makeAbstract(InnerInhibitoryNeuron n) {
        InhibitoryCategoryNeuron inhibCategory = new InhibitoryCategoryNeuron(Scope.SAME)
                .init(n.getModel(), n.getLabel() + CATEGORY_LABEL);

        inhibCategory.getProvider(true);

        new InhibitoryCategoryInputSynapse()
                .setWeight(1.0)
                .init(inhibCategory, n);

        return inhibCategory;
    }
}
