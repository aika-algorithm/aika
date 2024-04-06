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
public class StatesDef {

    TypeModel typeModel;

    StateDef preFeedbackState = new StateDef(typeModel);
    StateDef outerFeedbackState = new StateDef(typeModel);
    StateDef innerFeedbackState = new StateDef(typeModel);

    public StatesDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {
        preFeedbackState.init("PreFeedbackState", PRE_FEEDBACK);
        outerFeedbackState.init("OuterFeedbackState", OUTER_FEEDBACK);
        innerFeedbackState.init("InnerFeedbackState", INNER_FEEDBACK);

        link(
                preFeedbackState.state.getFieldDef("net"),
                outerFeedbackState.state.getFieldDef("net")
        );

        link(
                outerFeedbackState.state.getFieldDef("net"),
                innerFeedbackState.state.getFieldDef("net")
        );
    }

    public StateTypeDefinition getPreFeedbackState() {
        return preFeedbackState.state;
    }

    public StateTypeDefinition getOuterFeedbackState() {
        return outerFeedbackState.state;
    }

    public StateTypeDefinition getInnerFeedbackState() {
        return innerFeedbackState.state;
    }


}
