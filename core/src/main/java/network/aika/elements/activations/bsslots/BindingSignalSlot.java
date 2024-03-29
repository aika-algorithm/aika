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
import network.aika.elements.activations.State;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.enums.Scope;
import network.aika.queue.steps.Linking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static network.aika.elements.activations.StateType.PRE_FEEDBACK;
import static network.aika.enums.Trigger.*;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class BindingSignalSlot {

    protected static final Logger log = LoggerFactory.getLogger(BindingSignalSlot.class);

    protected Activation<?> act;

    protected BSSlotDefinition slotDef;

    public BindingSignalSlot(Activation act, BSSlotDefinition slotDef) {
        this.act = act;
        this.slotDef = slotDef;
    }

    public boolean isFeedback() {
        return slotDef.isFeedback();
    }

    public static BindingSignalSlot create(Activation act, BSSlotDefinition slotDef) {
        return slotDef.isMulti() ?
                        new MultiBSSlot(act, slotDef) :
                        new SingleBSSlot(act, slotDef);
    }

    public Scope getType() {
        return slotDef.getScope();
    }

    public abstract boolean isSet();

    public abstract boolean isSet(PatternActivation bs);

    public abstract Stream<PatternActivation> getBindingSignals();

    public abstract void updateBindingSignal(PatternActivation bs, boolean state);

    protected void onBindingSignalSlotUpdate(PatternActivation bs, boolean state) {
        if(!state)
            return;

        Linking.add(act, getType(), bs, NOT_FIRED);

        if(act.isFired(PRE_FEEDBACK))
            Linking.add(act, getType(), bs, FIRED_PRE_FEEDBACK);
    }

    public void onFired(State s) {
        Stream<PatternActivation> bindingSignals =
                isFeedback() && !isSet() ?
                        Stream.of((PatternActivation) null) :
                        getBindingSignals();

        bindingSignals.forEach(bs ->
                s.getType().getTriggers()
                        .filter(t -> t.getType() == s.getType())
                        .filter(t -> t.checkPrimary(act))
                        .forEach(t ->
                                Linking.add(act, getType(), bs, t)
                        )
        );
    }

    @Override
    public String toString() {
        return getType() + (isFeedback() ? "-fb" : "") + ": " +
                getBindingSignals()
                        .map(bs -> "" + bs)
                        .collect(Collectors.joining(", "));
    }
}
