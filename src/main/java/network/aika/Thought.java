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


import network.aika.callbacks.EventListener;
import network.aika.neuron.Neuron;
import network.aika.neuron.NeuronProvider;
import network.aika.neuron.Range;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.*;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.steps.InnerQueue;
import network.aika.steps.QueueKey;
import network.aika.steps.Step;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class Thought<M extends Model> {

    protected final M model;

    private long id;
    private long absoluteBegin;

    private Timestamp timestampOnProcess = new Timestamp(0);
    private long timestampCounter = 0;
    private int activationIdCounter = 0;

    private final NavigableMap<QueueKey, InnerQueue> queue = new TreeMap<>(QueueKey.COMPARATOR);

    private final TreeMap<Integer, Activation> activationsById = new TreeMap<>();
    private final Map<NeuronProvider, SortedSet<Activation<?>>> actsPerNeuron = new HashMap<>();
    private final List<EventListener> eventListeners = new ArrayList<>();

    private Config config;

    public Thought(M m) {
        model = m;
        id = model.createThoughtId();
        absoluteBegin = m.getN();
        m.setCurrentThought(this);
    }

    public long getId() {
        return id;
    }

    public void updateModel() {
        model.addToN(length());
    }

    public M getModel() {
        return model;
    }

    public abstract int length();

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void queueEntryAddedEvent(Step s) {
        callEventListener(el ->
                el.queueEntryAddedEvent(s)
        );
    }

    public void beforeProcessedEvent(Step s) {
        callEventListener(el ->
                el.beforeProcessedEvent(s)
        );
    }

    public void afterProcessedEvent(Step s) {
        callEventListener(el ->
                el.afterProcessedEvent(s)
        );
    }

    public void onActivationCreationEvent(Activation act, Synapse originSynapse, Activation originAct) {
        callEventListener(el ->
                el.onActivationCreationEvent(act, originSynapse, originAct)
        );
    }

    public void onLinkCreationEvent(Link l) {
        callEventListener(el ->
                el.onLinkCreationEvent(l)
        );
    }

    private void callEventListener(Consumer<EventListener> el) {
        getEventListeners().forEach(el);
    }

    public synchronized Collection<EventListener> getEventListeners() {
        return new ArrayList<>(eventListeners);
    }

    public synchronized void addEventListener(EventListener l) {
        eventListeners.add(l);
    }

    public synchronized void removeEventListener(EventListener l) {
        eventListeners.remove(l);
    }

    public void register(Activation act) {
        activationsById.put(act.getId(), act);
    }

    public void register(NeuronProvider np, SortedSet<Activation<?>> acts) {
        actsPerNeuron.put(np, acts);
    }

    public void registerBindingSignalSource(Activation act, BindingSignal pbs) {
    }

    public void addInnerQueueEntry(InnerQueue e) {
        e.setSecondaryTimestamp(getNextTimestamp());
        queue.put(e, e);
    }

    public void removeInnerQueueEntry(InnerQueue e) {
        InnerQueue removedEntry = queue.remove(e);
        assert removedEntry != null;
    }

    public Collection<InnerQueue> getQueue() {
        return queue.values();
    }

    public Range getRange() {
        return new Range(absoluteBegin, absoluteBegin + length());
    }

    public void process() {
        while (!queue.isEmpty()) {
            InnerQueue e = queue.pollFirstEntry().getValue();
            timestampOnProcess = getCurrentTimestamp();

            e.process();
        }
    }

    /**
     * After the switch into final mode, the initial assumption, that the positive feedback synapses are fully
     * active is reverted and the actual evaluation of the feedback synapses takes place.
     */
    public void processFinalMode() {
        activationsById.values()
                .forEach(act ->
                        act.getIsFinal().set(1.0)
                );
        process();
    }

    /**
     * The postprocessing steps such as counting, cleanup or save are executed.
     */
    public void postProcessing() {
        process();
    }

    public Timestamp getTimestampOnProcess() {
        return timestampOnProcess;
    }

    public Timestamp getCurrentTimestamp() {
        return new Timestamp(timestampCounter);
    }

    public Timestamp getNextTimestamp() {
        return new Timestamp(timestampCounter++);
    }

    public <E extends Element> List<Step> getStepsByElement(E element) {
        return queue
                .values()
                .stream()
                .filter(e -> e == element)
                .flatMap(e -> e.getSteps())
                .collect(Collectors.toList());
    }

    public int createActivationId() {
        return activationIdCounter++;
    }

    public Activation getActivation(Integer id) {
        return activationsById.get(id);
    }

    public Collection<Activation> getActivations() {
        return activationsById.values();
    }

    public int getNumberOfActivations() {
        return activationsById.size();
    }

    public void disconnect() {
        getActivations().forEach(act ->
                act.disconnect()
        );
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Activation act: activationsById.values()) {
            sb.append(act.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    public Stream<BindingSignal<?>> getLooselyRelatedBindingSignals(BindingSignal<?> fromBindingSignal, Integer looseLinkingRange, Neuron toNeuron) {
        return Stream.empty();
    }
}
