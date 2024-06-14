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
import network.aika.elements.links.DisjunctiveLink;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.SynapseSlotTypeDefinition;
import network.aika.elements.typedef.SynapseTypeDefinition;
import network.aika.enums.direction.Direction;
import network.aika.fields.Field;
import network.aika.queue.Queue;

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */

public class DisjunctiveSynapseSlot implements SynapseSlot<DisjunctiveSynapse, DisjunctiveLink> {

    private Direction dir;

    private Link link;

    public DisjunctiveSynapseSlot(Direction dir) {
        this.dir = dir;
    }

    @Override
    public void init() {

    }

    @Override
    public void addLink(Link l) {
        link = l;
    }

    @Override
    public Stream<Link> getLinks() {
        return Stream.of(link);
    }

    @Override
    public Link getLink(Activation act) {
        return link;
    }

    @Override
    public Synapse getSynapse() {
        return link.getSynapse();
    }

    @Override
    public Activation getActivation() {
        return dir.getActivation(link);
    }

    @Override
    public Direction getDirection() {
        return dir;
    }

    @Override
    public Field getInputField() {
        return link.getSynapse().getOutputNet(link.getOutput());
    }

    @Override
    public Field getOutputField() {
        return link.getWeightedInput();
    }

    @Override
    public Link getSelectedLink() {
        return link;
    }

    @Override
    public Queue getQueue() {
        return link.getQueue();
    }
}
