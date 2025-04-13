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


import network.aika.activations.Activation;
import network.aika.bindingsignal.BSType;
import network.aika.bindingsignal.BindingSignal;
import network.aika.neurons.Neuron;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import network.aika.queue.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;


/**
 * The {@code Document} class represents a single document which may be either used for processing a text or as
 * training input. A document consists of the raw text, the interpretations and the activations.
 *
 * @author Lukas Molzberger
 */
public class Document extends Queue implements ModelProvider, QueueProvider {

    protected static final Logger LOG = LoggerFactory.getLogger(Document.class);

    protected final Model model;

    private Long id;

    private long absoluteBeginChar;
    private int length;

    private int activationIdCounter = 0;

    private final Map<Integer, Activation> activations = new TreeMap<>();
    private final TreeMap<Integer, BindingSignal> bindingSignals = new TreeMap<>();

    private boolean isStale;

    public Document(Model m, int length) {
        model = m;
        id = model.createThoughtId();

        absoluteBeginChar = m.getN();
        this.length = length;

        m.registerDocument(this);
    }

    public Long getId() {
        return id;
    }

    @Override
    public Long getTimeout() {
        return getConfig().getTimeout();
    }

    public void process(Predicate<Step> filter) {
        super.process(filter);

        if(model.getConfig().isCountingEnabled())
            model.addToN(length);
    }

    public Model getModel() {
        return model;
    }

    @Override
    public Config getConfig() {
        return model.getConfig();
    }

    public Step getCurrentStep() {
        return currentStep;
    }

    public void addActivation(Activation act) {
        activations.put(act.getId(), act);
    }

    public Collection<Activation> getActivations() {
        return activations.values();
    }

    public Activation getActivationByNeuron(Neuron outputNeuron) {
        return getActivations().stream()
                .filter(act -> act.getNeuron() == outputNeuron)
                .findFirst()
                .orElse(null);
    }

    public int createActivationId() {
        return activationIdCounter++;
    }

    public void disconnect() {
        model.deregisterDocument(this);

        isStale = true;
    }

    @Override
    public Queue getQueue() {
        return this;
    }

    public Activation addToken(Neuron n, BSType bsType, int tokenId) {
        BindingSignal bs = getOrCreateBindingSignal(tokenId);

        Activation act = n.createActivation(
                null,
                this,
                Map.of(bsType, bs)
        );

        return act;
    }

    public BindingSignal getOrCreateBindingSignal(int tokenId) {
        return bindingSignals.computeIfAbsent(tokenId, tid ->
                new BindingSignal(tid, this)
        );
    }

    public BindingSignal getBindingSignal(Integer tokenId) {
        return bindingSignals.get(id);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Id:" + id);
        return sb.toString();
    }
}
