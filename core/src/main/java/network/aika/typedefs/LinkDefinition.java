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
import network.aika.type.relations.RelationTypeOne;
import network.aika.neurons.Synapse;
import network.aika.type.Type;
import network.aika.type.TypeRegistry;

import java.util.List;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkDefinition extends Type<LinkDefinition, Link> {

    public static RelationTypeOne<LinkDefinition, Link, ActivationDefinition, Activation> INPUT = new RelationTypeOne<>(Link::getInput, "LINK-INPUT");
    public static RelationTypeOne<LinkDefinition, Link, ActivationDefinition, Activation> OUTPUT = new RelationTypeOne<>(Link::getOutput, "LINK-OUTPUT");
    public static RelationTypeOne<LinkDefinition, Link, SynapseDefinition, Synapse> SYNAPSE = new RelationTypeOne<>(Link::getSynapse, "LINK-SYNAPSE");

    public static RelationTypeOne<LinkDefinition, Link, LinkDefinition, Link> CORRESPONDING_INPUT_LINK = new RelationTypeOne<>(Link::getCorrespondingInputLink, "LINK-CORRESPONDING-INPUT-LINK");
    public static RelationTypeOne<LinkDefinition, Link, LinkDefinition, Link> CORRESPONDING_OUTPUT_LINK = new RelationTypeOne<>(Link::getCorrespondingOutputLink, "LINK-CORRESPONDING-INPUT-LINK");

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
