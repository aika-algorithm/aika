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
import network.aika.lattice.refinement.OrEntry;
import network.aika.lattice.refinement.RefValue;
import network.aika.lattice.refinement.Refinement;
import network.aika.neuron.INeuron;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The {@code Node} class is the abstract class for all the boolean logic nodes underneath the neural network layer.
 * These nodes form a boolean representation for all the neurons of the neural network. Whenever changes occur to the
 * synapse weights in the neural layer, then the structure of the boolean representation needs to be adjusted. Several
 * neurons, however, might share common substructures in this boolean representation. The {@code InputNode} and
 * the {@code AndNode} classes together form a pattern lattice, containing all possible substructures of any
 * given conjunction. For example if we have the conjunction ABCD where A, B, C, D are the inputs then the
 * pattern lattice will contain the nodes ABCD, ABC, ABD, ACD, BCD, AB, AC, AD, BC, BD, CD, A, B, C, D. The class
 * {@code OrNode} is a disjunction of either input-nodes or and-nodes. The or-node is connected with one of
 * the neurons.
 * <p>
 * <p>Each logic node has a set of activations. The activations are stored in the thread local data structure
 * {@code ThreadState}.
 *
 * @author Lukas Molzberger
 */
public abstract class Node<T extends Node, A extends NodeActivation<T>> extends AbstractNode<Provider<T>> implements Comparable<Node> {

    public static final Node MIN_NODE = new InputNode();
    public static final Node MAX_NODE = new InputNode();


    TreeMap<Refinement, RefValue> andChildren;
    TreeSet<OrEntry> orChildren;

    public int level;
    private long visited;

    private AtomicInteger numberOfNeuronRefs = new AtomicInteger(0);
    volatile boolean isRemoved;

    // Only the children maps are locked.
    protected ReadWriteLock lock = new ReadWriteLock();


    private WeakHashMap<Integer, ThreadState> activations = new WeakHashMap<>();


    long markedCreated;

    /**
     * Propagate an activation to the next node or the next neuron that is depending on the current node.
     *
     * @param act
     */
    protected abstract void propagate(A act);


    /**
     * The {@code ThreadState} is a thread local data structure containing the activations of a single document for
     * a specific logic node.
     */
    public static class ThreadState<A extends NodeActivation> {
        public long lastUsed;

        public List<A> added;
        public List<A> activations;

        public long visited;

        public boolean isQueued = false;
        public long queueId;

        public ThreadState() {
            added = new ArrayList<>();
            activations = new ArrayList<>();
        }
    }

    public ThreadState<A> lookupThreadState(Document doc) {
        synchronized (activations) {
            ThreadState<A> th = activations
                    .computeIfAbsent(
                            doc.getId(),
                            n -> new ThreadState()
                    );
            th.lastUsed = provider.getModel().docIdCounter.get();
            return th;
        }
    }

    abstract RefValue expand(Document doc, Refinement ref);

    public abstract void reprocessInputs(Document doc);

    public abstract void cleanup();

    public abstract String logicToString();


    protected Node() {
    }


    public Node(Model m, int level) {
        provider = new Provider(m, this);
        this.level = level;
        setModified();
    }


    public void postCreate(Document doc) {
        if(doc != null) {
            markedCreated = doc.createV;
            doc.addedNodes.add(this);
        }
    }


    void addOrChild(OrEntry rv) {
        lock.acquireWriteLock();
        if (orChildren == null) {
            orChildren = new TreeSet<>();
        }
        orChildren.add(rv);
        lock.releaseWriteLock();
    }


    void removeOrChild(OrEntry rv) {
        lock.acquireWriteLock();
        if (orChildren != null) {
            orChildren.remove(rv);
            if (orChildren.isEmpty()) {
                orChildren = null;
            }
        }
        lock.releaseWriteLock();
    }


    void addAndChild(Refinement ref, RefValue child) {
        if (andChildren == null) {
            andChildren = new TreeMap<>();
        }

        if(!andChildren.containsKey(ref)) {
            andChildren.put(ref, child);
        }
    }


    void removeAndChild(Refinement ref) {
        if (andChildren != null) {
            andChildren.remove(ref);

            if (andChildren.isEmpty()) {
                andChildren = null;
            }
        }
    }


    void processActivation(A act) {
        register(act);
        propagate(act);
    }


    public void register(A act) {
        if(act.registered) {
            return;
        }

        Document doc = act.getDocument();

        assert act.getNode() == this;

        ThreadState th = lookupThreadState(doc);
        doc.addActivatedNode(act.getNode());

        th.activations.add(act);

        doc.addedNodeActivations.add(act);
        act.registered = true;
    }

    /**
     * Process all added or removed activation for this logic node.
     *
     * @param doc
     */
    public void processChanges(Document doc) {
        ThreadState th = lookupThreadState(doc);
        List<A> tmpAdded = th.added;

        th.added = new ArrayList<>();

        tmpAdded.forEach(act -> processActivation(act));
    }


