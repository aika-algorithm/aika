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
package network.aika.queue.link;

import network.aika.elements.links.Link;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;

/**
 * Counts the number of activations a particular neuron has encountered.
 *
 * @author Lukas Molzberger
 */
public class LinkCounting extends ElementStep<Link> {

    public static void add(Link l) {
        if (l.getConfig().isCountingEnabled() && !l.getOutput().getNeuron().isAbstract())
            add(new LinkCounting(l));
    }

    private LinkCounting(Link act) {
        super(act);
    }

    @Override
    public Phase getPhase() {
        return Phase.COUNTING;
    }

    @Override
    public void process() {
        Link l = getElement();

        l.getSynapse()
                .count(l);
    }
}
