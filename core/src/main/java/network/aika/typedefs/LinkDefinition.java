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
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;
import network.aika.neurons.Synapse;
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
public class LinkDefinition extends Type<LinkDefinition, Link> {

    public static final RelationSelf<ActivationDefinition, Activation> SELF = new RelationSelf<>(0, "LINK-SELF");

    public static final RelationOne<LinkDefinition, Link, ActivationDefinition, Activation> INPUT = new RelationOne<>(Link::getInput, 1, "LINK-INPUT");
    public static final RelationOne<LinkDefinition, Link, ActivationDefinition, Activation> OUTPUT = new RelationOne<>(Link::getOutput, 2, "LINK-OUTPUT");
    public static final RelationOne<LinkDefinition, Link, SynapseDefinition, Synapse> SYNAPSE = new RelationOne<>(Link::getSynapse, 3, "LINK-SYNAPSE");

    public static final RelationOne<LinkDefinition, Link, LinkDefinition, Link> CORRESPONDING_INPUT_LINK = new RelationOne<>(l -> l.getInput().getCorrespondingInputLink(l), 4, "CORRESPONDING_INPUT_LINK");
    public static final RelationOne<LinkDefinition, Link, LinkDefinition, Link> CORRESPONDING_OUTPUT_LINK = new RelationOne<>(l -> l.getOutput().getCorrespondingOutputLink(l), 5, "CORRESPONDING_OUTPUT_LINK");

    public static final Relation[] RELATIONS = {INPUT, OUTPUT, SYNAPSE, CORRESPONDING_INPUT_LINK, CORRESPONDING_OUTPUT_LINK};

    static {
        SYNAPSE.setReversed(SynapseDefinition.LINK);
        INPUT.setReversed(ActivationDefinition.OUTPUT);
        OUTPUT.setReversed(ActivationDefinition.INPUT);
    }

    private SynapseDefinition synapse;

    private ActivationDefinition input;
    private ActivationDefinition output;


    public LinkDefinition(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation<LinkDefinition, Link, ?, ?>[] getRelations() {
        return RELATIONS;
    }

    public Link instantiate(Synapse synapse, Activation input, Activation output) {
        assert input.getType().isInstanceOf(getInput());
        assert output.getType().isInstanceOf(getOutput());

        return instantiate(
                List.of(LinkDefinition.class, Synapse.class, Activation.class, Activation.class),
                List.of(this, synapse, input, output)
        );
    }

    public SynapseDefinition getSynapse() {
        return synapse != null ?
                synapse :
                getFromParent(LinkDefinition::getSynapse);
    }

    public LinkDefinition setSynapse(SynapseDefinition synapse) {
        assert synapse != null;

        this.synapse = synapse;

        return this;
    }

    public ActivationDefinition getInput() {
        return input != null ?
                input :
                getFromParent(LinkDefinition::getInput);
    }

    public LinkDefinition setInput(ActivationDefinition input) {
        assert input != null;

        this.input = input;

        return this;
    }

    public ActivationDefinition getOutput() {
        return output != null ?
                output :
                getFromParent(LinkDefinition::getOutput);
    }

    public LinkDefinition setOutput(ActivationDefinition output) {
        assert output != null;

        this.output = output;
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getInput().getName() + " --> " + getOutput().getName() + ")";
    }
}
