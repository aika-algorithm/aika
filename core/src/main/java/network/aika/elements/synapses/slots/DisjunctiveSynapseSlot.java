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

import network.aika.Document;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.fields.Field;

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class DisjunctiveSynapseSlot implements SynapseSlot {

    protected Activation act;

    protected DisjunctiveSynapse synapse;

    private Link link;

    public DisjunctiveSynapseSlot(Activation act, DisjunctiveSynapse syn) {
        this.act = act;
        this.synapse = syn;
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
    public void disconnect() {

    }

    @Override
    public Document getDocument() {
        return act.getDocument();
    }
}
