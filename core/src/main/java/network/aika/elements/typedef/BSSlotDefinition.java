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
package network.aika.elements.typedef;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.bsslots.BindingSignalSlot;
import network.aika.elements.activations.bsslots.MultiBSSlot;
import network.aika.elements.activations.bsslots.SingleBSSlot;
import network.aika.enums.Scope;

import static network.aika.enums.Scope.INPUT;
import static network.aika.enums.Scope.SAME;

/**
 *
 * @author Lukas Molzberger
 */
public class BSSlotDefinition {

    private Scope scope;
    private boolean multi;

    private boolean isFeedback;
    private boolean isStart;

    public static BSSlotDefinition bsSlotDef(Scope scope) {
        return new BSSlotDefinition()
                .setScope(scope);
    }

    public BindingSignalSlot instantiate(Activation act) {
        BindingSignalSlot bs = isMulti() ?
                new MultiBSSlot(act, this) :
                new SingleBSSlot(act, this);

        if(isStart)
            bs.updateBindingSignal(act, true);

        return bs;
    }

    public Scope getScope() {
        return scope;
    }

    public BSSlotDefinition setScope(Scope scope) {
        this.scope = scope;

        return this;
    }

    public boolean isMulti() {
        return multi;
    }

    public BSSlotDefinition setMulti(boolean multi) {
        this.multi = multi;

        return this;
    }

    public boolean isFeedback() {
        return isFeedback;
    }

    public BSSlotDefinition setFeedback(boolean feedback) {
        isFeedback = feedback;

        return this;
    }

    public boolean isStart() {
        return isStart;
    }

    public BSSlotDefinition setStart(boolean start) {
        isStart = start;

        return this;
    }
}
