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
import network.aika.enums.Scope;

import static network.aika.enums.Scope.INPUT;
import static network.aika.enums.Scope.SAME;

/**
 *
 * @author Lukas Molzberger
 */
public enum BSSlotDefinition {
    SINGLE_SAME(SAME, false, false, false),
    SINGLE_SAME_START(SAME, false, false, true),
    SINGLE_SAME_FEEDBACK(SAME, false, true, false),
    SINGLE_INPUT(INPUT, false, false, false),
    MULTI_INPUT(INPUT, true, false, false);

    private Scope scope;
    private boolean multi;

    private boolean isFeedback;
    private boolean isStart;

    BSSlotDefinition(Scope scope, boolean multi, boolean isFeedback, boolean isStart) {
        this.scope = scope;
        this.multi = multi;
        this.isFeedback = isFeedback;
        this.isStart = isStart;
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

    public boolean isMulti() {
        return multi;
    }

    public boolean isFeedback() {
        return isFeedback;
    }
}
