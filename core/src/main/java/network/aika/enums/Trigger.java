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

import java.util.function.Predicate;

import static network.aika.elements.activations.StateType.PRE_FEEDBACK;
import static network.aika.elements.activations.StateType.WITH_FEEDBACK;

/**
 *
 * @author Lukas Molzberger
 */
public enum Trigger {
    FIRED_PRE_FEEDBACK(act -> act.isFired(PRE_FEEDBACK)),
    FIRED_WITH_FEEDBACK(act -> act.isFired(WITH_FEEDBACK)),
    NOT_FIRED(oAct -> true);

    Trigger(Predicate<Activation> check) {
        this.check = check;
    }

    private Predicate<Activation> check;

    public boolean check(Activation oAct) {
        return check.test(oAct);
    }
}
