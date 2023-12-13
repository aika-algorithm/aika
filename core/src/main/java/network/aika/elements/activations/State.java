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
import network.aika.elements.Timestamp;
import network.aika.fields.*;
import network.aika.queue.steps.Fired;

import static network.aika.elements.Timestamp.NOT_SET;
import static network.aika.fields.Fields.func;
import static network.aika.fields.Fields.isTrue;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class State implements FieldObject {

    protected StateType type;

    protected Activation act;

    protected SumField net;

    protected FieldFunction value;

    protected Timestamp fired = NOT_SET;
    protected Fired firedStep = new Fired(this);


    public State(Activation act, StateType type) {
        this.act = act;
        this.type = type;

        init();
    }

    protected void init() {
        net = new SumField(this, "net", null);

        net.addListener("onFired", (fl, u) ->
                updateFiredStep(fl)
        );

        value = func(
                this,
                "value = f(net)",
                TOLERANCE,
                net,
                x -> act.getActivationFunction().f(x)
        );
        value.setQueued(act.getDocument(), INFERENCE);
    }

    private void updateFiredStep(AbstractFieldLink fl) {
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
        return type;
    }

    public FieldOutput getValue() {
        return value;
    }

    public void setNet(double v) {
        net.setValue(v);
    }

    public SumField getNet() {
        return net;
    }

    public Timestamp getFired() {
        return fired;
    }

    public void setFired() {
        fired = getDocument().getCurrentTimestamp();
    }

    public boolean isFired() {
        return isTrue(value);
    }

    @Override
    public void disconnect() {
        net.disconnectAndUnlinkInputs(false);
    }

    @Override
    public Document getDocument() {
        return act.getDocument();
    }

    public Activation<?> getActivation() {
        return act;
    }

    public String toString() {
        return act.toKeyString() + " - " + type;
    }
}
