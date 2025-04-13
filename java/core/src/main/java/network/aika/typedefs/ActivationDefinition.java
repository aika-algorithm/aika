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
import network.aika.activations.*;
import network.aika.bindingsignal.BindingSignal;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;
import network.aika.neurons.Neuron;
import network.aika.bindingsignal.BSType;
import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.RelationSelf;

import java.util.*;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class ActivationDefinition extends Type {

    public enum ActivationSubType {
        CONJUNCTIVE,
        DISJUNCTIVE,
        INHIBITORY
    }

    public static final RelationSelf SELF = new RelationSelf(0, "ACT-SELF");

    public static final RelationMany INPUT = new RelationMany( 1, "ACT-INPUT");
    public static final RelationMany OUTPUT = new RelationMany( 2, "ACT-OUTPUT");
    public static final RelationOne NEURON = new RelationOne(3, "ACT-NEURON");

    public static final Relation[] RELATIONS = {SELF, INPUT, OUTPUT, NEURON};

    static {
        NEURON.setReversed(NeuronDefinition.ACTIVATION);
        INPUT.setReversed(LinkDefinition.OUTPUT);
        OUTPUT.setReversed(LinkDefinition.INPUT);
    }

    ActivationSubType subType;

    NeuronDefinition neuron;

    BSType wildcard;

    public ActivationDefinition(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation[] getRelations() {
        return RELATIONS;
    }

    public Activation instantiate(int actId, Activation parent, Neuron n, Document doc, Map<BSType, BindingSignal> bindingSignals) {
        switch (subType) {
            case CONJUNCTIVE:
                return new ConjunctiveActivation(this, parent, actId, n, doc, bindingSignals);
            case DISJUNCTIVE:
                return new DisjunctiveActivation(this, parent, actId, n, doc, bindingSignals);
            case INHIBITORY:
                return new InhibitoryActivation(this, parent, actId, n, doc, bindingSignals);
            default:
                return null;
        }
    }

    public ActivationSubType getSubType() {
        return subType;
    }

    public ActivationDefinition setSubType(ActivationSubType subType) {
        this.subType = subType;
        return this;
    }

    public NeuronDefinition getNeuron() {
        return neuron != null ?
                neuron :
                getFromParent(p -> ((ActivationDefinition)p).getNeuron());
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
