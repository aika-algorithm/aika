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
package network.aika.elements.synapses.slots;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.SynapseSlotDefinition;
import network.aika.enums.direction.Direction;
import network.aika.fields.Field;
import network.aika.fields.MaxField;
import network.aika.fields.ObjImpl;
import network.aika.fields.link.FieldLink;
import network.aika.queue.Queue;

import java.util.stream.Stream;

import static network.aika.elements.typedef.FieldTags.OUTPUT_SLOT;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseSlot extends ObjImpl<SynapseSlotDefinition, SynapseSlot> {


    protected Activation activation;

    protected Synapse synapse;

    public SynapseSlot(SynapseSlotDefinition type, Activation activation, Synapse synapse) {
        this.type = type;
        this.activation = activation;
        this.synapse = synapse;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public void setSynapse(Synapse synapse) {
        this.synapse = synapse;
    }

    public Synapse getSynapse() {
        return synapse;
    }

    public Direction getDirection() {
        return getType().getDirection();
    }

    public Stream<Link> getLinks() {
        Field<SynapseSlot, ?, ?> f = getSlotField();
        return f.getInputs()
                .getInputs()
                .map(SynapseSlot::extractLinkFromFieldLink);
    }

    private static ConjunctiveLink extractLinkFromFieldLink(FieldLink fl) {
        return (ConjunctiveLink) fl.getInput().getObject();
    }

    private Field<SynapseSlot, ?, ?> getSlotField() {
        return getField(OUTPUT_SLOT);
    }

    public Link getLink(Activation act) {
        return getSlotField();
    }

    public Link getSelectedLink() {
        return null;
    }


    @Override
    public Queue getQueue() {
        return activation.getQueue();
    }

    @Override
    public boolean isNextRound() {
        return false;
    }

    @Override
    public String toKeyString() {
        return "syn-slot:" + activation.toKeyString() + ":" + synapse.toKeyString();
    }
}
