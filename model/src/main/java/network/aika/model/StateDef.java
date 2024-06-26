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
import network.aika.elements.typedef.StateDefinition;
import network.aika.fields.UpdateListener;

import static network.aika.FieldActivationFunction.actFunc;
import static network.aika.debugger.EventType.UPDATE;
import static network.aika.elements.activations.StateType.*;
import static network.aika.fields.Fields.isTrue;
import static network.aika.fields.SumField.sum;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class StateDef {

    TypeModel typeModel;

    StateDefinition state;


    public StateDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init(String name, StateType stateType) {
        state = new StateDefinition(name, stateType)
                .setNextRound(stateType == OUTER_FEEDBACK);

        state.setNet(sum(state, "net"));

        /*
        state.net.addListener("onFired", (r, fl, u) ->
                r.updateFiredStep(fl)
        );
*/
        state.setValue(
                actFunc(
                        state,
                        "value = f(net)",
                        TOLERANCE
                )
                        .in(0, (o, p) -> o.getNet())
        );

        state.getValue().addListener("onFired", (r, fl, u) -> {
            if (isTrue(r.getField(state.value), false) != isTrue(r.getField(state.value), true))
                r.getDocument().onElementEvent(UPDATE, r.getActivation());
        });

        state.getValue().setQueued(INFERENCE);
    }
}
