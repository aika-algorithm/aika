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
package network.aika.model;

import network.aika.elements.activations.StateType;
import network.aika.elements.typedef.StateTypeDefinition;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.SumField;

import static network.aika.debugger.EventType.UPDATE;
import static network.aika.elements.activations.StateType.*;
import static network.aika.fielddefs.Operators.func;
import static network.aika.fields.Fields.isTrue;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class StateDef {

    TypeModel typeModel;

    StateTypeDefinition state;


    public StateDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init(String name, StateType stateType) {
        state = new StateTypeDefinition(name, stateType)
                .setNextRound(stateType == OUTER_FEEDBACK);

        state.net = new FieldDefinition<>(SumField.class, state, "net");

        state.net.addListener("onFired", (r, fl, u) ->
                r.updateFiredStep(fl)
        );

        state.value = func(
                state,
                "value = f(net)",
                TOLERANCE,
                state.net,
                (r, x) ->
                        r.getActivation().getActivationFunction().f(x)
        );

        state.value.addListener("onFired", (r, fl, u) -> {
            if (isTrue(r.getField(state.value), false) != isTrue(r.getField(state.value), true))
                r.getDocument().onElementEvent(UPDATE, r.getActivation());
        });

        state.value.setQueued(INFERENCE);
    }
}
