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
package network.aika.elements.synapses;

import network.aika.elements.activations.BindingActivation;
import network.aika.elements.links.FeedbackLink;
import network.aika.enums.Scope;
import network.aika.Thought;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.Neuron;
import network.aika.fields.Field;

import static network.aika.fields.FieldLink.linkAndConnect;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class FeedbackSynapse<S extends FeedbackSynapse, I extends Neuron, L extends FeedbackLink<S, IA>, IA extends Activation<?>> extends BindingNeuronSynapse<
        S,
        I,
        L,
        IA
        > {

    public FeedbackSynapse(Scope scope) {
        super(scope);
    }


    @Override
    public Field getOutputNetForBias(BindingActivation act) {
        return act.getNet();
    }

    @Override
    public Field getOutputNetForWeight(BindingActivation act) {
        return act.getNet();
    }

    public void initDummyLink(BindingActivation oAct) {
        if(!linkExists(oAct, true))
            createAndInitLink(null, oAct);
    }

    @Override
    protected void warmUpInputNeuron(Thought t) {
    }

    @Override
    public boolean isLatentLinkingAllowed() {
        return false;
    }
}
