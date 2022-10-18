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
package network.aika.neuron.conjunctive;

import network.aika.fields.LinkSlot;
import network.aika.fields.Multiplication;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.*;

import static network.aika.fields.FieldLink.connect;
import static network.aika.fields.Fields.mul;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class FeedbackSynapse<S extends FeedbackSynapse, I extends Neuron, L extends Link<S, IA, BindingActivation>, IA extends Activation<?>> extends BindingNeuronSynapse<
        S,
        I,
        L,
        IA
        > {

    public FeedbackSynapse(Scope scope) {
        super(scope);
    }

    public void initDummyLink(BindingActivation oAct) {
        Multiplication dummyWeight = mul(
                oAct,
                (getDummyLinkUB() ? "pos" : "neg")  + "-" + getInput().getId(),
                oAct.getIsOpen(),
                getWeight()
        );

        LinkSlot ls = oAct.lookupLinkSlot(this, getDummyLinkUB());
        connect(dummyWeight, -1, ls);
    }

    protected abstract boolean getDummyLinkUB();
}