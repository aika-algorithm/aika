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
import network.aika.elements.typedef.BSSlotDefinition;

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class SingleBSSlot extends BindingSignalSlot {

    private Activation bindingSignal;

    private int sourcesCount;

    public SingleBSSlot(Activation act, BSSlotDefinition slotDef) {
        super(act, slotDef);
    }

    @Override
    protected void onBindingSignalSlotUpdate(Activation bs, boolean state) {
        super.onBindingSignalSlotUpdate(bs, state);

        act.propagateBindingSignal(getType(), bs, state);
    }

    @Override
    public boolean isSet() {
        return bindingSignal != null;
    }

    @Override
    public boolean isSet(Activation bs) {
        return bindingSignal == bs;
    }

    public Activation getBindingSignal() {
        return bindingSignal;
    }

    @Override
    public Stream<Activation> getBindingSignals() {
        return bindingSignal != null ?
                Stream.of(bindingSignal) :
                Stream.empty();
    }

    @Override
    public void updateBindingSignal(Activation bs, boolean state) {
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

        if(state != lastState) {
            onBindingSignalSlotUpdate(bindingSignal, state);

            act.getInputLinks()
                    .filter(l -> l.getSynapse().getRequired().getTo() == getType())
                    .forEach(l ->
                            l.onOutputBindingSignalUpdate(getType(), bindingSignal, state)
                    );
        }
    }

    @Override
    public String toString() {
        return super.toString() + " (" + sourcesCount + ")";
    }
}
