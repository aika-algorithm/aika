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
package network.aika.debugger.activations;

import network.aika.debugger.AbstractConsoleManager;
import network.aika.debugger.ElementPanel;
import network.aika.debugger.activations.properties.links.LinkPropertyPanel;
import network.aika.debugger.neurons.properties.slots.SynapseSlotPropertyPanel;
import network.aika.debugger.neurons.properties.synapses.SynapsePropertyPanel;
import network.aika.elements.links.Link;

import javax.swing.*;


/**
 * @author Lukas Molzberger
 */
public class LinkPanel extends ElementPanel {


    public LinkPanel(Link l) {

        //The following line enables to use scrolling tabs.
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        setFocusCycleRoot(true);

        initTabs(l);
    }

    private void initTabs(Link l) {
        if(l == null)
            return;

        LinkPropertyPanel actPropertyPanel = LinkPropertyPanel.create(l);
        actPropertyPanel.addFinal();
        addTab(
                "Link",
                "Shows the Link",
                actPropertyPanel
        );

        if (l.getSynInputSlot() != null) {
            SynapseSlotPropertyPanel inputSynapseSlotPropertyPanel = SynapseSlotPropertyPanel.create(l.getSynInputSlot());
            inputSynapseSlotPropertyPanel.addFinal();
            addTab(
                    "Input Synapse Slot",
                    "Shows the Input Synapse Slot",
                    inputSynapseSlotPropertyPanel
            );
        }

        SynapseSlotPropertyPanel outputSynapseSlotPropertyPanel = SynapseSlotPropertyPanel.create(l.getSynOutputSlot());
        outputSynapseSlotPropertyPanel.addFinal();
        addTab(
                "Output Synapse Slot",
                "Shows the Output Synapse Slot",
                outputSynapseSlotPropertyPanel
        );

        SynapsePropertyPanel synapsePropertyPanel = SynapsePropertyPanel.create(l.getSynapse(), l);
        synapsePropertyPanel.addFinal();
        addTab(
                "Synapse",
                "Shows the Synapse",
                synapsePropertyPanel
        );
    }

    protected void setConsoleManager(AbstractConsoleManager cm) {
        super.setConsoleManager(cm);

        setSelectedIndex(
                Math.min(
                        this.getTabCount(),
                        Math.max(
                                0,
                                consoleManager.getSelectedLinkPanelTab()
                        )
                )
        );
        addChangeListener(e -> {
            JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
            consoleManager.setSelectedLinkPanelTab(
                    sourceTabbedPane.getSelectedIndex()
            );
        });
    }

    public void remove() {
    }
}
