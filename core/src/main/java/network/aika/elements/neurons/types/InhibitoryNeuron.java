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

import network.aika.ActivationFunction;
import network.aika.Model;
import network.aika.Document;
import network.aika.elements.activations.types.InhibitoryActivation;
import network.aika.elements.neurons.DisjunctiveNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.NeuronType;
import network.aika.elements.neurons.RefType;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.types.InhibitoryCategoryInputSynapse;
import network.aika.elements.synapses.types.InhibitoryCategorySynapse;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.elements.Type.INHIBITORY;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_INPUT;
import static network.aika.elements.neurons.RefType.CATEGORY;
import static network.aika.elements.neurons.RefType.TEMPLATE;

/**
 *
 * @author Lukas Molzberger
 */
@NeuronType(
        type = INHIBITORY,
        activationFunction = LIMITED_RECTIFIED_LINEAR_UNIT,
        bindingSignalSlots = SINGLE_INPUT,
        trainingAllowed = false
)
public class InhibitoryNeuron extends DisjunctiveNeuron<InhibitoryNeuron, InhibitoryActivation> {

    public InhibitoryNeuron(NeuronProvider np) {
        super(np);
    }

    public InhibitoryNeuron(Model m, RefType rt) {
        super(m, rt);
    }

    @Override
    public InhibitoryCategoryInputSynapse makeAbstract(boolean instantiable) {
        setInstantiable(true);

        InhibitoryCategoryNeuron inhibCategory = new InhibitoryCategoryNeuron(getModel(), CATEGORY)
                .setInstantiable(instantiable)
                .setLabel(getLabel() + CATEGORY_LABEL);

        InhibitoryCategoryInputSynapse s = new InhibitoryCategoryInputSynapse()
                .link(inhibCategory, this);

        s.setInitialCategorySynapseWeight(1.0);

        inhibCategory.getProvider().decreaseRefCount(CATEGORY);
        return s;
    }

    @Override
    public InhibitoryNeuron instantiateTemplate() {
        InhibitoryNeuron n = new InhibitoryNeuron(getModel(), TEMPLATE);
        n.initFromTemplate(this);
        return n;
    }

    @Override
    public InhibitoryActivation createActivation(Document doc) {
        return new InhibitoryActivation(doc.createActivationId(), doc, this);
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
