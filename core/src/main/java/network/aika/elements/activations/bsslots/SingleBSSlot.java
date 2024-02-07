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
package network.aika.elements.activations.bsslots;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.enums.Scope;

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class SingleBSSlot extends BindingSignalSlot {

    private PatternActivation bindingSignal;

    private int sourcesCount;

    public SingleBSSlot(Activation act, Scope type, boolean isFeedback) {
        super(act, type, isFeedback);
    }

    @Override
    protected void onBindingSignalSlotFilled(PatternActivation bs) {
        super.onBindingSignalSlotFilled(bs);

        act.propagateBindingSignal(type, bs, true);
    }

    @Override
    public boolean isSet() {
        return bindingSignal != null;
    }

    public PatternActivation getBindingSignal() {
        return bindingSignal;
    }

    @Override
    public Stream<PatternActivation> getBindingSignals() {
        return bindingSignal != null ?
                Stream.of(bindingSignal) :
                Stream.empty();
    }

    @Override
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

        if(state && !lastState)
            onBindingSignalSlotFilled(bindingSignal);

        if(state != lastState)
            act.getInputLinks()
                    .filter(l -> l.getSynapse().getRequired().getTo() == type)
                    .forEach(l ->
                            l.onOutputBindingSignalChange(type, bindingSignal, state)
                    );
    }

    @Override
    public String toString() {
        return super.toString() + " (" + sourcesCount + ")";
    }
}
