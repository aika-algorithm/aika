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
package network.aika.enums;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.StateType;

import static network.aika.elements.activations.StateType.*;

/**
 *
 * @author Lukas Molzberger
 */
public enum Trigger {
    FIRED_PRE_FEEDBACK(PRE_FEEDBACK),
    FIRED_OUTER_FEEDBACK(OUTER_FEEDBACK),
    PRIMARY_CHECKED_FIRED_OUTER_FEEDBACK(OUTER_FEEDBACK, true),
    FIRED_INNER_FEEDBACK(INNER_FEEDBACK),
    NOT_FIRED(null);

    private boolean checkPrimary;
    private StateType type;

    Trigger(StateType type) {
        this.type = type;
        if(type != null)
            type.addTrigger(this);
    }

    Trigger(StateType type, boolean checkPrimary) {
        this(type);
        this.checkPrimary = checkPrimary;
    }

    public void setType(StateType type) {
        this.type = type;
    }

    public StateType getType() {
        return type;
    }

    public boolean check(Activation act) {
        if(!checkPrimary(act))
            return false;

        if(type == null)
            return true;

        return act.isFired(type);
    }

    public boolean match(Trigger t) {
        return type == t.type && checkPrimary == t.checkPrimary;
    }

    public boolean checkPrimary(Activation act) {
        return !checkPrimary || act.checkPrimary();
    }
}
