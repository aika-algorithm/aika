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

import network.aika.elements.neurons.RefType;
import network.aika.elements.synapses.Synapse;
import network.aika.suspension.InMemorySuspensionCallback;
import network.aika.suspension.SuspensionCallback;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.queue.Queue;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static network.aika.elements.neurons.RefType.*;

/**
 *
 * @author Lukas Molzberger
 */
public class Model extends Queue implements Writable {

    protected static final Logger LOG = LoggerFactory.getLogger(Model.class);

    private long N = 0;

    private Config config;

    private SuspensionCallback suspensionCallback;

    private final AtomicLong documentIdCounter = new AtomicLong(0);

    public final Map<Long, NeuronProvider> providers = new TreeMap<>();

    private SortedMap<Long, Document> documents = new TreeMap<>();

    private long lastProcessedDocument;

    private Supplier<Writable> customDataInstanceSupplier;

    public Model() {
        this(new InMemorySuspensionCallback());
    }

    public Model(SuspensionCallback sc) {
        suspensionCallback = sc;
    }

    public Supplier<Writable> getCustomDataInstanceSupplier() {
        return customDataInstanceSupplier;
    }

    public void setCustomDataInstanceSupplier(Supplier<Writable> customDataInstanceSupplier) {
        this.customDataInstanceSupplier = customDataInstanceSupplier;
    }

    public long createNeuronId() {
        return suspensionCallback.createId();
    }

    public Long getLowestDocumentId() {
        synchronized (documents) {
            try {
                return documents.firstKey();
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public void registerDocument(Document doc) {
        synchronized (documents) {
            documents.put(doc.getId(), doc);
        }
    }

    public void deregisterDocument(Document doc) {
        synchronized (documents) {
            documents.remove(doc.getId());
        }

        lastProcessedDocument = Math.max(lastProcessedDocument, doc.getId());
    }

    public Collection<NeuronProvider> getActiveNeurons() {
        return new ArrayList<>(providers.values());
    }

    public <N extends Neuron> N lookupNeuronByLabel(String label, N template) {
        N n = getNeuronByLabel(label, template);
        if(n != null)
            return n;

        n = (N) template.instantiateTemplate()
                .setLabel(label);

        n.setAllowTraining(false);

        registerLabel(n, template);

        n.getProvider().decreaseRefCount(TEMPLATE);
        return n;
    }

    public void registerLabel(Neuron in, Neuron tn) {
        suspensionCallback.putLabel(in.getLabel(), tn.getId(), in.getId());
        in.getProvider().save();
    }

    public <N extends Neuron> N getNeuronByLabel(String tokenLabel, Neuron template) {
        Long id = suspensionCallback.getIdByLabel(tokenLabel, template.getId());
        return id != null ?
                (N) lookupNeuronProvider(id, NEURON_EXTERNAL).getNeuron() :
                null;
    }

    public Stream<NeuronProvider> getAllNeurons() {
        return suspensionCallback
                .getAllIds().stream()
                .map(id -> lookupNeuronProvider(id, OTHER));
    }

    public <N extends Neuron> Stream<N> getNeuronsByType(Class<N> type) {
        return getAllNeurons().map(NeuronProvider::getNeuron)
                .filter(type::isInstance)
                .map(type::cast);
    }

    public void applyMovingAverage(Config trainingConfig) {
        if(trainingConfig.getAlpha() != null) {
            N *= trainingConfig.getAlpha();
        }
    }

    public SuspensionCallback getSuspensionCallback() {
        return suspensionCallback;
    }

    public void setSuspensionCallback(SuspensionCallback suspensionCallback) {
        this.suspensionCallback = suspensionCallback;
    }

    public void addToN(int l) {
        N += l;
    }

    public long getN() {
        return N;
    }

    public void setN(long n) {
        N = n;
    }

    public boolean canBeSuspended(Long lastUsed) {
        Long tId = getLowestDocumentId();
        if(tId == null)
            tId = lastProcessedDocument;

        return lastUsed < tId - config.getNeuronProviderRetention();
    }

    public NeuronProvider lookupNeuronProvider(Long id, RefType rt) {
        synchronized (providers) {
            NeuronProvider n = providers.get(id);
            if(n != null) {
                if(rt != null)
                    n.increaseRefCount(rt);

                return n;
            }

            return new NeuronProvider(this, id, rt);
        }
    }

    public void saveAll() {
        List<NeuronProvider> toSave;

        synchronized (providers) {
            toSave = providers
                    .values()
                    .stream()
                    .filter(n -> !n.isSuspended())
                    .toList();
        }

        toSave.forEach(NeuronProvider::save);
    }

    public void suspend(boolean saveOnSuspend, boolean staleOnly) {
        List<NeuronProvider> toSuspend;

        synchronized (providers) {
            toSuspend = providers
                    .values()
                    .stream()
                    .filter(n ->
                            !n.isSuspended() &&
                                    !n.isPersistent() &&
                                    (!staleOnly || canBeSuspended(n.getLastUsed()))
                    )
                    .toList();
        }

        toSuspend.forEach(n ->
                        suspend(n, saveOnSuspend)
                );

        LOG.info("Suspended " + toSuspend.size() + " neurons. (saveOnSuspend:" + saveOnSuspend + ")");
    }

    private void suspend(NeuronProvider p, boolean saveOnSuspend) {
        Neuron n = p.getIfNotSuspended();
        if (n != null)
            p.suspend(saveOnSuspend);
    }

    public void register(NeuronProvider np) {
        synchronized (providers) {
            NeuronProvider existingNP = providers.put(np.getId(), np);

            if(existingNP != null)
                LOG.error("Attempted to overwrite existing Provider: (np:" + np.getId() + ")");
        }
    }

    public void unregister(NeuronProvider np) {
        synchronized (providers) {
            NeuronProvider removedNP = providers.remove(np.getId());

            if(removedNP != np)
                LOG.error("Attempted to remove a duplicate Provider: (np:" + np.getId() + ")");
        }
    }

    public void open(boolean create) throws IOException {
        if(create)
            suspensionCallback.prepareNewModel();
        else
            suspensionCallback.loadIndex(this);

        suspensionCallback.open();
    }

    public void close(boolean store) throws IOException {
        if(store)
            suspensionCallback.saveIndex(this);

        suspensionCallback.close();
    }

    public <N extends Neuron> N createNeuronByClass(String clazzName, NeuronProvider np) {
        try {
            Class clazz = getClass().getClassLoader().loadClass(clazzName);
            return (N) clazz.getConstructor(NeuronProvider.class)
                    .newInstance(np);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <S extends Synapse> S createSynapseByClass(String clazzName) {
        try {
            Class clazz = getClass().getClassLoader().loadClass(clazzName);
            return (S) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public long createThoughtId() {
        return documentIdCounter.addAndGet(1);
    }

    public Config getConfig() {
        if(config == null)
            config = new Config();

        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(N);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        N = in.readLong();
    }

    public String toString() {
        return "N:" + N;
    }
}
