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
package network.aika.elements.links;

import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.InnerInhibitoryCategoryInputSynapse;
import network.aika.elements.synapses.InhibitoryCategorySynapse;
import network.aika.visitor.pattern.PatternCategoryVisitor;


/**
 * @author Lukas Molzberger
 */
public class InnerInhibitoryCategoryInputLink extends DisjunctiveLink<InnerInhibitoryCategoryInputSynapse, CategoryActivation, InnerInhibitoryActivation> implements CategoryInputLink {

    public InnerInhibitoryCategoryInputLink(InnerInhibitoryCategoryInputSynapse s, CategoryActivation input, InnerInhibitoryActivation output) {
        super(s, input, output);
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new InhibitoryCategorySynapse();
    }

    @Override
    public void patternCatVisit(PatternCategoryVisitor v, int depth) {
    }

    @Override
    public void instantiateTemplate(CategoryActivation iAct, InnerInhibitoryActivation oAct) {
        instantiateTemplate(iAct, oAct, this);
    }
}