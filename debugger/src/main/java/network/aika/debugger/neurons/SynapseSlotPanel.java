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
package network.aika.debugger.neurons;

import network.aika.debugger.ElementPanel;
import network.aika.debugger.neurons.properties.slots.SynapseSlotPropertyPanel;
import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.slots.SynapseSlot;

import javax.swing.*;


/**
 * @author Lukas Molzberger
 */
public class SynapseSlotPanel extends ElementPanel {

    public SynapseSlotPanel(SynapseSlot s) {
        super();

        //The following line enables to use scrolling tabs.
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        setFocusCycleRoot(true);

        initTabs(s);
    }

    private void initTabs(SynapseSlot s) {
        if(s == null)
            return;

        AbstractPropertyPanel synapseSlotPropertyPanel = SynapseSlotPropertyPanel.create(s);

        synapseSlotPropertyPanel.addFinal();
        addTab(
                s.getDirection() + " Synapse Slot",
                "Shows the " + s.getDirection() + " Synapse",
                synapseSlotPropertyPanel
        );
    }
}
