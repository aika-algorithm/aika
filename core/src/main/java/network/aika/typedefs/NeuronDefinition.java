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
import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.neurons.Neuron;
import network.aika.neurons.Synapse;
import network.aika.type.relations.RelationSelf;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronDefinition extends Type<NeuronDefinition, Neuron> {

    public static final RelationSelf<ActivationDefinition, Activation> SELF = new RelationSelf<>(0, "NEURON-SELF");

    public static final RelationMany<NeuronDefinition, Neuron, SynapseDefinition, Synapse> INPUT = new RelationMany<>(Neuron::getInputSynapsesAsStream, 1, "NEURON-INPUT");
    public static final RelationMany<NeuronDefinition, Neuron, SynapseDefinition, Synapse> OUTPUT = new RelationMany<>(Neuron::getOutputSynapsesAsStream, 2, "NEURON-OUTPUT");
    public static final RelationMany<NeuronDefinition, Neuron, ActivationDefinition, Activation> ACTIVATION = new RelationMany<>(null, 3, "NEURON-ACTIVATION");

    public static final Relation[] RELATIONS = {SELF, INPUT, OUTPUT, ACTIVATION};

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
    public Relation<NeuronDefinition, Neuron, ?, ?>[] getRelations() {
        return RELATIONS;
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
