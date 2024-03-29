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
package network.aika.fielddefs;

import network.aika.fields.FieldFunction;
import network.aika.fields.SumField;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import org.junit.jupiter.api.Test;

import static network.aika.debugger.EventType.UPDATE;
import static network.aika.fielddefs.Operators.func;
import static network.aika.fields.Fields.isTrue;
import static network.aika.utils.ToleranceUtils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class FieldInstantiationTest {

    @Test
    public void testFieldInstantiation() {
        FieldObjectDefinition state = new FieldObjectDefinition();

        FieldDefinition net = new FieldDefinition(SumField.class, state, "net");

        FieldDefinition value = func(
                state,
                "value = f(net)",
                TOLERANCE,
                net,
                x -> Math.max(0.0, Math.tanh(x))
        );

        QueueProvider queueProvider = new QueueProvider() {
            @Override
            public Queue getQueue() {
                return null;
            }

            @Override
            public boolean isNextRound() {
                return false;
            }
        };

        value.setQueued(queueProvider, null);
    }
}
