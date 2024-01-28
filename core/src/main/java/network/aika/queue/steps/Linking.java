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
package network.aika.queue.steps;

import network.aika.elements.Timestamp;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingSignalSlot;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Scope;
import network.aika.enums.Trigger;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;
import network.aika.queue.keys.LinkingQueueKey;
import network.aika.visitor.UpVisitor;
import network.aika.visitor.operator.IncomingLinkingOperator;
import network.aika.visitor.operator.OutgoingLinkingOperator;
import network.aika.visitor.operator.LinkingOperator;

import static network.aika.enums.Scope.SAME;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.queue.Phase.LINKING;

/**
 *
 * @author Lukas Molzberger
 */
public class Linking extends ElementStep<Activation> {

    protected Trigger trigger;

    protected Scope bsType;

    protected PatternActivation bindingSignal;


    public static void add(Activation act, BindingSignalSlot bsSlot, Trigger trigger) {
        Step.add(new Linking(act, bsSlot, trigger));
    }

    public Linking(Activation act, BindingSignalSlot bsSlot, Trigger trigger) {
        super(act);
        this.trigger = trigger;
        this.bsType = bsSlot.getType();
        this.bindingSignal = bsSlot.getBindingSignal();
    }

    @Override
    public void createQueueKey(Timestamp timestamp, int round) {
        queueKey = new LinkingQueueKey(
                round,
                getPhase(),
                getElement(),
                trigger,
                bsType,
                timestamp
        );
    }

    @Override
    public void process() {
        Activation<?> act = getElement();
        Neuron<?, ?> n = act.getNeuron();

        if(bindingSignal != null)
            n.getInputSynapsesAsStream()
                    .filter(s ->
                            s.getRequired().getTo() == bsType
                    )
                    .forEach(targetSyn ->
                            linkIncoming(targetSyn)
                    );

        n.wakeupPropagable();

        n.getOutputSynapsesAsStream()
                .filter(s ->
                        s.getTrigger() == trigger &&
                                s.getRequired().getFrom() == bsType
                )
                .toList()
                .forEach(s -> {
                    if(bindingSignal != null)
                        linkOutgoing(s);

                    s.propagate(act);
                });
    }

    private void linkIncoming(Synapse targetSyn) {
        LinkingOperator op = new IncomingLinkingOperator(getElement(), targetSyn, bindingSignal);

        if(targetSyn.expandRelation(op, targetSyn.getOutput(), INPUT))
            return;

        new UpVisitor(bindingSignal.getDocument(), op)
                .start(bindingSignal, SAME);
    }

    private void linkOutgoing(Synapse targetSyn) {
        Activation<?> act = getElement();

        Neuron to = targetSyn.getOutput();
        LinkingOperator op = new OutgoingLinkingOperator(act, targetSyn, bindingSignal);

        if(targetSyn.expandRelation(op, to, OUTPUT))
            return;

        new UpVisitor(bindingSignal.getDocument(), op)
                    .start(bindingSignal, SAME);
    }

    @Override
    public Phase getPhase() {
        return LINKING;
    }

    @Override
    public String toString() {
        return super.toString() + " BS-Type:" + bsType + " Trigger:" + trigger + " BS:" + (bindingSignal != null ? "" + bindingSignal : "--");
    }
}
