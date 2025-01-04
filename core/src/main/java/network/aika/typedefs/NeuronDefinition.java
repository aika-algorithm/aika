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

import network.aika.Model;
import network.aika.activations.Activation;
import network.aika.activations.Link;
import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.RelationType;
import network.aika.type.relations.RelationTypeMany;
import network.aika.neurons.Neuron;
import network.aika.neurons.RefType;
import network.aika.neurons.Synapse;

import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronDefinition extends Type<NeuronDefinition, Neuron> {

    public static RelationTypeMany<NeuronDefinition, Neuron, SynapseDefinition, Synapse> INPUT = new RelationTypeMany<>(Neuron::getInputSynapsesAsStream, "NEURON-INPUT");
    public static RelationTypeMany<NeuronDefinition, Neuron, SynapseDefinition, Synapse> OUTPUT = new RelationTypeMany<>(Neuron::getOutputSynapsesAsStream, "NEURON-OUTPUT");
    public static RelationTypeMany<NeuronDefinition, Neuron, ActivationDefinition, Activation> ACTIVATION = new RelationTypeMany<>(null, "NEURON-ACTIVATION");

    static {
        ACTIVATION.setReversed(ActivationDefinition.NEURON);
        INPUT.setReversed(SynapseDefinition.OUTPUT);
        OUTPUT.setReversed(SynapseDefinition.INPUT);
    }

    private Boolean trainingAllowed;

    private ActivationDefinition activation;

    public NeuronDefinition(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Stream<RelationType<NeuronDefinition, Neuron, ?, ?>> getRelationTypes() {
        return Stream.of(INPUT, OUTPUT, ACTIVATION);
    }

    public Neuron instantiate(Model m) {
        return instantiate(
                List.of(NeuronDefinition.class, Model.class),
                List.of(this, m)
        );
    }

    public ActivationDefinition getActivation() {
        return activation != null ?
                activation :
                getFromParent(NeuronDefinition::getActivation);
    }

    public NeuronDefinition setActivation(ActivationDefinition activation) {
        this.activation = activation;

        return this;
    }

    public NeuronDefinition setTrainingAllowed(boolean trainingAllowed) {
        this.trainingAllowed = trainingAllowed;

        return this;
    }

    public Boolean isTrainingAllowed() {
        return trainingAllowed != null ?
                trainingAllowed :
                getFromParent(NeuronDefinition::isTrainingAllowed);
    }

}