    protected void propagateToOrNode(NodeActivation inputAct) {
        Document doc = inputAct.getDocument();
        try {
            lock.acquireReadLock();
            if (orChildren != null) {
                for (OrEntry oe : orChildren) {
                    OrNode on = oe.child.get(doc);
                    if(on != null) {
                        on.addActivation(oe, inputAct);
                    }
                }
            }
        } finally {
            lock.releaseReadLock();
        }
    }


    /**
     * Add a new activation to this logic node and further propagate this activation through the network.
     * This activation, however, will not be added immediately. This method only adds a request to the activations
     * queue in the document. The activation will be added when the method {@code Node.processChanges(Document doc)}
     * is called.
     *
     * @param act
     */
    public void addActivation(A act) {
        ThreadState<A> th = lookupThreadState(act.getDocument());
        act.doc.addActivatedNode(act.getNode());

        th.added.add(act);
        act.getDocument().getNodeQueue().add(this);
    }


    public void remove() {
        if(isRemoved) {
            return;
        }

        lock.acquireWriteLock();
        setModified();
        while (andChildren != null && !andChildren.isEmpty()) {
            andChildren.firstEntry().getValue().child.get().remove();
        }

        while (orChildren != null && !orChildren.isEmpty()) {
            orChildren.pollFirst().child.get().remove();
        }
        lock.releaseWriteLock();

        isRemoved = true;
    }


    RefValue getAndChild(Refinement ref) {
        lock.acquireReadLock();
        RefValue result = andChildren != null ? andChildren.get(ref) : null;
        lock.releaseReadLock();
        return result;
    }


    public boolean isRequired() {
        return numberOfNeuronRefs.get() > 0;
    }


    protected void changeNumberOfNeuronRefs(Document doc, long v, int d) {
        if(doc == null) {
            if (visited == v) return;
            visited = v;
        } else {
            ThreadState th = lookupThreadState(doc);
            if (th.visited == v) return;
            th.visited = v;
        }
        numberOfNeuronRefs.addAndGet(d);
    }


    public Collection<A> getActivations(Document doc) {
        ThreadState<A> th = lookupThreadState(doc);
        if (th == null) return Collections.EMPTY_LIST;
        return th.activations;
    }



    public String getNeuronLabel() {
        return "";
    }


    public boolean isQueued(Document doc, long queueId) {
        ThreadState th = lookupThreadState(doc);
        if (!th.isQueued) {
            th.isQueued = true;
            th.queueId = queueId;
        }
        return false;
    }


    public void setNotQueued(Document doc) {
        ThreadState th = lookupThreadState(doc);
        if(th == null) return;
        th.isQueued = false;
    }


    public static int compareRank(Document doc, Node n1, Node n2) {
        int r = Integer.compare(n1.level, n2.level);
        if(r != 0) return r;

        ThreadState th1 = n1.lookupThreadState(doc);
        ThreadState th2 = n2.lookupThreadState(doc);
        return Long.compare(th1.queueId, th2.queueId);
    }


    public String toString() {
        if(this == MIN_NODE) return "MIN_NODE";
        if(this == MAX_NODE) return "MAX_NODE";

        StringBuilder sb = new StringBuilder();
        sb.append(getNeuronLabel());
        sb.append(" - ");
        sb.append(logicToString());
        return sb.toString();
    }


    public int compareTo(Node n) {
        if (this == n) return 0;
        if (this == MIN_NODE) return -1;
        if (n == MIN_NODE) return 1;
        if (this == MAX_NODE) return 1;
        if (n == MAX_NODE) return -1;

        return provider.compareTo(n.provider);
    }


    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeInt(level);

        out.writeInt(numberOfNeuronRefs.get());

        if (andChildren != null) {
            out.writeInt(andChildren.size());
            for (Map.Entry<Refinement, RefValue> me : andChildren.entrySet()) {
                me.getKey().write(out);
                me.getValue().write(out);
            }
        } else {
            out.writeInt(0);
        }

        if (orChildren != null) {
            out.writeInt(orChildren.size());
            for (OrEntry oe : orChildren) {
                oe.write(out);
            }
        } else {
            out.writeInt(0);
        }
    }


    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        level = in.readInt();

        numberOfNeuronRefs.set(in.readInt());

        int s = in.readInt();
        for (int i = 0; i < s; i++) {
            addAndChild(Refinement.read(in, m), RefValue.read(in, m));
        }

        s = in.readInt();
        for (int i = 0; i < s; i++) {
            if (orChildren == null) {
                orChildren = new TreeSet<>();
            }
            orChildren.add(OrEntry.read(in, m));
        }
    }


    public static Node readNode(DataInput in, Provider p) throws IOException {
        char type = in.readChar();
        Node n = null;
        switch (type) {
            case 'I':
                n = new InputNode();
                break;
            case 'A':
                n = new AndNode();
                break;
            case 'O':
                n = new OrNode();
                break;
        }
        n.provider = p;

        n.readFields(in, p.getModel());
        return n;
    }

    @Override
    public boolean isNeuron() {
        return false;
    }
}
