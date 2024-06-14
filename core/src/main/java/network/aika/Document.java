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
import network.aika.elements.neurons.Neuron;
import network.aika.elements.PreActivation;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.fields.Field;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import network.aika.queue.Timestamp;
import network.aika.queue.Step;
import network.aika.text.TextReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static network.aika.queue.Timestamp.MIN;
import static network.aika.queue.Timestamp.NOT_SET;
import static network.aika.elements.activations.StateType.*;

/**
 * The {@code Document} class represents a single document which may be either used for processing a text or as
 * training input. A document consists of the raw text, the interpretations and the activations.
 *
 * @author Lukas Molzberger
 */
public class Document extends Queue implements Element, QueueProvider {

    protected static final Logger LOG = LoggerFactory.getLogger(Document.class);


    protected final Model model;

    private Long id;

    private final StringBuilder content;

    private Context context;

    private long absoluteBeginChar;

    private int activationIdCounter = 0;

    private long visitorCounter = 0;

    private final TreeMap<Integer, Activation> activationsById = new TreeMap<>();
    private final Map<NeuronProvider, PreActivation> actsPerNeuron = new HashMap<>();
    private final Set<EventListener> eventListeners = new HashSet<>();

    private InstantiationCallback instantiationCallback;

    private boolean isStale;

    public Document(Model m, String content) {
        model = m;
        id = model.createThoughtId();

        this.content = new StringBuilder();
        if(content != null) {
            this.content.append(content);
        }

        absoluteBeginChar = m.getN();

        m.registerDocument(this);
    }

    public long getNewVisitorId() {
        return visitorCounter++;
    }

    public Long getId() {
        return id;
    }

    @Override
    public long getTimeout() {
        return getConfig().getTimeout();
    }

    public void process(Predicate<Step> filter) {
        super.process(filter);

        if(model.getConfig().isCountingEnabled())
            model.addToN(length());
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

    public void register(Neuron n, PreActivation acts) {
        PreActivation existingPreAct = actsPerNeuron.put(n.getProvider(), acts);

        if(existingPreAct != null)
            LOG.error("Attempted to overwrite existing PreAct: (doc:" + id + " n:" + n.getId() + ")");
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
        model.deregisterDocument(this);

        getActivations()
                .forEach(act ->
                        act.disconnect()
                );

        actsPerNeuron.values()
                .stream()
                .map(PreActivation::getNeuron)
                .forEach(n -> n.removePreActivation(this));

        isStale = true;
    }

    @Override
    public Queue getQueue() {
        return this;
    }

    @Override
    public boolean isNextRound() {
        return false;
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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

    public static String getText(Activation act) {
        return act.getDocument().getTextSegment(act.getTextReference().getCharRange());
    }

    public Activation addToken(Neuron n, TextReference textReference) {
        return addToken(n, textReference, n.getTargetNet());
    }

    public Activation addToken(Neuron n, TextReference textReference, double inputNet) {
        Activation act = n.createActivation(this);

        act.updateRanges(textReference);

        disconnectNetFields(act);

        act.setNet(INNER_FEEDBACK, inputNet);

        return act;
    }

    private void disconnectNetFields(Activation act) {
        disconnectAndBlock(act.getNet(PRE_FEEDBACK));
        disconnectAndBlock(act.getNet(OUTER_FEEDBACK));
        disconnectAndBlock(act.getNet(INNER_FEEDBACK));
    }

    private void disconnectAndBlock(Field f) {
        f.disconnectInputs(false);
        f.setBlocked(true);
    }

    @Override
    public Timestamp getCreated() {
        return MIN;
    }

    @Override
    public Timestamp getFired() {
        return NOT_SET;
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
