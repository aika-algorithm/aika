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
package network.aika;


import network.aika.lattice.Node;
import network.aika.neuron.INeuron;
import network.aika.neuron.INeuron.Type;
import network.aika.neuron.Neuron;
import network.aika.Provider.SuspensionMode;
import network.aika.neuron.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * The model consists of two layers. The first layer is the actual neural network consisting of neurons and synapses.
 * The second layer is a pattern lattice containing a boolean logic representation of all the neurons. Whenever the
 * synapse weights of a neuron are adjusted, then the underlying boolean logic representation of this neuron will be
 * updated too.
 * <p>
 * <p>The model supports parallel processing using a fixed number of threads.
 *
 * @author Lukas Molzberger
 */
public class Model {

    private static final Logger log = LoggerFactory.getLogger(Model.class);

    public SuspensionHook suspensionHook;

    public AtomicInteger docIdCounter = new AtomicInteger(0);
    public AtomicInteger currentId = new AtomicInteger(0);

    // Important: the id field needs to be referenced by the provider!
    public WeakHashMap<Integer, WeakReference<Provider<? extends AbstractNode>>> providers = new WeakHashMap<>();
    public Map<Integer, Provider<? extends AbstractNode>> activeProviders = new TreeMap<>();

    public Map<Integer, PassiveInputFunction> passiveActivationFunctions = new TreeMap<>();

    public static AtomicLong visitedCounter = new AtomicLong(1);


    public Supplier<Writable> dataSupplier;
    /**
     * Creates a model with a single thread.
     */
    public Model() {
        this(null, 1);
    }


    public Model(SuspensionHook sh, int numberOfThreads) {
        assert numberOfThreads >= 1;

        suspensionHook = sh;
    }


    public Supplier<Writable> getCustomDataInstanceSupplier() {
        return dataSupplier;
    }

    public void setCustomDataInstanceSupplier(Supplier<Writable> dataSupplier) {
        this.dataSupplier = dataSupplier;
    }

    public SuspensionHook getSuspensionHook() {
        return suspensionHook;
    }


    public void setSuspensionHook(SuspensionHook suspensionHook) {
        this.suspensionHook = suspensionHook;
    }


    public Neuron createNeuron(Type type) {
        return createNeuron(null, type);
    }


    public Neuron createNeuron(String label, Type type) {
        return createNeuron(label, type, type.getDefaultActivationFunction(), null);
    }


    public Neuron createNeuron(String label, Type type, ActivationFunction actF) {
        return new INeuron(this, label, null, type, actF).getProvider();
    }

    public Neuron createNeuron(String label, Type type, String outputText) {
        return new INeuron(this, label, outputText, type, type.getDefaultActivationFunction()).getProvider();
    }


    public Neuron createNeuron(String label, Type type, ActivationFunction actF, String outputText) {
        return new INeuron(this, label, outputText, type, actF).getProvider();
    }


    public INeuron readNeuron(DataInput in, Neuron p) throws IOException {
        INeuron n = new INeuron(p);
        n.readFields(in, this);
        return n;
    }


    public Synapse readSynapse(DataInput in) throws IOException {
        Synapse s = new Synapse();
        s.readFields(in, this);
        return s;
    }


    public void writeSynapse(Synapse s, DataOutput out) throws IOException {
        s.write(out);
    }


    public int getNewDocumentId() {
        return docIdCounter.addAndGet(1);
    }



    public Collection<Neuron> getActiveNeurons() {
        List<Neuron> tmp = new ArrayList<>();
        for(Provider<?> p: activeProviders.values()) {
            if(p instanceof Neuron) {
                tmp.add((Neuron) p);
            }
        }

        return tmp;
    }


    public <P extends Provider<? extends Node>> P lookupNodeProvider(int id) {
        synchronized (providers) {
            WeakReference<Provider<? extends AbstractNode>> wr = providers.get(id);
            if(wr != null) {
                P p = (P) wr.get();
                if (p != null) {
                    return p;
                }
            }

            return (P) new Provider(this, id);
        }
    }



    public Neuron lookupNeuron(int id) {
        synchronized (providers) {
            WeakReference<Provider<? extends AbstractNode>> wr = providers.get(id);
            if(wr != null) {
                Neuron n = (Neuron) wr.get();
                if (n != null) {
                    return n;
                }
            }

            return new Neuron(this, id);
        }
    }


    public void register(Provider p) {
        synchronized (activeProviders) {
            activeProviders.put(p.id, p);
        }
    }


    public void unregister(Provider p) {
        synchronized (activeProviders) {
            activeProviders.remove(p.id);
        }
    }



    /**
     * Suspend all neurons and logic nodes whose last used document id is lower/older than docId.
     *
     * @param docId
     */
    public void suspendUnusedNodes(int docId, SuspensionMode sm) {
        List<Provider> tmp;
        synchronized (activeProviders) {
            tmp = new ArrayList<>(activeProviders.values());
        }
        for (Provider p: tmp) {
            suspend(docId, p, sm);
        }
    }



    /**
     * Suspend all neurons and logic nodes in memory.
     *
     */
    public void suspendAll(SuspensionMode sm) {
        suspendUnusedNodes(Integer.MAX_VALUE, sm);
    }


    private boolean suspend(int docId, Provider<? extends AbstractNode> p, SuspensionMode sm) {
        AbstractNode an = p.getIfNotSuspended();
        if (an != null && an.lastUsedDocumentId < docId) {
            p.suspend(sm);
            return true;
        }
        return false;
    }

    public void removeProvider(Provider p) {
        synchronized (activeProviders) {
            activeProviders.remove(p.id);
        }
        synchronized (providers) {
            providers.remove(p.id);
        }
    }




    public static class StaleDocumentException extends RuntimeException {

        public StaleDocumentException() {
            super("Two documents are using the same thread. Call clearActivations() first, before processing the next document.");
        }
    }
}
