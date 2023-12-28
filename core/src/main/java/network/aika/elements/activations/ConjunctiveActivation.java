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
package network.aika.elements.activations;

import network.aika.Document;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.queue.steps.FeedbackTrigger;

import static network.aika.fields.link.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.scale;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveActivation<N extends ConjunctiveNeuron<N, ?>> extends Activation<N> {

    public ConjunctiveActivation(int id, Document doc, N n) {
        super(id, doc, n);

        if(getConfig().isMetaInstantiationEnabled()) {
            FeedbackTrigger.add(this, true);
        }
    }


    @Override
    protected void connectWeightUpdate() {
        negUpdateValue = scale(
                this,
                "-updateValue",
                -1.0,
                updateValue
        );

        linkAndConnect(
                updateValue,
                getNeuron().getBias()
        );
    }

    @Override
    protected void initBiases() {
        neuron.getSynapseBiasSynapses()
                .forEach(s ->
                        s.initBiasInput(this)
                );

        super.initBiases();
    }
}
