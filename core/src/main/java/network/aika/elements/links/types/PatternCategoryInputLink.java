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
package network.aika.elements.links.types;

import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.CategoryInputLink;
import network.aika.elements.links.PositiveFeedbackLink;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.types.PatternCategoryInputSynapse;
import network.aika.elements.synapses.types.PatternCategorySynapse;
import network.aika.fields.Field;


/**
 * @author Lukas Molzberger
 */
public class PatternCategoryInputLink extends PositiveFeedbackLink<PatternCategoryInputSynapse, CategoryActivation, PatternActivation> implements CategoryInputLink {

    public PatternCategoryInputLink(PatternCategoryInputSynapse s, CategoryActivation input, PatternActivation output) {
        super(s, input, output);
    }

    @Override
    protected Field getFeedbackTrigger() {
        return getDocument().getInstantiationFeedbackTrigger();
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new PatternCategorySynapse();
    }

    @Override
    protected void connectGradientFields() {
    }

    @Override
    public void instantiateTemplate(CategoryActivation iAct, PatternActivation oAct) {
        instantiateTemplate(iAct, oAct, this);
    }
}