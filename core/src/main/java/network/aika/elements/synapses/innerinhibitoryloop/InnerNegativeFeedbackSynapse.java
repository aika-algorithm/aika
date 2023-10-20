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
package network.aika.elements.synapses.innerinhibitoryloop;

import network.aika.elements.Type;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.links.innerinhibitoryloop.InnerNegativeFeedbackLink;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.InnerInhibitoryNeuron;
import network.aika.elements.synapses.FeedbackSynapse;
import network.aika.enums.Scope;

import static network.aika.elements.Type.*;

/**
 *
 * @author Lukas Molzberger
 */
public class InnerNegativeFeedbackSynapse extends FeedbackSynapse<
        InnerNegativeFeedbackSynapse,
        InnerInhibitoryNeuron,
        BindingNeuron,
        InnerNegativeFeedbackLink,
        InnerInhibitoryActivation,
        BindingActivation
        >
{
    public InnerNegativeFeedbackSynapse() {
    }

    public int getTypeId() {
        return 8;
    }

    @Override
    public Type getInputType() {
        return INNER_INHIBITORY;
    }

    @Override
    public Type getOutputType() {
        return BINDING;
    }

    @Override
    public void setPropagable(boolean propagable) {
    }

    @Override
    public Scope getScope() {
        return Scope.SAME;
    }

    @Override
    protected void checkWeight() {
        if(isNegative())
            delete();
    }

    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return false;
    }

    @Override
    public InnerNegativeFeedbackLink createLink(InnerInhibitoryActivation input, BindingActivation output) {
        return new InnerNegativeFeedbackLink(this, input, output);
    }

    @Override
    public void initDummyLink(BindingActivation oAct) {
    }

    @Override
    public void linkAndPropagateOut(InnerInhibitoryActivation act) {
        getOutput()
                .linkOutgoing(this, act);
    }
}
