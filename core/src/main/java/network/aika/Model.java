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


import network.aika.elements.neurons.PatternNeuron;
import network.aika.suspension.InMemorySuspensionCallback;
import network.aika.suspension.SuspensionCallback;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.suspension.SuspensionMode;
import network.aika.utils.Writable;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 *
 * @author Lukas Molzberger
 */
public class Model implements Writable {

    private long N = 0;

    private Config config;

    private SuspensionCallback suspensionCallback;
    private final AtomicLong retrievalCounter = new AtomicLong(0);
    private final AtomicLong thoughtIdCounter = new AtomicLong(0);

    public final Map<Long, NeuronProvider> providers = new TreeMap<>();

    private Thought currentThought;

    private SortedMap<Long, Thought> thoughts = new TreeMap<>();

    private long lastProcessedThought;

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

    public long getCurrentRetrievalCount() {
        return retrievalCounter.longValue();
    }

    public void incrementRetrievalCounter() {
        retrievalCounter.addAndGet(1);
    }

    public long createNeuronId() {
        return suspensionCallback.createId();
    }

    public Thought getCurrentThought() {
        return currentThought;
    }

    public Long getLowestThoughtId() {
        return thoughts.isEmpty() ?
                null :
                thoughts.firstKey();
    }

    public void registerThought(Thought t) {
        this.currentThought = t;
        thoughts.put(t.getId(), t);
    }

    public void deregisterThought(Thought t) {
        if(currentThought == t)
            currentThought = null;

        thoughts.remove(t.getId());

        lastProcessedThought = Math.max(lastProcessedThought, t.getId());
    }

    public Collection<NeuronProvider> getActiveNeurons() {
        return new ArrayList<>(providers.values());
    }

    public PatternNeuron lookupInputNeuron(String tokenLabel, PatternNeuron template) {
        PatternNeuron n = getInputNeuron(tokenLabel, template);
        if(n != null)
            return n;

        n = template.instantiateTemplate()
                .setLabel(tokenLabel);

        n.setAllowTraining(false);

        suspensionCallback.putLabel(tokenLabel, template.getId(), n.getId());
        n.getProvider().save();
        return n;
    }

    public <N extends Neuron> N getInputNeuron(String tokenLabel, PatternNeuron template) {
        Long id = suspensionCallback.getIdByLabel(tokenLabel, template.getId());
        return id != null ? (N) lookupNeuronProvider(id).getNeuron() : null;
    }

    public Stream<NeuronProvider> getAllNeurons() {
        return suspensionCallback
                .getAllIds().stream()
                .map(this::lookupNeuronProvider);
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
        Long tId = getLowestThoughtId();
        if(tId == null)
            tId = lastProcessedThought;

        return lastUsed < tId - config.getNeuronProviderRetention();
    }

    public NeuronProvider lookupNeuronProvider(Long id) {
        synchronized (providers) {
            NeuronProvider n = providers.get(id);
            if(n != null)
                return n;

            return new NeuronProvider(this, id);
        }
    }

    public void suspendUnusedNeurons(long retrievalCount, SuspensionMode sm) {
        synchronized (providers) {
            providers
                    .values()
                    .stream()
                    .filter(n -> !n.isSuspended())
                    .toList()
                    .forEach(n -> suspend(retrievalCount, n, sm));
        }
    }

    public void suspendAll(SuspensionMode sm) {
        suspendUnusedNeurons(Integer.MAX_VALUE, sm);
    }

    private void suspend(long retrievalCount, NeuronProvider p, SuspensionMode sm) {
        Neuron an = p.getIfNotSuspended();
        if (an != null && an.getRetrievalCount() < retrievalCount) {
            p.suspend(sm);
        }
    }

    public void register(NeuronProvider p) {
        synchronized (providers) {
            providers.put(p.getId(), p);
        }
    }

    public void unregister(NeuronProvider p) {
        synchronized (providers) {
            providers.remove(p.getId());
        }
    }

    public void open(boolean create) throws IOException {
        if(create)
            suspensionCallback.prepareNewModel();
        else
            suspensionCallback.loadIndex(this);

        suspensionCallback.open();
    }

    public void close() throws IOException {
        suspensionCallback.saveIndex(this);

        suspensionCallback.close();
    }

    public Object modelClass(String clazzName) {
        try {
            Class clazz = getClass().getClassLoader().loadClass(clazzName);
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long createThoughtId() {
        return thoughtIdCounter.addAndGet(1);
    }

    public Config getConfig() {
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
