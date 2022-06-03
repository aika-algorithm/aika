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
package network.aika.neuron.activation;

import network.aika.Config;
import network.aika.Model;
import network.aika.Thought;
import network.aika.direction.Direction;
import network.aika.fields.*;
import network.aika.neuron.*;
import network.aika.neuron.bindingsignal.BindingSignal;
import network.aika.neuron.bindingsignal.State;
import network.aika.sign.Sign;
import network.aika.steps.InnerQueue;
import network.aika.steps.activation.Counting;
import network.aika.utils.Utils;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.Integer.MAX_VALUE;
import static network.aika.direction.Direction.DIRECTIONS;
import static network.aika.fields.Fields.*;
import static network.aika.fields.ThresholdOperator.Type.ABOVE;
import static network.aika.fields.ThresholdOperator.Type.ABOVE_ABS;
import static network.aika.neuron.activation.Timestamp.NOT_SET;

/**
 * @author Lukas Molzberger
 */
public abstract class Activation<N extends Neuron> extends InnerQueue implements Element, Comparable<Activation> {

    public static final Comparator<Activation> NEURON_COMPARATOR = Comparator.
            <Activation>comparingLong(act -> act.getNeuron().getId())
            .thenComparingInt(Activation::getId);

    public static final Comparator<Activation> ID_COMPARATOR = Comparator.comparingInt(Activation::getId);

    protected final int id;
    protected N neuron;
    protected Thought thought;

    protected Timestamp created = NOT_SET;
    protected Timestamp fired = NOT_SET;

    protected Field value = new Field(this, "value");
    protected FieldOutput finalValue;
    protected Field net;

    protected FieldOutput isFired;
    protected FieldOutput isFiredForWeight;
    protected FieldOutput isFiredForBias;

    protected Field isFinal;

    private FieldFunction entropy;
    protected FieldFunction netOuterGradient;
    protected Field ownInputGradient;
    protected Field backpropInputGradient;
    protected Field ownOutputGradient;
    protected Field backpropOutputGradient;
    protected FieldOutput outputGradient;
    protected FieldOutput updateValue;
    protected FieldOutput inductionThreshold;

    protected Map<NeuronProvider, Link> inputLinks;
    protected NavigableMap<OutputKey, Link> outputLinks;

    protected SortedMap<Activation<?>, BindingSignal> bindingSignals = new TreeMap<>(
            Comparator.comparing(Activation::getId)
    );

    protected Field sameBSEvent = new Field(this, "sameBSEvent");

    protected NavigableMap<Activation<?>, BindingSignal> reverseBindingSignals = new TreeMap<>(NEURON_COMPARATOR);


    protected Activation(int id, N n) {
        this.id = id;
        this.neuron = n;
    }

    public Activation(int id, Thought t, N n) {
        this(id, n);
        this.thought = t;
        setCreated(t.getCurrentTimestamp());

        inputLinks = new TreeMap<>();
        outputLinks = new TreeMap<>(OutputKey.COMPARATOR);

        net = initNet();
        net.setPropagatePreCondition((cv, nv, u) ->
                !Utils.belowTolerance(u) && (cv >= 0.0 || nv >= 0.0)
        );
        connect(getNeuron().getBias(), net);

        isFinal = new QueueField(
                this,
                "isFinal",
                isTemplate() ? 1.0 : 0.0
        );

        isFired = threshold("isFired", 0.0, ABOVE, net);

        isFired.addEventListener(() -> {
                    fired = thought.getCurrentTimestamp();
                    Counting.add(this);
                }
        );

        isFiredForWeight = func("(isFired * 2) - 1", isFired, x -> (x * 2.0) - 1.0);
        isFiredForBias = func("(isFired * -1) + 1", isFired, x -> (x * -1.0) + 1.0);

        initFields();

        if (!getNeuron().isNetworkInput() && getConfig().isTrainingEnabled())
            initGradientFields();

        finalValue = mul(
                "finalValue",
                isFinal,
                value
        );

        thought.register(this);
        neuron.register(this);
    }

    protected Field initNet() {
        return new QueueField(this, "net", 0.0);
    }

