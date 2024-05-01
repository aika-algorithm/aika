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
package network.aika.elements.activations;

import network.aika.Document;
import network.aika.elements.links.*;
import network.aika.text.TextReference;
import network.aika.elements.neurons.CategoryNeuron;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public abstract class CategoryActivation extends DisjunctiveActivation {

    public CategoryActivation(int id, Document doc, CategoryNeuron neuron) {
        super(id, doc, neuron);
    }

    @Override
    public CategoryInputLink getActiveCategoryInputLink() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void linkTemplateAndInstance(Activation<CategoryNeuron> ti) {
        if(ti.getOutputLinksByType(CategoryInputLink.class)
                .count() == 0)
            createCategoryInputLink(ti);
    }

    public void createCategoryInputLink(Activation<CategoryNeuron> ti) {
        CategoryInputSynapse cis = ti.getNeuron().getOutgoingCategoryInputSynapse();
        if(cis == null) {
            instantiateCategoryInputSynapse(ti);
            return;
        }

        Synapse s = ((Synapse)cis);
        Activation catInputAct = getActiveCategoryInput();

        s.createAndInitLink(ti, catInputAct);
    }

    private void instantiateCategoryInputSynapse(Activation<CategoryNeuron> ti) {
        Link cil = (Link) getOutputLinksByType(CategoryInputLink.class)
                .findFirst()
                .orElse(null);

        if(cil == null)
            return;

        Activation catInputAct = getActiveCategoryInput();

        cil.instantiateTemplate(
                ti,
                catInputAct
        );
    }

    @Override
    public Activation getTemplate() {
        return getOutputLinksByType(CategoryInputLink.class)
                .map(CategoryInputLink::getOutput)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Activation getActiveTemplateInstance() {
        Activation inputAct = getActiveCategoryInput();
        if(inputAct == null)
            return null;

        CategoryInputLink cil = inputAct.getActiveCategoryInputLink();
        if(cil == null)
            return null;

        return cil.getInput();
    }

    public Activation getActiveCategoryInput() {
        return getCategoryInputs()
                .filter(Activation::isActiveTemplateInstance)
                .max(
                        Comparator.comparingDouble(act ->
                                act.getValue().getValue()
                        )
                )
                .orElse(null);
    }

    public Stream<Activation> getCategoryInputs() {
        return getInputLinks()
                .map(Link::getInput);
    }

    public Activation getCategoryInput() {
        return getCategoryInputs()
                .findFirst()
                .orElse(null);
    }

    @Override
    public TextReference getTextReference() {
        Activation iAct = getCategoryInput();
        return iAct != null ? iAct.getTextReference() : null;
    }
}
