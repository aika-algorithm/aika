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
package network.aika.elements.synapses.positivefeedbackloop;

import network.aika.elements.Type;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.positivefeedbackloop.PositiveFeedbackLink;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.FeedbackSynapse;

import static network.aika.elements.Type.BINDING;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class PositiveFeedbackSynapse<S extends PositiveFeedbackSynapse, I extends Neuron, O extends ConjunctiveNeuron<O, OA>, L extends PositiveFeedbackLink<S, IA, OA>, IA extends Activation<?>, OA extends ConjunctiveActivation<O>> extends FeedbackSynapse<
        S,
        I,
        O,
        L,
        IA,
        OA
        >
{

    @Override
    public Type getOutputType() {
        return BINDING;
    }

    @Override
    public double getSortingWeight() {
        return 0.0;
    }

    @Override
    public boolean checkLinkingEvent(Activation act) {
        return true;
    }
}
