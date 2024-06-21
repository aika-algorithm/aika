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

import network.aika.elements.typedef.StateTypeDefinition;

import static network.aika.elements.activations.StateType.*;
import static network.aika.fielddefs.FieldLinkDefinition.link;
import static network.aika.model.StateDef.NET;

/**
 *
 * @author Lukas Molzberger
 */
public class StatesDef {

    TypeModel typeModel;

    StateDef nonFeedbackState = new StateDef(typeModel);
    StateDef outerFeedbackState = new StateDef(typeModel);
    StateDef innerFeedbackState = new StateDef(typeModel);

    public StatesDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {
        nonFeedbackState.init("NonFeedbackState", NON_FEEDBACK);
        outerFeedbackState.init("OuterFeedbackState", OUTER_FEEDBACK);
        innerFeedbackState.init("InnerFeedbackState", INNER_FEEDBACK);

        link(
                nonFeedbackState.state.getFieldDef(NET),
                outerFeedbackState.state.getFieldDef(NET)
        );

        link(
                outerFeedbackState.state.getFieldDef(NET),
                innerFeedbackState.state.getFieldDef(NET)
        );
    }

    public StateTypeDefinition getNonFeedbackState() {
        return nonFeedbackState.state;
    }

    public StateTypeDefinition getOuterFeedbackState() {
        return outerFeedbackState.state;
    }

    public StateTypeDefinition getInnerFeedbackState() {
        return innerFeedbackState.state;
    }

}
