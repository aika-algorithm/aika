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


import network.aika.Document;
import network.aika.Model;
import network.aika.Provider;
import network.aika.lattice.activation.AndActivation;
import network.aika.lattice.activation.InputActivation;
import network.aika.lattice.refinement.RefValue;
import network.aika.lattice.refinement.Refinement;
import network.aika.lattice.refinement.RelationsMap;
import network.aika.neuron.relation.Relation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;


/**
 * The {@code InputNode} and the {@code AndNode} classes together form a pattern lattice, containing all
 * possible substructures of any given conjunction. For example if we have the conjunction ABCD where A, B, C, D are
 * the inputs, then the pattern lattice will contain the nodes ABCD, ABC, ABD, ACD, BCD, AB, AC, AD, BC, BD, CD,
 * A, B, C, D. The pattern lattice is organized in layers, where each layer only contains conjunctions/patterns of the
 * same size. These layers are connected through refinements. For example the and-node
 * ABD on layer 3 is connected to the and-node ABCD on layer 4 via the refinement C.
 *
 * @author Lukas Molzberger
 */
public class AndNode extends Node<AndNode, AndActivation> {

    public List<Entry> parents;


    public AndNode() {
        parents = new ArrayList<>();
    }


    public AndNode(Model m, int level, List<Entry> parents) {
        super(m, level);
        this.parents = parents;
    }


    private void init() {
        for(Entry e: parents) {
            e.rv.child = provider;
            Node pn = e.rv.parent.get();

            pn.addAndChild(e.ref, e.rv);
            pn.setModified();
        }
    }

    @Override
    protected void propagate(AndActivation act) {
        if (andChildren != null) {
            for (AndActivation.Link fl : act.inputs) {
                if(fl == null) continue;

                NodeActivation<?> pAct = fl.input;

                for (AndActivation.Link sl : pAct.outputsToAndNode.values()) {
                    NodeActivation secondAct = sl.output;
                    if (act != secondAct) {
                        applyIntern(act, fl.refAct, fl.ref, fl.rv, secondAct, sl.refAct, sl.ref, sl.rv);
                    }
                }
            }
        }

        propagateToOrNode(act);
    }


    @Override
    public void cleanup() {
        if(!isRemoved && !isRequired()) {
            remove();

            for(Entry e: parents) {
                e.rv.parent.get().cleanup();
            }
        }
    }


    @Override
    void processActivation(AndActivation act) {
        if(act.isComplete()) {
            super.processActivation(act);
        }
    }



    private void applyIntern(AndActivation act, InputActivation refAct, Refinement ref, RefValue rv, NodeActivation secondAct, InputActivation secondRefAct, Refinement secondRef, RefValue secondRv) {
        Document doc = act.getDocument();

        lock.acquireReadLock();
        for(Map.Entry<Refinement, RefValue> me: andChildren.subMap(
                new Refinement(RelationsMap.MIN, secondRef.input),
                new Refinement(RelationsMap.MAX, secondRef.input)).entrySet()) {
            Refinement nRef = me.getKey();
            RefValue nRv = me.getValue();
            if(nRef.contains(secondRef, rv)) {
                AndNode nlNode = nRv.child.get(doc);

                AndActivation nlAct = lookupAndActivation(act, nRef);

                if(nlAct == null) {
                    nlAct = new AndActivation(doc, nlNode);
                    nlAct.link(nRef, nRv, secondRefAct, act);
                }

                nlAct.getNode().addActivation(nlAct);

                for(Entry secondNE: nlNode.parents) {
                    if(secondNE.rv.parent.get(doc) == secondAct.getNode() && secondNE.ref.contains(ref, secondRv)) {
                        nlAct.link(secondNE.ref, secondNE.rv, refAct, secondAct);
                        break;
                    }
                }
            }
        }
        lock.releaseReadLock();
    }


    private AndActivation lookupAndActivation(NodeActivation<?> input, Refinement ref) {
        for (AndActivation.Link l : input.outputsToAndNode.values()) {
            if(l.ref.compareTo(ref) == 0) {
                return l.output;
            }
        }
        return null;
    }


    RefValue expand(Document doc, Refinement firstRef) {
        if(!firstRef.isConvertible()) return null;

        RefValue firstRV = getAndChild(firstRef);
        if(firstRV != null) {
            return firstRV;
        }

        int firstRefOffset = level;
        Integer[] firstOffsets = new Integer[level];
        for(int i = 0; i < firstOffsets.length; i++) {
            firstOffsets[i] = i;
        }

        List<Entry> nextLevelParents = new ArrayList<>();

        for(Entry firstParent: parents) {
            Node parentNode = firstParent.rv.parent.get(doc);

            Refinement secondParentRef = new Refinement(getParentRelations(firstRef, firstParent), firstRef.input);
            RefValue secondParentRV = parentNode.expand(doc, secondParentRef);

            if(secondParentRV == null) {
                continue;
            }

            Refinement secondRef = new Refinement(getRelations(firstRef, firstParent, secondParentRV), firstParent.ref.input);
            RefValue secondRV = new RefValue(getOffsets(firstRefOffset, firstParent, secondParentRV), firstOffsets[firstParent.rv.refOffset], secondParentRV.child);

            nextLevelParents.add(new Entry(secondRef, secondRV));
        }

        firstRV = new RefValue(firstOffsets, firstRefOffset, provider);
        nextLevelParents.add(new Entry(firstRef, firstRV));

        return createAndNode(provider.getModel(), doc, nextLevelParents, level + 1) ? firstRV : null;
    }


