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

import network.aika.neurons.Neuron;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;

/**
 * Store model
 *
 * @author Lukas Molzberger
 */
public class Save extends ElementStep<Neuron> {

    public static void add(Neuron n) {
        Step.add(new Save(n));
    }

    private Save(Neuron n) {
        super(n);
    }

    @Override
    public Phase getPhase() {
        return Phase.SAVE;
    }

    @Override
    public void process() {
        getElement()
                .save();
    }
}