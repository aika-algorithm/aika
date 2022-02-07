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
package network.aika.lattice;


import network.aika.*;
import network.aika.lattice.activation.OrActivation;
import network.aika.lattice.refinement.OrEntry;
import network.aika.lattice.refinement.RefValue;
import network.aika.lattice.refinement.Refinement;
import network.aika.neuron.INeuron;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.*;
import network.aika.Document;
import network.aika.neuron.activation.link.Direction;
import network.aika.neuron.activation.link.Link;
import network.aika.neuron.relation.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static network.aika.neuron.Synapse.OUTPUT;


/**
 * While several neurons might share a the same input-node or and-node, there is always a always a one-to-one relation
 * between or-nodes and neurons. The only exceptions are the input neurons which have a one-to-one relation with the
 * input-node. The or-nodes form a disjunction of one or more input-nodes or and-nodes.
 *
 * @author Lukas Molzberger
 */
public class OrNode extends Node<OrNode, OrActivation> {

    private static final Logger log = LoggerFactory.getLogger(OrNode.class);

    TreeSet<OrEntry> andParents = new TreeSet<>();

    private Neuron outputNeuron = null;


    public OrNode() {}


    public OrNode(Model m) {
        super(m, -1); // Or-node activations always need to be processed first!
    }

    @Override
    public RefValue expand(int threadId, Document doc, Refinement ref) {
        throw new UnsupportedOperationException();
    }


    protected void addActivation(OrEntry oe, NodeActivation inputAct) {
        OrActivation.Link ol = new OrActivation.Link(oe, inputAct);

        Document doc = inputAct.getDocument();
        INeuron n = outputNeuron.get(doc);
        if(n == null) return;

        Activation act = lookupActivation(ol, l -> {
            Synapse s = l.getSynapse();
            if(!s.isIdentity()) return true;

            Integer i = oe.revSynapseIds.get(s.getId());
            Activation iAct = doc.getLinker().computeInputActivation(s, inputAct.getInputActivation(i));
            return i != null && l.getInput() == iAct;
        });

        if(act == null) {
            OrActivation orAct = new OrActivation(doc, this);
            register(orAct);

            act = new Activation(doc, n, getSlots(oe, inputAct));

            act.setInputNodeActivation(orAct);
            orAct.setOutputAct(act);
        }

        OrActivation orAct = act.getInputNodeActivation();
        propagate(orAct);
        orAct.link(ol);

        ol.linkOutputActivation(act);
    }


    private Activation lookupActivation(OrActivation.Link ol, Predicate<Link> filter) {
        for(Link l: ol.getInputLinks(outputNeuron)) {
            Synapse syn = l.getSynapse();

            Map<Integer, Relation> rels = syn.getRelations();
            for(Map.Entry<Integer, Relation> me: rels.entrySet()) {
                Integer relSynId = me.getKey();
                Relation rel = me.getValue();

                Activation existingAct = null;
                if(relSynId != OUTPUT) {
                    Synapse s = outputNeuron.getSynapseById(relSynId);
                    if (s != null) {
                        existingAct = rel
                                .invert()
                                .getActivations(s.getInput().get(), l.getInput())
                                .flatMap(act -> act.getLinksBySynapse(Direction.OUTPUT, s))
                                .map(rl -> rl.getOutput())
                                .findFirst()
                                .orElse(null);
                    }
                } else {
                    INeuron n = outputNeuron.get();
                    if(n == null) return null;

                    existingAct = rel
                            .invert()
                            .getActivations(n, l.getInput())
                            .findFirst()
                            .orElse(null);
                }

                if(existingAct != null && existingAct.match(filter)) {
                    return existingAct;
                }
            }
        }

        return null;
    }


    private SortedMap<Integer, Position> getSlots(OrEntry oe, NodeActivation inputAct) {
        SortedMap<Integer, Position> slots = new TreeMap<>();
        for(int i = 0; i < oe.synapseIds.length; i++) {
            int synapseId = oe.synapseIds[i];

            Synapse s = outputNeuron.getSynapseById(synapseId);
            if(s != null) {
                for (Map.Entry<Integer, Relation> me : s.getRelations().entrySet()) {
                    Relation rel = me.getValue();
                    if (me.getKey() == Synapse.OUTPUT) {
                        Activation iAct = inputAct.getInputActivation(i);
                        rel.mapSlots(slots, iAct);
                    }
                }
            }
        }
        return slots;
    }


    @Override
    protected void propagate(OrActivation act) {
        act.getDocument().getUpperBoundQueue().add(act.getOutputAct());
    }


    @Override
    public void cleanup() {

    }


    @Override
    public void reprocessInputs(Document doc) {
        for (OrEntry oe : andParents) {
            Node<?, NodeActivation<?>> pn = oe.parent.get();
            for (NodeActivation act : pn.getActivations(doc)) {
                act.repropagateV = markedCreated;
                act.getNode().propagate(act);
            }
        }
    }


    void addInput(int[] synapseIds, int threadId, Node in, boolean andMode) {
        in.changeNumberOfNeuronRefs(threadId, provider.getModel().visitedCounter.addAndGet(1), 1);

        OrEntry oe = new OrEntry(synapseIds, in.getProvider(), provider);
        in.addOrChild(oe);
        in.setModified();

        if(andMode) {
            lock.acquireWriteLock();
            setModified();
            andParents.add(oe);
            lock.releaseWriteLock();
        }
    }


    @Override
    public void delete(Set<String> modelLabels) {
        outputNeuron.get().remove();

        super.remove();

        try {
            lock.acquireReadLock();
            removeParents(modelLabels);
        } finally {
            lock.releaseReadLock();
        }
    }


    void removeParents(Set<String> modelLabels) {
        for (OrEntry oe : andParents) {
            Provider<? extends Node> pp = oe.parent;
            Node pn = pp.get();
            pn.changeNumberOfNeuronRefs(provider.getModel().defaultThreadId, provider.getModel().visitedCounter.addAndGet(1), -1);
            pn.removeOrChild(oe);
            pn.setModified();

//            pp.delete(modelLabels);
        }
        andParents.clear();
    }

    void removeParents(int threadId) {
        for (OrEntry oe : andParents) {
            Node pn = oe.parent.get();
            pn.changeNumberOfNeuronRefs(threadId, provider.getModel().visitedCounter.addAndGet(1), -1);
            pn.removeOrChild(oe);
            pn.setModified();
        }
        andParents.clear();
    }


    @Override
    protected void changeNumberOfNeuronRefs(int threadId, long v, int d) {
        throw new UnsupportedOperationException();
    }


    public String logicToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OR[");
        boolean first = true;
        int i = 0;
        for(OrEntry oe : andParents) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(oe.parent.get().logicToString());
            if (i > 2) {
                sb.append(",...");
                break;
            }

            i++;
        }

        sb.append("]");
        return sb.toString();
    }


    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(false);
        out.writeChar('O');
        super.write(out);

        out.writeInt(outputNeuron.getId());

        out.writeInt(andParents.size());
        for(OrEntry oe: andParents) {
            oe.write(out);
        }
    }


    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        outputNeuron = m.lookupNeuron(in.readInt());

        int s = in.readInt();
        for(int i = 0; i < s; i++) {
            andParents.add(OrEntry.read(in, m));
        }
    }


    public String getNeuronLabel() {
        String l = outputNeuron.getLabel();
        return l != null ? l : "";
    }


    public void setOutputNeuron(Neuron n) {
        outputNeuron = n;
    }

    public Neuron getOutputNeuron() {
        return outputNeuron;
    }
}
