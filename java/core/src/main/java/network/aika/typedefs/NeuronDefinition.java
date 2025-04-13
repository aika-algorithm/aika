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
public class NeuronDefinition extends Type {

    public static final RelationSelf SELF = new RelationSelf(0, "NEURON-SELF");

    public static final RelationMany INPUT = new RelationMany(1, "NEURON-INPUT");
    public static final RelationMany OUTPUT = new RelationMany(2, "NEURON-OUTPUT");
    public static final RelationMany ACTIVATION = new RelationMany(3, "NEURON-ACTIVATION");

    public static final Relation[] RELATIONS = {SELF, INPUT, OUTPUT, ACTIVATION};

    static {
        ACTIVATION.setReversed(ActivationDefinition.NEURON);
        INPUT.setReversed(SynapseDefinition.OUTPUT);
        OUTPUT.setReversed(SynapseDefinition.INPUT);
    }

    private ActivationDefinition activation;

    public NeuronDefinition(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation[] getRelations() {
        return RELATIONS;
    }

    public Neuron instantiate(Model m) {
        return new Neuron(this, m);
    }

    public ActivationDefinition getActivation() {
        return activation != null ?
                activation :
                getFromParent(p -> ((NeuronDefinition)p).getActivation());
    }

    public NeuronDefinition setActivation(ActivationDefinition activation) {
        this.activation = activation;

        return this;
    }
}
