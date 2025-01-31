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
public class LinkDefinition extends Type {

    public static final RelationSelf SELF = new RelationSelf(0, "LINK-SELF");

    public static final RelationOne INPUT = new RelationOne(1, "LINK-INPUT");
    public static final RelationOne OUTPUT = new RelationOne(2, "LINK-OUTPUT");
    public static final RelationOne SYNAPSE = new RelationOne(3, "LINK-SYNAPSE");

    public static final RelationOne CORRESPONDING_INPUT_LINK = new RelationOne(4, "CORRESPONDING_INPUT_LINK");
    public static final RelationOne CORRESPONDING_OUTPUT_LINK = new RelationOne(5, "CORRESPONDING_OUTPUT_LINK");

    public static final Relation[] RELATIONS = {SELF, INPUT, OUTPUT, SYNAPSE, CORRESPONDING_INPUT_LINK, CORRESPONDING_OUTPUT_LINK};

    static {
        SYNAPSE.setReversed(SynapseDefinition.LINK);
        INPUT.setReversed(ActivationDefinition.OUTPUT);
        OUTPUT.setReversed(ActivationDefinition.INPUT);
        CORRESPONDING_INPUT_LINK.setReversed(CORRESPONDING_OUTPUT_LINK);
        CORRESPONDING_OUTPUT_LINK.setReversed(CORRESPONDING_INPUT_LINK);
    }

    private SynapseDefinition synapse;

    private ActivationDefinition input;
    private ActivationDefinition output;


    public LinkDefinition(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation[] getRelations() {
        return RELATIONS;
    }

    public Link instantiate(Synapse synapse, Activation input, Activation output) {
        assert input.getType().isInstanceOf(getInput());
        assert output.getType().isInstanceOf(getOutput());

        return new Link(this, synapse, input, output);
    }

    public SynapseDefinition getSynapse() {
        return synapse != null ?
                synapse :
                getFromParent(p -> ((LinkDefinition)p).getSynapse());
    }

    public LinkDefinition setSynapse(SynapseDefinition synapse) {
        assert synapse != null;

        this.synapse = synapse;

        return this;
    }

    public ActivationDefinition getInput() {
        return input != null ?
                input :
                getFromParent(p -> ((LinkDefinition)p).getInput());
    }

    public LinkDefinition setInput(ActivationDefinition input) {
        assert input != null;

        this.input = input;

        return this;
    }

    public ActivationDefinition getOutput() {
        return output != null ?
                output :
                getFromParent(p -> ((LinkDefinition)p).getOutput());
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
