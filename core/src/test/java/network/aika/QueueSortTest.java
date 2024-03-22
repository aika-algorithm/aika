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
package network.aika;

import network.aika.queue.Timestamp;
import network.aika.queue.keys.FieldQueueKey;
import network.aika.queue.Phase;
import network.aika.queue.keys.QueueKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

/**
 *
 * @author Lukas Molzberger
 */
public class QueueSortTest {

    @Test
    public void testQueueSorting() {
        TreeMap<QueueKey, Integer> testQueue = new TreeMap<>(QueueKey.COMPARATOR);
/*
        testQueue.put(new TestQueueKey(null, 321, 0.0, 385), 1);
        testQueue.put(new TestQueueKey(337l, 316, 0.0, 384), 2);
*/
        QueueKey tqk3 = new FieldQueueKey(0, Phase.INFERENCE, -1802,  new Timestamp(78));

        testQueue.put(tqk3, 3);

        testQueue.put(new FieldQueueKey(0, Phase.INFERENCE,  0,  new Timestamp(393)), 2);
        testQueue.put(new FieldQueueKey(0, Phase.INFERENCE, 0,  new Timestamp(339)), 1);

        Integer removedStep = testQueue.remove(tqk3);

        Assertions.assertNotNull(removedStep);
    }
}
