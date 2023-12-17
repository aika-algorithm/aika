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

import network.aika.elements.activations.types.PatternActivation;
import network.aika.enums.Scope;
import network.aika.queue.steps.Linking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.elements.activations.StateType.PRE_FEEDBACK;
import static network.aika.enums.Trigger.*;

/**
 *
 * @author Lukas Molzberger
 */
public class BindingSignalSlot {

    protected static final Logger log = LoggerFactory.getLogger(BindingSignalSlot.class);

    private Activation<?> act;

    private int sourcesCount;

    private boolean isFeedback;

    private PatternActivation bindingSignal;

    private Scope type;

    public BindingSignalSlot(Activation act, Scope type, boolean isFeedback) {
        this.act = act;
        this.type = type;
        this.isFeedback = isFeedback;
    }

    public Scope getType() {
        return type;
    }

    public boolean isSet() {
        return bindingSignal != null;
    }

    public PatternActivation getBindingSignal() {
        return bindingSignal;
    }

    public void connectBindingSignal(PatternActivation bs, boolean state) {
        if(bs == null)
            return;

        boolean lastState = sourcesCount > 0;

        if(state) {
            sourcesCount++;
            bindingSignal = bs;
        } else {
            sourcesCount--;
            if(sourcesCount <= 0)
                this.bindingSignal = null;
        }

        if(state && !lastState) {
            Linking.add(act, this, NOT_FIRED);

            if(act.isFired(PRE_FEEDBACK))
                Linking.add(act, this, FIRED_PRE_FEEDBACK);

            act.propagateBindingSignal(type, bindingSignal, state);
        }

        if(state != lastState)
            act.getInputLinks()
                    .filter(l -> l.getSynapse().getRequired().getTo() == type)
                    .forEach(l ->
                            l.onOutputBindingSignalChange(this, state)
                    );
    }

    public void onFired(State s) {
        if(isFeedback || isSet())
            Linking.add(act, this, s.getType().getTrigger());
    }

    public String toString() {
        return type + (isFeedback ? "-fb" : "") + ": " + (bindingSignal != null ? bindingSignal : "--") + " (" + sourcesCount + ")";
    }
}
