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
package network.aika.debugger.neurons.properties.neurons;

import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.LatentRelationNeuron;

import static network.aika.utils.StringUtils.doubleToString;


/**
 * @author Lukas Molzberger
 */
public class BindingNeuronPropertyPanel<E extends BindingNeuron> extends ConjunctiveNeuronPropertyPanel<E> {

    public BindingNeuronPropertyPanel(E n, Activation ref) {
        super(n, ref);

        addConstant("Is Primary: ", "" + n.isPrimary());
    }

    public static BindingNeuronPropertyPanel create(BindingNeuron n, Activation ref) {
        if(n instanceof LatentRelationNeuron) {
            return LatentRelationNeuronPropertyPanel.create((LatentRelationNeuron) n, ref);
        }

        return new BindingNeuronPropertyPanel(n, ref);
    }
}
