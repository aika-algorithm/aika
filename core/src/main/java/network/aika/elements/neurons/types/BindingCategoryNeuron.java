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

import network.aika.Model;
import network.aika.Document;
import network.aika.elements.activations.types.BindingCategoryActivation;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.NeuronType;
import network.aika.elements.neurons.RefType;
import network.aika.elements.synapses.CategoryInputSynapse;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.elements.NeuronType.BINDING;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_INPUT;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_SAME;

/**
 * @author Lukas Molzberger
 */
@NeuronType(
        type = BINDING,
        activationFunction = LIMITED_RECTIFIED_LINEAR_UNIT,
        bindingSignalSlots = {SINGLE_INPUT, SINGLE_SAME},
        trainingAllowed = false
)
public class BindingCategoryNeuron extends CategoryNeuron {

    public BindingCategoryNeuron(NeuronProvider np) {
        super(np);
    }

    public BindingCategoryNeuron(Model m, RefType rt) {
        super(m, rt);
    }

    @Override
    public CategoryNeuron createCategoryNeuron() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CategoryInputSynapse createCategoryInputSynapse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CategoryActivation createActivation(Document doc) {
        return new BindingCategoryActivation(doc.createActivationId(), doc, this);
    }
}
