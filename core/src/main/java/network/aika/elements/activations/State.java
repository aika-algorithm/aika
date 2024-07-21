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
package network.aika.elements.activations;

import network.aika.Document;
import network.aika.elements.typedef.StateDefinition;
import network.aika.elements.typedef.Type;
import network.aika.fields.*;
import network.aika.fields.link.FieldLink;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import network.aika.queue.Timestamp;
import network.aika.queue.steps.Fired;

import static network.aika.fields.Fields.isTrue;
import static network.aika.queue.Timestamp.NOT_SET;

/**
 *
 * @author Lukas Molzberger
 */
public class State extends Type<StateDefinition, State> implements QueueProvider {

    protected Activation act;

    protected Timestamp fired = NOT_SET;
    protected Fired firedStep = new Fired(this);


    public State(Activation act) {
        this.act = act;
    }

    private Field getValue() {
        return getField(this.getObjectDefinition().getField(StateDefinition.VALUE));
    }

    public void updateFiredStep(FieldLink fl) {
        FieldOutput net = fl.getInput();
        if(!net.exceedsThreshold() || fired != NOT_SET)
            return;

        Document doc = getDocument();
        if(firedStep.isQueued())
            doc.removeStep(firedStep);

        firedStep.updateNet(net.getUpdatedValue());
        doc.addStep(firedStep);
    }

    public StateType getType() {
        return typeDef.getType();
    }

    public Timestamp getFired() {
        return fired;
    }

    public void setFired() {
        fired = getDocument().getCurrentTimestamp();
    }

    public void setFired(Timestamp f) {
        fired = f;
    }

    public boolean isFired() {
        return isTrue(getValue(), true);
    }

    @Override
    public Queue getQueue() {
        return act.getQueue();
    }

    @Override
    public boolean isNextRound() {
        return typeDef.isNextRound();
    }

    public Document getDocument() {
        return act.getDocument();
    }

    public Activation getActivation() {
        return act;
    }

    public String toString() {
        return act.toString() + " type:" + typeDef;
    }
}
