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
package network.aika.elements.neurons;

import network.aika.Model;
import network.aika.Document;
import network.aika.elements.Type;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.synapses.*;


import static network.aika.elements.Type.BINDING;


/**
 * @author Lukas Molzberger
 */
@NeuronType(type = BINDING)
public class BindingNeuron extends ConjunctiveNeuron<BindingNeuron, BindingActivation> {


    public BindingNeuron(Model m) {
        super(m);
    }

    public static BindingNeuron create(Model m, String label) {
        return new BindingNeuron(m)
                .setLabel(label)
                .setPersistent(true);
    }

    @Override
    public BindingCategoryInputSynapse makeAbstract() {
        BindingCategoryNeuron bindingCategory = new BindingCategoryNeuron(getModel())
                .setLabel(getLabel() + CATEGORY_LABEL);

        BindingCategoryInputSynapse s = new BindingCategoryInputSynapse()
                .link(bindingCategory, this);

        s.setInitialCategorySynapseWeight(1.0);

        return s;
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new BindingCategorySynapse();
    }

    @Override
    public BindingActivation createActivation(Document doc) {
        return new BindingActivation(doc.createActivationId(), doc, this);
    }

    @Override
    public BindingCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(BindingCategoryInputSynapse.class);
    }

    @Override
    public BindingCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(BindingCategorySynapse.class);
    }
}