    private RelationsMap getParentRelations(Refinement firstRef, Entry firstParent) {
        Relation[] secondParentRelations = new Relation[firstRef.relations.length() - 1];
        for(int i = 0; i < firstRef.relations.length(); i++) {
            Integer j = firstParent.rv.reverseOffsets[i];
            if(j != null) {
                secondParentRelations[j] = firstRef.relations.get(i);
            }
        }
        return new RelationsMap(secondParentRelations);
    }


    private static RelationsMap getRelations(Refinement firstRef, Entry firstParent, RefValue secondParentRV) {
        Relation[] secondRelations = new Relation[firstParent.ref.relations.length() + 1];
        for(int i = 0; i < firstParent.ref.relations.length(); i++) {
            int j = secondParentRV.offsets[i];
            secondRelations[j] = firstParent.ref.relations.get(i);
        }

        Relation rel = firstRef.relations.get(firstParent.rv.refOffset);
        if(rel != null) {
            secondRelations[secondParentRV.refOffset] = rel.invert();
        }
        return new RelationsMap(secondRelations);
    }


    private static Integer[] getOffsets(int firstRefOffset, Entry firstParent, RefValue secondParentRV) {
        Integer[] secondOffsets = new Integer[secondParentRV.offsets.length + 1];
        for(int i = 0; i < firstParent.rv.reverseOffsets.length; i++) {
            Integer j = firstParent.rv.reverseOffsets[i];
            if(j != null) {
                secondOffsets[secondParentRV.offsets[j]] = i;
            }
        }
        secondOffsets[secondParentRV.refOffset] = firstRefOffset;
        return secondOffsets;
    }


    static boolean createAndNode(Model m, Document doc, List<Entry> parents, int level) {
        if (parents != null) {
            // Locking needs to take place in a predefined order.
            TreeSet<Provider<? extends Node>> parentsForLocking = new TreeSet();
            for(Entry e: parents) {
                parentsForLocking.add(e.rv.parent);
            }

            for (Provider<? extends Node> pn : parentsForLocking) {
                pn.get().lock.acquireWriteLock();
            }
            try {
                AndNode nln = new AndNode(m, level, parents);

                nln.init();
                nln.postCreate(doc);
            } finally {
                for (Provider<? extends Node> pn : parentsForLocking) {
                    pn.get().lock.releaseWriteLock();
                }
            }
        }

        return true;
    }


    @Override
    public void changeNumberOfNeuronRefs(Document doc, long v, int d) {
        super.changeNumberOfNeuronRefs(doc, v, d);

        parents.forEach(e -> e.rv.parent.get().changeNumberOfNeuronRefs(doc, v, d));
    }


    @Override
    public void reprocessInputs(Document doc) {
        for(Entry e: parents) {
            Node<?, NodeActivation<?>> pn = e.rv.parent.get();
            for(NodeActivation act : pn.getActivations(doc)) {
                act.repropagateV = markedCreated;
                act.getNode().propagate(act);
            }
        }
    }


    @Override
    public void remove() {
        super.remove();

        for(Entry e: parents) {
            Node pn = e.rv.parent.get();
            pn.lock.acquireWriteLock();
            pn.removeAndChild(e.ref);
            pn.setModified();
            pn.lock.releaseWriteLock();
        }
    }


    public String logicToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AND(" + level + ")[");
        boolean first = true;
        for(Entry e: parents) {
            if(!first) {
                sb.append(",");
            }
            first = false;
            sb.append(e.ref);
        }
        sb.append("]");
        return sb.toString();
    }


    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(false);
        out.writeChar('A');
        super.write(out);

        out.writeInt(parents.size());
        for(Entry e: parents) {
            e.ref.write(out);
            e.rv.write(out);
        }
    }


    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        int s = in.readInt();
        for(int i = 0; i < s; i++) {
            Refinement ref = Refinement.read(in, m);
            RefValue rv = RefValue.read(in, m);
            parents.add(new Entry(ref, rv));
        }
    }


    public static class Entry {
        public Refinement ref;
        public RefValue rv;

        public Entry(Refinement ref, RefValue rv) {
            this.ref = ref;
            this.rv = rv;
        }
    }
}
