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

import network.aika.elements.links.Link;
import network.aika.elements.links.types.SameObjectLink;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.types.SameObjectSynapse;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;


import static network.aika.queue.Phase.POST_INSTANTIATION;

/**
 *
 * @author Lukas Molzberger
 */
public class PostInstantiation extends ElementStep<SameObjectLink> {

    private SameObjectLink templateLink;

    public static void add(SameObjectLink l, SameObjectLink tl) {
        Step.add(new PostInstantiation(l, tl));
    }

    public PostInstantiation(SameObjectLink l, SameObjectLink tl) {
        super(l);
        this.templateLink = tl;
    }

    @Override
    public void process() {
        SameObjectLink l = getElement();

        SameObjectSynapse templateSyn = templateLink.getSynapse();
        Synapse templateRelSynapse = templateSyn
                .getPOutput()
                .getSynapseBySynId(templateSyn.getRelationSynId());

        Link templateRelLink = templateLink.getOutput().getInputLinks(templateRelSynapse)
                .findFirst()
                .orElse(null);

        l.getSynapse().setRelationSynId(
                templateRelLink.getInstanceSynapseId()
        );
    }

    @Override
    public Phase getPhase() {
        return POST_INSTANTIATION;
    }
}
