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

import network.aika.activations.*;
import network.aika.bindingsignal.BSType;
import network.aika.neurons.ConjunctiveSynapse;
import network.aika.neurons.DisjunctiveSynapse;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;
import network.aika.neurons.Neuron;
import network.aika.neurons.Synapse;
import network.aika.bindingsignal.Transition;
import network.aika.misc.direction.Direction;
import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.RelationSelf;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseDefinition extends Type {

    public enum SynapseSubType {
        CONJUNCTIVE,
        DISJUNCTIVE
    }

    public static final RelationSelf SELF = new RelationSelf(0, "SYN-SELF");

    public static final RelationOne INPUT = new RelationOne(1, "SYN-INPUT");
    public static final RelationOne OUTPUT = new RelationOne(2, "SYN-OUTPUT");
    public static final RelationMany LINK = new RelationMany(3, "SYN-LINK");

    public static final Relation[] RELATIONS = {SELF, INPUT, OUTPUT, LINK};

    static {
        LINK.setReversed(LinkDefinition.SYNAPSE);
        INPUT.setReversed(NeuronDefinition.OUTPUT);
        OUTPUT.setReversed(NeuronDefinition.INPUT);
    }

    SynapseSubType subType;

    private LinkDefinition link;

    private NeuronDefinition input;
    private NeuronDefinition output;

    private Transition[] transition;

    private Direction storedAt;

    private Boolean trainingAllowed;

    private SynapseDefinition instanceSynapseType;

    public SynapseDefinition(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation[] getRelations() {
        return RELATIONS;
    }

    public Synapse instantiate() {
        switch (subType) {
            case CONJUNCTIVE:
                return new ConjunctiveSynapse(this);
            case DISJUNCTIVE:
                return new DisjunctiveSynapse(this);

            default:
                return null;
        }
    }

    public Synapse instantiate(Neuron input, Neuron output) {
        assert input.getType().isInstanceOf(getInput());
        assert output.getType().isInstanceOf(getOutput());

        switch (subType) {
            case CONJUNCTIVE:
                return new ConjunctiveSynapse(this, input, output);
            case DISJUNCTIVE:
                return new DisjunctiveSynapse(this, input, output);
            default:
                return null;
        }
    }

    public SynapseSubType getSubType() {
        return subType;
    }

    public SynapseDefinition setSubType(SynapseSubType subType) {
        this.subType = subType;
        return this;
    }

    public NeuronDefinition getInput() {
        return input != null ?
                input :
                getFromParent(p -> ((SynapseDefinition)p).getInput());
    }

    public SynapseDefinition setInput(NeuronDefinition input) {
        assert input != null;

        this.input = input;

        return this;
    }

    public NeuronDefinition getOutput() {
        return output != null ?
                output :
                getFromParent(p -> ((SynapseDefinition)p).getOutput());
    }

    public SynapseDefinition setOutput(NeuronDefinition outputDef) {
        assert outputDef != null;

        this.output = outputDef;

        return this;
    }

    public LinkDefinition getLink() {
        return link != null ?
                link :
                getFromParent(p -> ((SynapseDefinition)p).getLink());
    }

    public SynapseDefinition setLink(LinkDefinition link) {
        assert link != null;

        this.link = link;

        return this;
    }

    public boolean isIncomingLinkingCandidate(Set<BSType> BSTypes) {
        for (Transition t : getTransition()) {
            if (BSTypes.contains(t.to()))
                return true;
        }
        return false;
    }

    public boolean isOutgoingLinkingCandidate(Set<BSType> BSTypes) {
        for (Transition t : getTransition()) {
            if (BSTypes.contains(t.from()))
                return true;
        }
        return false;
    }

    public BSType mapTransitionForward(BSType bsType) {
        for (Transition t : getTransition()) {
            if(t.from() == bsType)
                return t.to();
        }
        return null;
    }

    public BSType mapTransitionBackward(BSType bsType) {
        for (Transition t : getTransition()) {
            if(t.to() == bsType)
                return t.from();
        }
        return null;
    }

    public Transition[] getTransition() {
        return transition != null ?
                transition :
                getFromParent(p -> ((SynapseDefinition)p).getTransition());
    }

    public SynapseDefinition setTransition(Transition... transition) {
        this.transition = transition;

        return this;
    }

    public Direction getStoredAt() {
        return storedAt != null ?
                storedAt :
                getFromParent(p -> ((SynapseDefinition)p).getStoredAt());
    }

    public SynapseDefinition setStoredAt(Direction storedAt) {
        this.storedAt = storedAt;

        return this;
    }

    public SynapseDefinition setTrainingAllowed(boolean trainingAllowed) {
        this.trainingAllowed = trainingAllowed;

        return this;
    }

    public SynapseDefinition getInstanceSynapseType() {
        return instanceSynapseType != null ?
                instanceSynapseType :
                getFromParent(p -> ((SynapseDefinition)p).getInstanceSynapseType());
    }

    public SynapseDefinition setInstanceSynapseType(SynapseDefinition instanceSynapseType) {
        this.instanceSynapseType = instanceSynapseType;

        return this;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getInput().getName() + " --> " + getOutput().getName() + ")";
    }
}
