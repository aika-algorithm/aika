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
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.SynapseSlotTypeDefinition;
import network.aika.elements.typedef.SynapseTypeDefinition;
import network.aika.elements.typedef.Type;
import network.aika.enums.direction.Direction;
import network.aika.fields.AbstractMaxField;
import network.aika.fields.link.ArgumentFieldLink;
import network.aika.queue.Queue;
import network.aika.utils.ApproximateComparisonValueUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Comparator.*;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class ConjunctiveSynapseSlot extends SynapseSlot {

    public ConjunctiveSynapseSlot(Activation act, Synapse synapse, String label, Direction dir) {
        super(null, label, TOLERANCE);

        this.act = act;
        this.synapse = synapse;
        this.dir = dir;
    }

    @Override
    protected void updateSelectedInput(ArgumentFieldLink<LinkKey, Link> si, boolean state) {
        if (si == null)
            return;

        si.getArgumentRef()
                .setState(
                        dir,
                        state
                );
    }

    @Override
    public ConjunctiveSynapseSlot getReference() {
        return this;
    }

    @Override
    protected boolean isNegativeInputAllowed() {
        return dir == INPUT;
    }

    @Override
    public Comparator<ArgumentFieldLink<Link>> getComparator() {
        Comparator<ArgumentFieldLink<Link>> valueComp = comparingInt(fl ->
                ApproximateComparisonValueUtil.convert(fl.getUpdatedInputValue())
        );
        Comparator<ArgumentFieldLink<Link>> firedComp = comparing(fl -> fl.getArgumentRef().getFired());
        return valueComp.thenComparing(firedComp.reversed());
    }

    @Override
    public synchronized Stream<Link> getLinks() {
        return getInputs()
                .stream()
                .map(ArgumentFieldLink::getArgumentRef);
    }

    @Override
    public Link getLink(Activation act) {
        ArgumentFieldLink<Link> fl = links.get(
                new LinkKey(
                        act.getNeuron(),
                        act
                )
        );

        return getLink(fl);
    }

    private Link getLink(ArgumentFieldLink<Link> fl) {
        return fl != null ?
                fl.getArgumentRef() :
                null;
    }

    @Override
    public Link getSelectedLink() {
        return getLink(getSelectedInput());
    }

    @Override
    public void disconnect() {
        unlinkInputs();

        getLinks()
                .forEach(Link::disconnect);
    }

    @Override
    public Queue getQueue() {
        return act.getQueue();
    }


    @Override
    public void addLink(Link l) {
    }

    @Override
    public void addInput(ArgumentFieldLink<Link> fl) {
        Link l = fl.getArgumentRef();

        Direction d = dir.invert();
        ArgumentFieldLink<Link> el = links.put(
                new LinkKey(
                        d.getNeuron(l.getSynapse()),
                        d.getActivation(l)
                ),
                fl
        );
        assert el == null;
    }

    @Override
    public void removeInput(ArgumentFieldLink<Link> fl) {
        links.remove(
                new LinkKey(fl, dir)
        );
    }

    @Override
    public Collection<ArgumentFieldLink<Link>> getInputs() {
        return links.values();
    }

    @Override
    public int size() {
        return links.size();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " Act-Id:" + act.getId() + " Act-Label:" + act.getLabel() + " Syn:" + synapse;
    }
}
