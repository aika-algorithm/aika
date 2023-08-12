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


import network.aika.debugger.EventListener;
import network.aika.debugger.EventType;
import network.aika.callbacks.InstantiationCallback;
import network.aika.elements.activations.Activation;
import network.aika.elements.Element;
import network.aika.elements.Timestamp;
import network.aika.exceptions.PreviousThoughtNotDisconnected;
import network.aika.fields.*;
import network.aika.elements.PreActivation;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.text.Range;
import network.aika.steps.Phase;
import network.aika.steps.keys.QueueKey;
import network.aika.steps.Step;
import network.aika.steps.activation.InactiveLinks;
import network.aika.steps.activation.Instantiation;
import network.aika.steps.thought.AnnealStep;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static network.aika.debugger.EventType.*;
import static network.aika.steps.Phase.*;
import static network.aika.steps.keys.QueueKey.MAX_ROUND;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Thought implements Element {

    private Field annealing;
    private Field feedbackTrigger;

    protected final Model model;

    private Long id;
    private long absoluteBeginChar;

    private Timestamp timestampOnProcess = new Timestamp(0);
    private long timestampCounter = 0;
    private int activationIdCounter = 0;

    private long visitorCounter = 0;

    private Step currentStep;

    int round = 0;

    private final NavigableMap<QueueKey, Step> queue = new TreeMap<>(QueueKey.COMPARATOR);

    private final TreeMap<Integer, Activation> activationsById = new TreeMap<>();
    private final Map<NeuronProvider, PreActivation<? extends Activation>> actsPerNeuron = new HashMap<>();
    private final Set<EventListener> eventListeners = new HashSet<>();

    private Config config;

    private InstantiationCallback instantiationCallback;


    public Thought(Model m) {
        model = m;
        id = model.createThoughtId();
        absoluteBeginChar = m.getN();

        annealing = new InputField(this, "anneal", 0.0);
        feedbackTrigger = new QueueSumField(this, FEEDBACK_TRIGGER, "feedback trigger", 0.0);

        if(m.getCurrentThought() != null) {
            throw new PreviousThoughtNotDisconnected(m.getCurrentThought(), this);
        }
        m.setCurrentThought(this);
    }

    public int getRound(boolean nextRound) {
        return round + (nextRound ? 1 : 0);
    }

    public void updateRound(int r) {
        if(currentStep.getRound() != MAX_ROUND && round < r)
            round = r;
    }

    public void incrementRound() {
        round++;
    }

    public long getNewVisitorId() {
        return visitorCounter++;
    }

    public Long getId() {
        return id;
    }

    public void updateModel() {
        model.addToN(length());
    }

    public Model getModel() {
        return model;
    }

    public Field getAnnealing() {
        return annealing;
    }

    public Field getFeedbackTrigger() {
        return feedbackTrigger;
    }

    public void setFeedbackTriggerRound() {
        feedbackTrigger.receiveUpdate(null, false, 1.0);
        feedbackTrigger.receiveUpdate(null, true, -1.0);
    }

    public abstract int length();

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Step getCurrentStep() {
        return currentStep;
    }

    public void queueEvent(EventType et, Step s) {
        callEventListener(el ->
                el.onQueueEvent(et, s)
        );
    }

    public void onElementEvent(EventType et, Element e) {
        callEventListener(el ->
                el.onElementEvent(et, e)
        );
    }

    public InstantiationCallback getInstantiationCallback() {
        return instantiationCallback;
    }

    public void setInstantiationCallback(InstantiationCallback instantiationCallback) {
        this.instantiationCallback = instantiationCallback;
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

    public void register(NeuronProvider np, PreActivation<? extends Activation> acts) {
        actsPerNeuron.put(np, acts);
    }

    public void addStep(Step s) {
        s.createQueueKey(getNextTimestamp());
        queue.put(s.getQueueKey(), s);
        queueEvent(ADDED, s);
    }

    public void removeStep(Step s) {
        Step removedStep = queue.remove(s.getQueueKey());
        assert removedStep != null;
        s.removeQueueKey();
    }

    public Collection<Step> getQueue() {
        return queue.values();
    }

    public Range getCharRange() {
        return new Range(absoluteBeginChar, absoluteBeginChar + length());
    }

    public void process(int maxRound, Phase maxPhase) {
        while (!queue.isEmpty()) {
            if(checkMaxPhaseReached(maxRound, maxPhase))
                break;

            currentStep = queue.pollFirstEntry().getValue();
            currentStep.removeQueueKey();

            timestampOnProcess = getCurrentTimestamp();

            queueEvent(BEFORE, currentStep);

            updateRound(currentStep.getRound());

            currentStep.process();
            queueEvent(AFTER, currentStep);
            currentStep = null;
        }
    }

    private boolean checkMaxPhaseReached(int maxRound, Phase maxPhase) {
        QueueKey fe = queue.firstEntry().getKey();
        if(fe.getRound() > maxRound)
            return true;

        if(maxPhase == null)
            return false;

        return maxPhase.compareTo(fe.getPhase()) < 0;
    }

    /**
     * The postprocessing steps such as counting, cleanup or save are executed.
     */
    public void postProcessing() {
        process(MAX_ROUND, null);
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
                .filter(s -> s.getElement() == element)
                .toList();
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

    public void disconnect() {
        if(model.getCurrentThought() == this)
            model.setCurrentThought(null);

        getActivations()
                .forEach(act ->
                        act.disconnect()
                );
    }

    public void anneal() {
        AnnealStep.add(this);
        process(MAX_ROUND, ANNEAL); // Anneal needs to be finished before instantiation can start.
    }

    public void train() {
        activationsById.values()
                .forEach(InactiveLinks::add);

        process(MAX_ROUND, TRAINING);
    }

    public void instantiateTemplates() {
        if (!getConfig().isMetaInstantiationEnabled())
            return;

        activationsById.values().stream()
                .filter(act -> act.getNeuron().isAbstract())
                .filter(Activation::isFired)
                .forEach(Instantiation::add);

        process(MAX_ROUND, ANNEAL);

        incrementRound();
        setFeedbackTriggerRound();
    }

    public String activationsToString() {
        return activationsById
                .values()
                .stream()
                .map(act -> act + "\n")
                .collect(Collectors.joining());
    }

    public String toString() {
        return getClass().getSimpleName() + " id:" + id;
    }
}
