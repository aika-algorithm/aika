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
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.SynapseSlotDefinition;
import network.aika.elements.typedef.Type;
import network.aika.enums.direction.Direction;
import network.aika.queue.Queue;

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class SynapseSlot extends Type<SynapseSlotDefinition, SynapseSlot> {


    protected Activation activation;

    protected Synapse synapse;

    public SynapseSlot(Activation activation, Synapse synapse) {
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
        return typeDef.getDirection();
    }

    public abstract void addLink(Link l);

    public abstract Stream<Link> getLinks();

    public abstract Link getLink(Activation act);

    public abstract Link getSelectedLink();


    @Override
    public Queue getQueue() {
        return activation.getQueue();
    }

    @Override
    public boolean isNextRound() {
        return false;
    }
}
