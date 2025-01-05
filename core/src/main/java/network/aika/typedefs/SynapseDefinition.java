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

import network.aika.activations.Activation;
import network.aika.activations.Link;
import network.aika.bindingsignal.BSType;
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
public class SynapseDefinition extends Type<SynapseDefinition, Synapse> {

    public static final RelationSelf<ActivationDefinition, Activation> SELF = new RelationSelf<>(0, "SYN-SELF");

    public static final RelationOne<SynapseDefinition, Synapse, NeuronDefinition, Neuron> INPUT = new RelationOne<>(Synapse::getInput, 1, "SYN-INPUT");
    public static final RelationOne<SynapseDefinition, Synapse, NeuronDefinition, Neuron> OUTPUT = new RelationOne<>(Synapse::getOutput, 2, "SYN-OUTPUT");
    public static final RelationMany<SynapseDefinition, Synapse, LinkDefinition, Link> LINK = new RelationMany<>(null, 3, "SYN-LINK");

    public static final Relation[] RELATIONS = {INPUT, OUTPUT, LINK};

    static {
        LINK.setReversed(LinkDefinition.SYNAPSE);
        INPUT.setReversed(NeuronDefinition.OUTPUT);
        OUTPUT.setReversed(NeuronDefinition.INPUT);
    }

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
    public Relation<SynapseDefinition, Synapse, ?, ?>[] getRelationTypes() {
        return RELATIONS;
    }

    public Synapse instantiate() {
        return instantiate(
                List.of(SynapseDefinition.class),
                List.of(this)
        );
    }

    public Synapse instantiate(Neuron input, Neuron output) {
        assert input.getType().isInstanceOf(getInput());
        assert output.getType().isInstanceOf(getOutput());

        return instantiate(
                List.of(SynapseDefinition.class, Neuron.class, Neuron.class),
                List.of(this, input, output)
        );
    }

    public NeuronDefinition getInput() {
        return input != null ?
                input :
                getFromParent(SynapseDefinition::getInput);
    }

    public SynapseDefinition setInput(NeuronDefinition input) {
        assert input != null;

        this.input = input;

        return this;
    }

    public NeuronDefinition getOutput() {
        return output != null ?
                output :
                getFromParent(SynapseDefinition::getOutput);
    }

    public SynapseDefinition setOutput(NeuronDefinition outputDef) {
        assert outputDef != null;

        this.output = outputDef;

        return this;
    }

    public LinkDefinition getLink() {
        return link != null ?
                link :
                getFromParent(SynapseDefinition::getLink);
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
                getFromParent(SynapseDefinition::getTransition);
    }

    public SynapseDefinition setTransition(Transition... transition) {
        this.transition = transition;

        return this;
    }

    public Direction getStoredAt() {
        return storedAt != null ?
                storedAt :
                getFromParent(SynapseDefinition::getStoredAt);
    }

    public SynapseDefinition setStoredAt(Direction storedAt) {
        this.storedAt = storedAt;

        return this;
    }

    public Boolean isTrainingAllowed() {
        return trainingAllowed != null ?
                trainingAllowed :
                getFromParent(SynapseDefinition::isTrainingAllowed);
    }

    public SynapseDefinition setTrainingAllowed(boolean trainingAllowed) {
        this.trainingAllowed = trainingAllowed;

        return this;
    }

    public SynapseDefinition getInstanceSynapseType() {
        return instanceSynapseType != null ?
                instanceSynapseType :
                getFromParent(SynapseDefinition::getInstanceSynapseType);
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
