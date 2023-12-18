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
package network.aika.elements.activations;

import network.aika.enums.Trigger;

import static network.aika.enums.Trigger.*;

/**
 *
 * @author Lukas Molzberger
 */
public enum StateType {
    PRE_FEEDBACK(FIRED_PRE_FEEDBACK),
    NEGATIVE_FEEDBACK(FIRED_NEGATIVE_FEEDBACK),
    POSITIVE_FEEDBACK(FIRED_POSITIVE_FEEDBACK);

    private Trigger trigger;

    StateType(Trigger trigger) {
        this.trigger = trigger;
    }

    public Trigger getTrigger() {
        return trigger;
    }
}
