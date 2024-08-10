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

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class ConjunctiveSynapseSlot extends SynapseSlot {


    public ConjunctiveSynapseSlot(SynapseSlotDefinition type, Activation activation, Synapse synapse) {
        super(type, activation, synapse);
    }

    @Override
    public Link getLink(Activation act) {
        return null;
    }

    @Override
    public Link getSelectedLink() {
       return null;
    }


    @Override
    public void addLink(Link l) {
    }

    @Override
    public Stream<Link> getLinks() {
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " Act-Id:" + activation.getId() + " Act-Label:" + activation.getLabel() + " Syn:" + synapse;
    }
}
