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
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.types.InhibitoryCategoryActivation;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.NeuronType;

import static network.aika.elements.Type.INHIBITORY;
import static network.aika.enums.Scope.INPUT;

/**
 * @author Lukas Molzberger
 */
@NeuronType(
        type = INHIBITORY,
        bindingSignalSlots = INPUT
)
public class InhibitoryCategoryNeuron extends CategoryNeuron {

    public InhibitoryCategoryNeuron(NeuronProvider np) {
        super(np);
    }

    public InhibitoryCategoryNeuron(Model m) {
        super(m);
    }

    @Override
    public CategoryActivation createActivation(Document doc) {
        return new InhibitoryCategoryActivation(doc.createActivationId(), doc, this);
    }
}
