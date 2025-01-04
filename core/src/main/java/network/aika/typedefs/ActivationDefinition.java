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
package network.aika.typedefs;

import network.aika.Document;
import network.aika.activations.Activation;
import network.aika.activations.Link;
import network.aika.bindingsignal.BindingSignal;
import network.aika.type.relations.RelationType;
import network.aika.type.relations.RelationTypeMany;
import network.aika.type.relations.RelationTypeOne;
import network.aika.neurons.Neuron;
import network.aika.bindingsignal.BSType;
import network.aika.type.Type;
import network.aika.type.TypeRegistry;

import java.util.*;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class ActivationDefinition extends Type<ActivationDefinition, Activation> {


    public static RelationTypeMany<ActivationDefinition, Activation, LinkDefinition, Link> INPUT = new RelationTypeMany<>(Activation::getInputLinks, "ACT-INPUT");
    public static RelationTypeMany<ActivationDefinition, Activation, LinkDefinition, Link> OUTPUT = new RelationTypeMany<>(Activation::getOutputLinks, "ACT-OUTPUT");
    public static RelationTypeOne<ActivationDefinition, Activation, NeuronDefinition, Neuron> NEURON = new RelationTypeOne<>(Activation::getNeuron, "ACT-NEURON");

    static {
        NEURON.setReversed(NeuronDefinition.ACTIVATION);
        INPUT.setReversed(LinkDefinition.OUTPUT);
        OUTPUT.setReversed(LinkDefinition.INPUT);
    }

    NeuronDefinition neuron;

    BSType wildcard;

    public ActivationDefinition(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Stream<RelationType<ActivationDefinition, Activation, ?, ?>> getRelationTypes() {
        return Stream.of(INPUT, OUTPUT, NEURON);
    }

    public Activation instantiate(int actId, Activation parent, Neuron n, Document doc, Map<BSType, BindingSignal> bindingSignals) {
        return instantiate(
                List.of(ActivationDefinition.class, Activation.class, Integer.class, Neuron.class, Document.class, Map.class),
                Arrays.asList(this, parent, actId, n, doc, bindingSignals)
        );
    }

    public NeuronDefinition getNeuron() {
        return neuron != null ?
                neuron :
                getFromParent(ActivationDefinition::getNeuron);
    }

    public ActivationDefinition setNeuron(NeuronDefinition neuron) {
        this.neuron = neuron;

        return this;
    }

    public BSType getWildcard() {
        return wildcard;
    }

    public ActivationDefinition setWildcard(BSType wildcard) {
        this.wildcard = wildcard;
        return this;
    }
}
