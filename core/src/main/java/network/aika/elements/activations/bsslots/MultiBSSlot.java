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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class MultiBSSlot extends BindingSignalSlot {

    private Set<Activation> bindingSignals = new HashSet<>();

    public MultiBSSlot(Activation act, BSSlotDefinition slotDef) {
        super(act, slotDef);
    }

    @Override
    public boolean isSet() {
        return !bindingSignals.isEmpty();
    }

    @Override
    public boolean isSet(Activation bs) {
        return getBindingSignals()
                .anyMatch(bsa -> bsa == bs);
    }

    @Override
    public Stream<Activation> getBindingSignals() {
        return bindingSignals.stream();
    }

    @Override
    public void updateBindingSignal(Activation bs, boolean state) {
        if (state)
            bindingSignals.add(bs);
        else
            bindingSignals.remove(bs);

        onBindingSignalSlotUpdate(bs, state);
    }
}
