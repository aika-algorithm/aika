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
package network.aika.queue.steps;

import network.aika.Document;
import network.aika.elements.activations.CategoryActivation;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;

import static network.aika.elements.activations.StateType.INNER_FEEDBACK;
import static network.aika.queue.Phase.INSTANTIATION_TRIGGER;


/**
 *
 * @author Lukas Molzberger
 */
public class InstantiationTrigger extends ElementStep<Document> {

    public static void add(Document doc) {
        if (doc.getConfig().isMetaInstantiationEnabled())
            add(new InstantiationTrigger(doc));
    }

    public InstantiationTrigger(Document doc) {
        super(doc);
    }

    @Override
    public void process() {
        Document doc = getElement();

        doc.getActivations().stream()
                .filter(act -> act.getNeuron().isInstantiable())
                .filter(act -> act.isFired(INNER_FEEDBACK))
                .forEach(Instantiation::add);
    }

    @Override
    public Phase getPhase() {
        return INSTANTIATION_TRIGGER;
    }
}
