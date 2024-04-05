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
package network.aika.elements.typedef.model;

import network.aika.elements.activations.State;
import network.aika.elements.activations.StateType;
import network.aika.elements.typedef.StateTypeDefinition;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.SumField;

import static network.aika.debugger.EventType.UPDATE;
import static network.aika.elements.activations.StateType.*;
import static network.aika.fielddefs.FieldLinkDefinition.link;
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

    private StateTypeDefinition preFeedbackState;
    private StateTypeDefinition outerFeedbackState;
    private StateTypeDefinition innerFeedbackState;

    public StateDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {
        preFeedbackState = initStateType("PreFeedbackState", PRE_FEEDBACK);
        outerFeedbackState = initStateType("OuterFeedbackState", OUTER_FEEDBACK);
        innerFeedbackState = initStateType("InnerFeedbackState", INNER_FEEDBACK);

        link(
                preFeedbackState.getFieldDef("net"),
                outerFeedbackState.getFieldDef("net")
        );

        link(
                outerFeedbackState.getFieldDef("net"),
                innerFeedbackState.getFieldDef("net")
        );
    }


    private StateTypeDefinition initStateType(String name, StateType stateType) {
        StateTypeDefinition state = new StateTypeDefinition(name, stateType)
                .setNextRound(stateType == PRE_FEEDBACK);

        FieldDefinition<State, SumField> net = new FieldDefinition<>(SumField.class, state, "net");

        net.addListener("onFired", (r, fl, u) ->
                r.updateFiredStep(fl)
        );

        FieldDefinition<State, SumField> value = func(
                state,
                "value = f(net)",
                TOLERANCE,
                net,
                (r, x) ->
                        r.getActivation().getActivationFunction().f(x)
        );

        value.addListener("onFired", (r, fl, u) -> {
            if (isTrue(r.getValue(), false) != isTrue(r.getValue(), true))
                r.getDocument().onElementEvent(UPDATE, r.getActivation());
        });

        value.setQueued(INFERENCE);

        return preFeedbackState;
    }

    public StateTypeDefinition getPreFeedbackState() {
        return preFeedbackState;
    }

    public StateTypeDefinition getOuterFeedbackState() {
        return outerFeedbackState;
    }

    public StateTypeDefinition getInnerFeedbackState() {
        return innerFeedbackState;
    }


}
