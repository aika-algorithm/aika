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
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.PatternNeuron;


/**
 * @author Lukas Molzberger
 */
public class ConjunctiveNeuronPropertyPanel<E extends ConjunctiveNeuron> extends NeuronPropertyPanel<E> {


    public ConjunctiveNeuronPropertyPanel(E n, Activation ref) {
        super(n, ref);
    }

    public static ConjunctiveNeuronPropertyPanel create(ConjunctiveNeuron n, Activation ref) {
        if(n instanceof BindingNeuron) {
            return BindingNeuronPropertyPanel.create((BindingNeuron) n, ref);
        } else if(n instanceof PatternNeuron) {
            return PatternNeuronPropertyPanel.create((PatternNeuron) n, ref);
        }

        return new ConjunctiveNeuronPropertyPanel(n, ref);
    }
}
