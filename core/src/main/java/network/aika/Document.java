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
import network.aika.elements.Timestamp;
import network.aika.elements.activations.Activation;
import network.aika.elements.Element;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.exceptions.PreviousThoughtNotDisconnected;
import network.aika.fields.*;
import network.aika.elements.PreActivation;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.queue.Queue;
import network.aika.text.Range;
import network.aika.queue.Step;
import network.aika.queue.activation.InactiveLinks;
import network.aika.queue.activation.Instantiation;
import network.aika.queue.document.AnnealStep;
import network.aika.text.TextReference;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static network.aika.elements.Timestamp.MIN;
import static network.aika.elements.Timestamp.NOT_SET;
import static network.aika.queue.Phase.*;
import static network.aika.queue.keys.QueueKey.MAX_ROUND;

/**
 * The {@code Document} class represents a single document which may be either used for processing a text or as
 * training input. A document consists of the raw text, the interpretations and the activations.
 *
 * @author Lukas Molzberger
 */
public class Document extends Queue implements Element {

    protected final Model model;

    private Long id;

    private final StringBuilder content;

    private long absoluteBeginChar;

    private Field annealing;
    private Field feedbackTrigger;
    private Field instantiationFeedbackTrigger;

    private int activationIdCounter = 0;

    private long visitorCounter = 0;

    private final TreeMap<Integer, Activation> activationsById = new TreeMap<>();
    private final Map<NeuronProvider, PreActivation<? extends Activation>> actsPerNeuron = new HashMap<>();
    private final Set<EventListener> eventListeners = new HashSet<>();

    private InstantiationCallback instantiationCallback;


    public Document(Model m, String content) {
        model = m;
        id = model.createThoughtId();

        this.content = new StringBuilder();
        if(content != null) {
            this.content.append(content);
        }

        absoluteBeginChar = m.getN();

        annealing = new InputField(this, "anneal", 0.0);
        feedbackTrigger = new SumField(this, "feedback trigger", 0.0)
                .setQueued(this, FEEDBACK_TRIGGER);

        instantiationFeedbackTrigger = new SumField(this, "instantiation feedback trigger", 0.0)
                .setQueued(this, FEEDBACK_TRIGGER);

        if(m.getCurrentDocument() != null)
            throw new PreviousThoughtNotDisconnected(m.getCurrentDocument(), this);

        m.registerDocument(this);
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

    public Field getInstantiationFeedbackTrigger() {
        return instantiationFeedbackTrigger;
    }

    public void setFeedbackTriggerRound() {
        feedbackTrigger.receiveUpdate(null, false, 1.0);
        feedbackTrigger.receiveUpdate(null, true, -1.0);
    }

    public void setInstantiationFeedbackTrigger(boolean state) {
        if(state)
            instantiationFeedbackTrigger.receiveUpdate(null, false, 1.0);
        else
            instantiationFeedbackTrigger.receiveUpdate(null, true, -1.0);
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

    public Range getCharRange() {
        return new Range(absoluteBeginChar, absoluteBeginChar + length());
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
        model.deregisterThought(this);

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
        getActivations()
                .forEach(InactiveLinks::add);

        process(MAX_ROUND, TRAINING);
    }

    public void instantiateTemplates() {
        if (!getConfig().isMetaInstantiationEnabled())
            return;

        getActivations().stream()
                .filter(act -> act.getNeuron().isAbstract())
                .filter(Activation::isFired)
                .forEach(Instantiation::add);

        process(MAX_ROUND, ANNEAL);

        incrementRound();
        setFeedbackTriggerRound();
    }

    public String activationsToString() {
        return getActivations()
                .stream()
                .map(act -> act + "\n")
                .collect(Collectors.joining());
    }

    public void append(String txt) {
        content.append(txt);
    }

    public char charAt(int i) {
        return content.charAt(i);
    }

    public String getContent() {
        return content.toString();
    }

    public int length() {
        return content.length();
    }

    public String getTextSegment(Range range) {
        if(range == null)
            return "";

        Range r = range.limit(new Range(0, length()));
        return content.substring((int) r.getBegin(), (int) r.getEnd());
    }

    public static String getText(Activation<?> act) {
        return act.getDocument().getTextSegment(act.getTextReference().getCharRange());
    }

    public PatternActivation addToken(PatternNeuron n, TextReference textReference) {
        return addToken(n, textReference, n.getTargetNet());
    }

    public PatternActivation addToken(PatternNeuron n, TextReference textReference, double inputNet) {
        PatternActivation act = n.createActivation(this);

        act.updateRanges(textReference);

        act.getNet().disconnectInputs(false);
        act.setNet(inputNet);
        return act;
    }

    @Override
    public Timestamp getCreated() {
        return MIN;
    }

    @Override
    public Timestamp getFired() {
        return NOT_SET;
    }

    @Override
    public Document getDocument() {
        return this;
    }

    public String docToString() {
        StringBuilder sb = new StringBuilder(content);
        sb.append("\n");
        sb.append(activationsToString());
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Id:" + id);
        sb.append(" Content: ");
        sb.append(
                content.substring(0, Math.min(content.length(), 100))
                        .replaceAll("[\\n\\r\\s]+", " ")
        );
        return sb.toString();
    }
}
