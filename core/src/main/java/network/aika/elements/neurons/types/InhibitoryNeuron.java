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
package network.aika.elements.neurons.types;


/**
 *
 * @author Lukas Molzberger
 */
/*
public class InhibitoryNeuron extends DisjunctiveNeuron<InhibitoryNeuron, InhibitoryActivation> {

    public InhibitoryNeuron(NeuronProvider np) {
        super(np);
    }

    public InhibitoryNeuron(Model m, RefType rt) {
        super(m, rt);
    }


    @Override
    public InhibitoryCategoryNeuron createCategoryNeuron() {
        return new InhibitoryCategoryNeuron(getModel(), CATEGORY)
                .setLabel(getLabel() + CATEGORY_LABEL);
    }

    @Override
    public InhibitoryCategoryInputSynapse createCategoryInputSynapse() {
        return new InhibitoryCategoryInputSynapse();
    }

    @Override
    public InhibitoryNeuron instantiateTemplate() {
        InhibitoryNeuron n = new InhibitoryNeuron(getModel(), TEMPLATE);
        n.initFromTemplate(this);
        return n;
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new InhibitoryCategorySynapse();
    }

    @Override
    public InhibitoryCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(InhibitoryCategoryInputSynapse.class);
    }

    @Override
    public InhibitoryCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(InhibitoryCategorySynapse.class);
    }
}
*/