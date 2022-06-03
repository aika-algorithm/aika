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
package network.aika.neuron.bindingsignal;

import network.aika.Config;
import network.aika.Thought;
import network.aika.direction.Direction;
import network.aika.fields.Field;
import network.aika.fields.FieldOutput;
import network.aika.fields.QueueField;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Element;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.Timestamp;
import network.aika.steps.InnerQueue;

import java.util.stream.Stream;

import static network.aika.fields.Fields.mul;


/**
 * @author Lukas Molzberger
 */
public class BindingSignal<A extends Activation> extends InnerQueue implements Element {

    private BindingSignal parent;
    private A activation;
    private Link link;
    private SingleTransition transition;
    private BindingSignal origin;
    private int depth;
    private State state;

    private Field onArrived;
    private FieldOutput onArrivedFired;

    public BindingSignal(A act, State state) {
        this.origin = this;
        this.depth = 0;
        this.state = state;

        init(act);
    }

    private BindingSignal(BindingSignal parent) {
        this.parent = parent;
        this.origin = parent.getOrigin();
        this.depth = parent.depth + 1;
    }

    public BindingSignal(BindingSignal parent, SingleTransition t) {
        this(parent);

        this.transition = t;
        this.state = t.next(Direction.OUTPUT);
    }

    public void init(A act) {
        this.activation = act;
        onArrived = new QueueField(this, "arrived", 0.0);
        onArrived.addEventListener(() ->
                activation.receiveBindingSignal(this)
        );

        initFields();
    }

    public void propagate(Link l) {
        SingleTransition t = transition(l.getSynapse());
        if(t == null)
            return;

        BindingSignal toBS = next(t);
        toBS.setLink(l);

        Activation oAct = l.getOutput();
        toBS.init(oAct);
        oAct.addBindingSignal(toBS);
    }

    public BindingSignal propagate(Synapse s) {
        return next(transition(s));
    }

    public SingleTransition transition(Synapse s) {
        if(depth >= 3)
            return null;

        Stream<Transition> transitions = s.getTransitions();
        return transitions
                .flatMap(t -> t.getBSPropagateTransitions(state))
                .findFirst()
                .orElse(null);
    }

    public BindingSignal next(SingleTransition t) {
        return t != null ?
                new BindingSignal(this, t) :
                null;
    }

    public void setLink(Link l) {
        this.link = l;
    }

    private void initFields() {
        if (!activation.getNeuron().isNetworkInput()) {

            if(state == State.INPUT && activation.getLabel() == null) {
                onArrived.addEventListener(() ->
                        activation.getNeuron().setLabel(
                                activation.getConfig().getLabel(this)
                        )
                );
            }
        }

        onArrivedFired = mul(
                "onFired * onArrived",
                activation.getIsFired(),
                onArrived
        );

        onArrivedFired.addEventListener(() ->
                getActivation().propagateBindingSignal(this)
        );
    }

    public Field getOnArrived() {
        return onArrived;
    }

    public boolean isOrigin() {
        return this == origin;
    }

    public BindingSignal getOrigin() {
        return origin;
    }

    public A getActivation() {
        return activation;
    }

    public Link getLink() {
        return link;
    }

    public SingleTransition getTransition() {
        return transition;
    }

    public int getDepth() {
        return depth;
    }

    public static boolean originEquals(BindingSignal bsA, BindingSignal bsB) {
        return bsA != null && bsB != null && bsA.getOrigin() == bsB.getOrigin();
    }

    public State getState() {
        return state;
    }

    public Activation getOriginActivation() {
        return origin.getActivation();
    }

    public void link() {
        getActivation().registerBindingSignal(this);
        getOriginActivation().registerReverseBindingSignal(getActivation(), this);
    }

    public boolean shorterBSExists() {
        BindingSignal existingBS = getActivation().getBindingSignal(getOriginActivation());
        if(existingBS == null)
            return false;

        return existingBS.getState() == state &&
                existingBS.depth <= depth;
    }

    public boolean isSelfRef(BindingSignal outputBS) {
        if(this == outputBS)
            return true;

        if(parent == null)
            return false;

        return parent.isSelfRef(outputBS);
    }

    public String toString() {
        return getOriginActivation().getId() + ":" + getOriginActivation().getLabel() + ", depth:" + getDepth() + ", state:" + state;
    }

    @Override
    public Timestamp getCreated() {
        return getOriginActivation().getCreated();
    }

    @Override
    public Timestamp getFired() {
//        if(state == State.BRANCH)
//            return Timestamp.NOT_SET;
        return getOriginActivation().getFired();
    }

    @Override
    public Thought getThought() {
        return activation.getThought();
    }

    @Override
    public Config getConfig() {
        return activation.getConfig();
    }
}
