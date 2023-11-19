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

import network.aika.Document;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.PositiveFeedbackLink;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.Neuron;
import network.aika.fields.Field;
import network.aika.fields.SynapseOutputSlot;

import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.max;
import static network.aika.fields.Fields.mul;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class PositiveFeedbackSynapse<S extends PositiveFeedbackSynapse, I extends Neuron, O extends ConjunctiveNeuron<O, OA>, L extends PositiveFeedbackLink<S, IA, OA>, IA extends Activation<?>, OA extends ConjunctiveActivation<O>>
        extends ConjunctiveSynapse<S, I, O, L, IA, OA>
{

    public void initFeedbackSynapseOutputSlot(ConjunctiveActivation oAct) {
        oAct.registerInputSlot(this);
    }

    @Override
    public void connectOutputToNet(SynapseOutputSlot slot, Activation oAct) {
        linkAndConnect(
                max(this, "ft-max",
                        slot,
                        mul(
                                this,
                                "weighted ft",
                                getFeedbackTrigger(oAct.getDocument()), true,
                                weight, false
                        )
                ),
                oAct.getNet()
        );
    }

    protected Field getFeedbackTrigger(Document doc) {
        return doc.getFeedbackTrigger();
    }

    @Override
    public double getWeightForNetUB() {
        return 0.0;
    }
}
