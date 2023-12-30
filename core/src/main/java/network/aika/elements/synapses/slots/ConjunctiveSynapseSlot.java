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
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.enums.direction.Direction;
import network.aika.fields.MaxField;
import network.aika.fields.link.ArgumentFieldLink;
import network.aika.fields.link.FieldLink;
import network.aika.queue.steps.LinkUpdate;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveSynapseSlot<S extends ConjunctiveSynapse, L extends ConjunctiveLink> implements SynapseSlot<S, L> {

    protected Activation act;

    protected S synapse;

    protected Direction dir;

    protected MaxField maxField;

    protected NavigableMap<Activation, L> links;

    public ConjunctiveSynapseSlot(Activation act, S synapse, Direction dir) {
        this.act = act;
        this.synapse = synapse;
        this.dir = dir;

        links = new TreeMap<>();
        maxField = new MaxField(this, getLabel(), TOLERANCE, (si, state) -> {
            if(si == null)
                return;

            LinkUpdate.add(
                    getLink(si),
                    dir,
                    state
            );
        });
    }

    public MaxField getMaxField() {
        return maxField;
    }

    @Override
    public MaxField getInputField() {
        return maxField;
    }

    @Override
    public MaxField getOutputField() {
        return maxField;
    }

    @Override
    public void addLink(L l) {
        Activation keyAct = dir.invert().getActivation(l);
        if(keyAct == null)
            return;

        L el = links.put(keyAct, l);
        assert el == null;
    }

    @Override
    public Stream<L> getLinks() {
        return links.values().stream();
    }

    @Override
    public L getLink(Activation act) {
        return links.get(act);
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

    protected abstract String getLabel();

    @Override
    public L getSelectedLink() {
        return getLink(maxField.getSelectedInput());
    }

    private L getLink(FieldLink fl) {
        ArgumentFieldLink afl = (ArgumentFieldLink) fl;
        if(afl == null)
            return null;

        return (L) afl.getArgumentRef();
    }

    @Override
    public void disconnect() {
        maxField.disconnectAndUnlinkOutputs(false);
    }

    @Override
    public Document getDocument() {
        return act.getDocument();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " Act-Id:" + act.getId() + " Act-Label:" + act.getLabel() + " Syn:" + synapse;
    }
}
