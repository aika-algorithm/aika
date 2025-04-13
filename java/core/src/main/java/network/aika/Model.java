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

import network.aika.neurons.RefType;
import network.aika.misc.suspension.InMemorySuspensionCallback;
import network.aika.misc.suspension.SuspensionCallback;
import network.aika.neurons.Neuron;
import network.aika.queue.Queue;
import network.aika.type.TypeRegistry;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 *
 * @author Lukas Molzberger
 */
public class Model extends Queue implements Writable<Model> {

    protected static final Logger LOG = LoggerFactory.getLogger(Model.class);

    private long N = 0;

    private Config config;

    private TypeRegistry typeRegistry;

    private SuspensionCallback suspensionCallback;

    private final AtomicLong documentIdCounter = new AtomicLong(0);

    public final Map<Long, Neuron> activeNeurons = new TreeMap<>();

    private SortedMap<Long, Document> documents = new TreeMap<>();

    private long lastProcessedDocument;

    private Supplier<Writable> customDataInstanceSupplier;

    public Model(TypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
        this.suspensionCallback = new InMemorySuspensionCallback();
    }

    @Override
    public Long getTimeout() {
        return config.getTimeout();
    }

    public Supplier<Writable> getCustomDataInstanceSupplier() {
        return customDataInstanceSupplier;
    }

    public Model setCustomDataInstanceSupplier(Supplier<Writable> customDataInstanceSupplier) {
        this.customDataInstanceSupplier = customDataInstanceSupplier;
        return this;
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

    public TypeRegistry getTypeRegistry() {
        return typeRegistry;
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

    public Collection<Neuron> getActiveNeurons() {
        return new ArrayList<>(activeNeurons.values());
    }


    public void registerTokenId(int tokenId, Neuron in) {
        suspensionCallback.putTokenId(tokenId, in.getId());
        in.save();
    }

    public <N extends Neuron> N getNeuronByTokenId(int tokenId) {
        Long id = suspensionCallback.getIdByTokenId(tokenId);
        return id != null ?
                (N) getNeuron(id) :
                null;
    }

    public void applyMovingAverage(Config trainingConfig) {
        if(trainingConfig.getAlpha() != null) {
            N *= trainingConfig.getAlpha();
        }
    }

    public SuspensionCallback getSuspensionCallback() {
        return suspensionCallback;
    }

    public Model setSuspensionCallback(SuspensionCallback suspensionCallback) {
        this.suspensionCallback = suspensionCallback;
        return this;
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

    public Neuron getNeuron(Long id) {
        synchronized (activeNeurons) {
            return activeNeurons.get(id);
        }
    }

    /*
    public void saveAll() {
        List<NeuronProvider> toSave;

        synchronized (activeNeurons) {
            toSave = activeNeurons
                    .values()
                    .stream()
                    .filter(n -> !n.isSuspended())
                    .toList();
        }

        toSave.forEach(NeuronProvider::save);
    }
     */

    /*
    public void suspend(boolean saveOnSuspend, boolean staleOnly) {
        List<Neuron> toSuspend;

        synchronized (activeNeurons) {
            toSuspend = activeNeurons
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
     */

    public void register(Neuron n) {
        synchronized (activeNeurons) {
            Neuron existingNP = activeNeurons.put(n.getId(), n);

            if(existingNP != null)
                LOG.error("Attempted to register Neuron twice: (n:" + n.getId() + ")");
        }
    }

    public void unregister(Neuron n) {
        synchronized (activeNeurons) {
            Neuron removedN = activeNeurons.remove(n.getId());

            if(removedN != n)
                LOG.error("Attempted to remove Neuron twice: (n:" + n.getId() + ")");
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

    public long createThoughtId() {
        return documentIdCounter.addAndGet(1);
    }

    public Config getConfig() {
        if(config == null)
            config = new Config();

        return config;
    }

    public Model setConfig(Config config) {
        this.config = config;
        return this;
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


    public synchronized boolean suspend(Neuron neuron, boolean saveOnSuspend) {
/*        if(neuron.inUse())
            return false;

        assert getSuspensionCallback() != null;

        if(saveOnSuspend)
            neuron.save();

        neuron.suspend();
        neuron = null;
*/
        return true;
    }

    public Neuron reactivate(long neuronId) {
        return null;
/*
        assert getSuspensionCallback() != null;

        Neuron n;
        try (DataInputStream dis = new DataInputStream(
                new ByteArrayInputStream(
                        getSuspensionCallback().retrieve(neuronId)
                )
        )) {
            n = Neuron.read(dis, this);
        } catch (Exception e) {
            throw new NeuronSerializationException(neuronId, e);
        }

        n.reactivate(this);

 */
    }
}
