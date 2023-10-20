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
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.links.innerinhibitoryloop.InnerInhibitoryCategoryLink;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.enums.Scope;

import static network.aika.elements.Type.*;

/**
 *
 * @author Lukas Molzberger
 */
public class InnerInhibitoryCategorySynapse extends CategorySynapse<InnerInhibitoryCategorySynapse, Neuron, InnerInhibitoryActivation> {

    public int getTypeId() {
        return 16;
    }

    @Override
    public Type getInputType() {
        return INNER_INHIBITORY;
    }

    @Override
    public Type getOutputType() {
        return INNER_INHIBITORY;
    }

    @Override
    public Scope getScope() {
        return Scope.SAME;
    }

    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return !latent;
    }

    @Override
    public InnerInhibitoryCategoryLink createLink(InnerInhibitoryActivation input, CategoryActivation output) {
        return new InnerInhibitoryCategoryLink(this, input, output);
    }
}
