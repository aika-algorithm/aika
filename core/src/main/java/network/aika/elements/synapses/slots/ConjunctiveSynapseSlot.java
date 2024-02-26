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
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.enums.direction.Direction;
import network.aika.fields.AbstractMaxField;
import network.aika.fields.link.AbstractFieldLink;
import network.aika.fields.link.ArgumentFieldLink;
import network.aika.queue.Queue;
import network.aika.queue.steps.LinkUpdate;

import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveSynapseSlot<S extends ConjunctiveSynapse, L extends ConjunctiveLink> extends AbstractMaxField<ArgumentFieldLink<L>> implements SynapseSlot<S, L> {

    protected Activation act;

    protected S synapse;

    protected Direction dir;

    protected NavigableMap<LinkKey, ArgumentFieldLink<L>> links;

    public ConjunctiveSynapseSlot(Activation act, S synapse, String label, Direction dir) {
        super(null,
                label,
                TOLERANCE,
                (si, state) -> {
                    if (si == null)
                        return;

                    LinkUpdate.add(
                            si.getArgumentRef(),
                            dir,
                            state
                    );
                });

        this.act = act;
        this.synapse = synapse;
        this.dir = dir;

        links = new TreeMap<>();
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
    public AbstractMaxField getInputField() {
        return this;
    }

    @Override
    public AbstractMaxField getOutputField() {
        return this;
    }

    @Override
    public Comparator<ArgumentFieldLink<L>> getComparator() {
        Comparator<ArgumentFieldLink<L>> valueComp = comparingDouble(AbstractFieldLink::getUpdatedInputValue);
        Comparator<ArgumentFieldLink<L>> firedComp = comparing(fl -> fl.getArgumentRef().getFired());
        return valueComp.thenComparing(firedComp.reversed());
    }

    @Override
    public Stream<L> getLinks() {
        return getInputs()
                .stream()
                .map(ArgumentFieldLink::getArgumentRef);
    }

    @Override
    public L getLink(Activation act) {
        ArgumentFieldLink<L> fl = links.get(
                new LinkKey(
                        act.getNeuron(),
                        act
                )
        );

        return getLink(fl);
    }

    private L getLink(ArgumentFieldLink<L> fl) {
        return fl != null ?
                fl.getArgumentRef() :
                null;
    }

    @Override
    public L getSelectedLink() {
        return getLink(getSelectedInput());
    }

    @Override
    public S getSynapse() {
        return synapse;
    }

    @Override
    public Activation getActivation() {
        return act;
    }

    @Override
    public Direction getDirection() {
        return dir;
    }

    @Override
    public void disconnect() {
        disconnectAndUnlinkInputs(false);

        getLinks()
                .forEach(ConjunctiveLink::disconnect);
    }

    @Override
    public Queue getQueue() {
        return act.getQueue();
    }


    @Override
    public void addLink(L l) {
    }

    @Override
    public void addInput(ArgumentFieldLink<L> fl) {
        L l = fl.getArgumentRef();

        Direction d = dir.invert();
        ArgumentFieldLink<L> el = links.put(
                new LinkKey(
                        d.getNeuron(l.getSynapse()),
                        d.getActivation(l)
                ),
                fl
        );
        assert el == null;
    }

    @Override
    public void removeInput(ArgumentFieldLink<L> fl) {
        links.remove(
                new LinkKey(fl, dir)
        );
    }

    @Override
    public Collection<ArgumentFieldLink<L>> getInputs() {
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
