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
package network.aika.debugger.neurons.properties.slots;

import network.aika.debugger.activations.properties.links.LinkPropertyPanel;
import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.slots.ConjunctiveSynapseSlot;
import network.aika.elements.synapses.slots.SynapseSlot;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public class SynapseSlotPropertyPanel<S extends SynapseSlot> extends AbstractPropertyPanel {

    public SynapseSlotPropertyPanel(S s) {
        initSlotProperties(s);
        initLinks(s);
    }

    public void initSlotProperties(S s) {
        addTitle(s.getClass().getSimpleName());
        addConstant("Activation Id: ", "" + s.getActivation().getId());
        addConstant("Activation Label: ", "" + s.getActivation().getLabel());
        addConstant("Synapse Id: ", "" + s.getSynapse().getSynapseId());
    }

    protected void initLinks(S s) {
        Stream<? extends Link> links = s.getLinks();

        links.limit(10)
                .forEach(l -> {
                            addEntry(LinkPropertyPanel.create(l));
                            addSeparator();
                        }
                );

        addFinal();
    }

    public static SynapseSlotPropertyPanel create(SynapseSlot s) {
        if(s instanceof ConjunctiveSynapseSlot) {
            return ConjunctiveSynapseSlotPropertyPanel.create((ConjunctiveSynapseSlot) s);
        }

        return new SynapseSlotPropertyPanel(s);
    }
}