    protected void initGradientFields() {
        if(isTemplate())
            induce();

        ownInputGradient = new QueueField(this, "Own-Input-Gradient");
        backpropInputGradient = new QueueField(this, "Backprop-Input-Gradient", 0.0);
        ownOutputGradient = new QueueField(this, "Own-Output-Gradient");
        backpropOutputGradient = new QueueField(this, "Backprop-Output-Gradient");

        entropy = func("Entropy", net, x ->
                        getNeuron().getSurprisal(
                                Sign.getSign(x),
                                getAbsoluteRange(),
                                true
                        ),
                ownInputGradient
        );

        netOuterGradient =
                func("f'(net)",
                        net,
                        x -> getNeuron().getActivationFunction().outerGrad(x)
        );

        mul(
                "ig * f'(net)",
                ownInputGradient,
                netOuterGradient,
                ownOutputGradient
        );

        mul(
                "ig * f'(net)",
                backpropInputGradient,
                netOuterGradient,
                backpropOutputGradient
        );

        outputGradient = add(
                "ownOG + backpropOG",
                ownOutputGradient,
                backpropOutputGradient
        );

        updateValue = scale(
                "learn-rate * og",
                getConfig().getLearnRate(),
                outputGradient
        );
        connect(updateValue, getNeuron().getBias());

        inductionThreshold = threshold(
                "induction threshold",
                getConfig().getInductionThreshold(),
                ABOVE_ABS,
                outputGradient
        );
    }

    public FieldOutput getIsFired() {
        return isFired;
    }

    public FieldOutput getIsFiredForWeight() {
        return isFiredForWeight;
    }

    public FieldOutput getIsFiredForBias() {
        return isFiredForBias;
    }

    public Field getIsFinal() {
        return isFinal;
    }


    public FieldOutput getEvent(boolean isFired, boolean isFinal) {
        if(isFired && isFinal)
            return mul("final * fired", this.isFinal, this.isFired);

        if(isFired)
            return this.isFired;

        if(isFinal)
            return this.isFinal;

        return null;
    }

    protected void initFields() {
        func(
                "f(net)",
                net,
                x -> getActivationFunction().f(x),
                value
        );
    }

    public FieldFunction getNetOuterGradient() {
        return netOuterGradient;
    }

    public void init(Synapse originSynapse, Activation originAct) {
        initFixedTransitionEvents();
        thought.onActivationCreationEvent(this, originSynapse, originAct);
    }

    public Field getFixedBSEvent(State s) {
        if(s == State.SAME)
            return sameBSEvent;
        return null;
    }

    public BindingSignal getFixedBindingSignal(State s) {
        List<FieldLink> inputs = getFixedBSEvent(s).getInputLinks();
        if(inputs.isEmpty())
            return null;

        FieldLink fl = inputs.get(0);
        Field onArrived = (Field) fl.getInput();
        return (BindingSignal) onArrived.getReference();
    }

    public void receiveBindingSignal(BindingSignal bs) {
        if(bs.getState() == State.SAME) {
            Fields.connect(bs.getOnArrived(), sameBSEvent);
            return;
        }

        notifyVariableTransitions(bs);
    }

    private void notifyVariableTransitions(BindingSignal bs) {
        Neuron<?, ?> n = getNeuron();

        boolean templateEnabled = getConfig().isTemplatesEnabled();
        for(Direction dir: DIRECTIONS)
            n.getTargetSynapses(dir, templateEnabled)
                    .forEach(s ->
                            s.notifyVariableTransitions(bs, dir)
                    );
    }

    private void initFixedTransitionEvents() {
        Neuron<?, ?> n = getNeuron();

        boolean templateEnabled = getConfig().isTemplatesEnabled();
        for(Direction dir: DIRECTIONS)
            n.getTargetSynapses(dir, templateEnabled)
                    .forEach(s ->
                            s.initFixedTransitions(this, dir)
                    );
    }

    public FieldOutput getEntropy() {
        return entropy;
    }

    public Field getOwnInputGradient() {
        return ownInputGradient;
    }

    public Field getBackpropInputGradient() {
        return backpropInputGradient;
    }

    public FieldOutput getOwnOutputGradient() {
        return ownOutputGradient;
    }

    public FieldOutput getBackpropOutputGradient() {
        return backpropOutputGradient;
    }

    public FieldOutput getOutputGradient() {
        return outputGradient;
    }

    public FieldOutput getUpdateValue() {
        return updateValue;
    }

    public FieldOutput getInductionThreshold() {
        return inductionThreshold;
    }

    public int getId() {
        return id;
    }

    public Field getValue() {
        return value;
    }

    public FieldOutput getFinalValue() {
        return finalValue;
    }

    public boolean isInput() {
        return false;
    }

    public Field getNet() {
        return net;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp ts) {
        this.created = ts;
    }

    public Timestamp getFired() {
        return fired;
    }

    public boolean isFired() {
        return fired != NOT_SET;
    }

    public void induce() {
        assert isTemplate();

        neuron = (N) neuron.instantiateTemplate(true);
    }

    public Thought getThought() {
        return thought;
    }

    public boolean isNetworkInput() {
        return getNeuron().isNetworkInput();
    }

    public boolean isTemplate() {
        return getNeuron().isTemplate();
    }

    public abstract Range getRange();

    public Range getAbsoluteRange() {
        Range r = getRange();
        if(r == null) return null;
        return r.getAbsoluteRange(thought.getRange());
    }

