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

import static network.aika.elements.typedef.FieldTags.*;
import static network.aika.elements.typedef.StateDefinition.*;
import static network.aika.fields.FieldActivationFunction.actFunc;
import static network.aika.elements.activations.StateType.*;
import static network.aika.fields.FiredListener.firedListener;
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

        sum(state, NET)
                .out((o, p) -> firedListener(o, FIRED, TOLERANCE));

        actFunc(state, VALUE, TOLERANCE)
                .in(0, (o, p) -> o.getFieldOutput(NET))
                .setQueued(INFERENCE);
    }
}
