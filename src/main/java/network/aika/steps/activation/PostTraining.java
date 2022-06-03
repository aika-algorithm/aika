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
package network.aika.steps.activation;

import network.aika.neuron.Neuron;
import network.aika.neuron.activation.DummyActivation;
import network.aika.neuron.conjunctive.ConjunctiveNeuron;
import network.aika.steps.Step;


/**
 * Determines which input synapses of this activations neuron should be linked to the input neuron.
 * Connecting a synapse to its input neuron is not necessary if the synapse weight is weak. That is the case if the
 * synapse is incapable to completely suppress the activation of this neuron.
 *
 * @author Lukas Molzberger
 */
public class PostTraining extends Step<DummyActivation> {

    public static void add(Neuron n) {
        if(n.isTemplate())
            return;

        if(!(n instanceof ConjunctiveNeuron<?,?>))
            return;

        ConjunctiveNeuron cn = (ConjunctiveNeuron) n;

        if(cn.getUpdateAllowPropagateIsQueued())
            return;

        Step.add(new PostTraining(new DummyActivation(n)));
    }

    private PostTraining(DummyActivation act) {
        super(act);
    }

    @Override
    public void process() {
        Neuron n = getElement().getNeuron();

        assert !n.isTemplate();
        n.updateAllowPropagate();
    }
}