    public Stream<BindingSignal> getBindingSignals() {
        return getPatternBindingSignals().values().stream();
    }

    public BindingSignal addBindingSignal(BindingSignal bs) {
        if (bs.shorterBSExists())
            return null;

        bs.link();
        return bs;
    }

    public void propagateBindingSignal(BindingSignal fromBS) {
        getOutputLinks().forEach(l ->
                fromBS.propagate(l)
        );
    }

    public void registerBindingSignal(BindingSignal bs) {
        bindingSignals.put(bs.getOriginActivation(), bs);
        bs.getOnArrived().set(1.0);
    }

    public Map<Activation<?>, BindingSignal> getPatternBindingSignals() {
        return bindingSignals;
    }

    public void registerReverseBindingSignal(Activation targetAct, BindingSignal bindingSignal) {
        reverseBindingSignals.put(targetAct, bindingSignal);
    }

    public Stream<BindingSignal> getReverseBindingSignals(Neuron toNeuron) {
        if(toNeuron.isTemplate()) {
            return reverseBindingSignals.values().stream()
                    .filter(bs -> bs.getActivation().getNeuron().templateNeuronMatches(toNeuron));
        } else {
            return reverseBindingSignals.subMap(
                    new DummyActivation(0, toNeuron),
                    new DummyActivation(Integer.MAX_VALUE, toNeuron)
            ).values().stream();
        }
    }

    public BindingSignal getBindingSignal(Activation act) {
        return bindingSignals.get(act);
    }

    public BindingSignal getBindingSignal(State s) {
        return bindingSignals.values()
                .stream()
                .filter(bs -> bs.getState() == s)
                .findFirst()
                .orElse(null);
    }

    @Override
    public int compareTo(Activation act) {
        return ID_COMPARATOR.compare(this, act);
    }

    public OutputKey getOutputKey() {
        return new OutputKey(getNeuronProvider(), getId());
    }

    public String getLabel() {
        return getNeuron().getLabel();
    }

    public N getNeuron() {
        return neuron;
    }

    public void setNeuron(N n) {
        this.neuron = n;
    }

    public ActivationFunction getActivationFunction() {
        return neuron.getActivationFunction();
    }

    public <M extends Model> M getModel() {
        return (M) neuron.getModel();
    }

    public Config getConfig() {
        return getThought().getConfig();
    }

    public NeuronProvider getNeuronProvider() {
        return neuron.getProvider();
    }

    public Link getInputLink(Neuron n) {
        return inputLinks.get(n.getProvider());
    }

    public Link getInputLink(Synapse s) {
        return inputLinks.get(s.getPInput());
    }

    public boolean inputLinkExists(Synapse s) {
        return inputLinks.containsKey(s.getPInput());
    }

    public Stream<Link> getOutputLinks(Synapse s) {
        return outputLinks
                .subMap(
                        new OutputKey(s.getOutput().getProvider(), Integer.MIN_VALUE),
                        true,
                        new OutputKey(s.getOutput().getProvider(), MAX_VALUE),
                        true
                ).values()
                .stream()
                .filter(l -> l.getSynapse() == s);
    }

    public void linkInputs() {
        inputLinks
                .values()
                .forEach(Link::linkInput);
    }

    public void unlinkInputs() {
        inputLinks
                .values()
                .forEach(Link::unlinkInput);
    }

    public void linkOutputs() {
        outputLinks
                .values()
                .forEach(Link::linkOutput);
    }

    public void unlinkOutputs() {
        outputLinks
                .values()
                .forEach(Link::unlinkOutput);
    }

    public void link() {
        linkInputs();
        linkOutputs();
    }

    public void unlink() {
        unlinkInputs();
        unlinkOutputs();
    }

    public void disconnect() {
        FieldOutput[] fields = new FieldOutput[] {
                net,
                value,
                finalValue,
                isFired,
                isFiredForWeight,
                isFiredForBias,
                isFinal,
                entropy,
                netOuterGradient,
                ownInputGradient,
                backpropInputGradient,
                ownOutputGradient,
                backpropOutputGradient,
                outputGradient
        };

        for(FieldOutput f: fields) {
            if(f == null)
                continue;
            f.disconnect();
        }
    }

    public Stream<Link> getInputLinks() {
        return inputLinks.values().stream();
    }

    public Stream<Link> getOutputLinks() {
        return outputLinks.values().stream();
    }

    public String toString() {
        return (isTemplate() ? "Template-" : "") + getClass().getSimpleName() + " " + toKeyString();
    }

    public String toKeyString() {
        return "id:" + getId() + " n:[" + getNeuron().toKeyString() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activation)) return false;
        Activation<?> that = (Activation<?>) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
