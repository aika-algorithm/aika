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
package network.aika.elements.typedef.model;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.ActivationTypeDefinition;
import network.aika.elements.typedef.LinkTypeDefinition;
import network.aika.elements.typedef.NeuronTypeDefinition;
import network.aika.elements.typedef.SynapseTypeDefinition;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronDef {

    TypeModel typeModel;

    private ActivationTypeDefinition activation;
    private NeuronTypeDefinition neuron;

    private LinkTypeDefinition link;

    private SynapseTypeDefinition synapse;

    public NeuronDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {
        activation = new ActivationTypeDefinition(
                "Activation",
                Activation.class
        );

        neuron = new NeuronTypeDefinition(
                "Neuron",
                Neuron.class
        );

        link = new LinkTypeDefinition(
                "Link",
                Link.class);

        synapse = new SynapseTypeDefinition(
                "Synapse",
                Synapse.class
        );
    }

    public TypeModel getTypeModel() {
        return typeModel;
    }

    public ActivationTypeDefinition getActivation() {
        return activation;
    }

    public NeuronTypeDefinition getNeuron() {
        return neuron;
    }

    public LinkTypeDefinition getLink() {
        return link;
    }

    public SynapseTypeDefinition getSynapse() {
        return synapse;
    }
}
