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
import network.aika.debugger.activations.properties.*;
import network.aika.debugger.activations.properties.LinksPropertyPanel;
import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.enums.direction.Direction;
import network.aika.elements.activations.Activation;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * @author Lukas Molzberger
 */
public class ActivationPanel extends ElementPanel implements MouseListener {

    public ActivationPanel(Activation act) {
        addMouseListener(this);

        //The following line enables to use scrolling tabs.
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        setFocusCycleRoot(true);

        initTabs(act);
    }

    public ActivationPanel(ActivationConsoleManager consoleManager, Activation act) {
        this(act);
        this.consoleManager = consoleManager;
    }

    private void initTabs(Activation act) {
        if(act == null)
            return;

        AbstractPropertyPanel actPropertyPanel = AbstractPropertyPanel.create(act);
        actPropertyPanel.addFinal();
        addTab(
                "Activation",
                "Shows the Activation",
                actPropertyPanel
        );

        AbstractPropertyPanel neuronPropertyPanel = AbstractPropertyPanel.createNeuralElement(act);
        neuronPropertyPanel.addFinal();
        addTab(
                "Neuron",
                "Shows the Neuron",
                neuronPropertyPanel
        );

        {
            AbstractPropertyPanel linksPropertyPanel = LinksPropertyPanel.create(act, Direction.INPUT);
            if (linksPropertyPanel != null)
                addTab(
                        "Input-Links",
                        "Shows the Input-Links",
                        linksPropertyPanel
                );
        }

        {
            AbstractPropertyPanel linksPropertyPanel = LinksPropertyPanel.create(act, Direction.OUTPUT);
            if (linksPropertyPanel != null)
                addTab(
                        "Output-Links",
                        "Shows the Output-Links",
                        linksPropertyPanel
                );
        }

        {
            AbstractPropertyPanel synapsesPropertyPanel = SynapsesPropertyPanel.create(act, Direction.INPUT);
            if (synapsesPropertyPanel != null)
                addTab(
                        "Input-Synapses",
                        "Shows the Input-Synapses",
                        synapsesPropertyPanel
                );
        }
        {
            AbstractPropertyPanel synapsesPropertyPanel = SynapsesPropertyPanel.create(act, Direction.OUTPUT);
            if (synapsesPropertyPanel != null)
                addTab(
                        "Output-Synapses",
                        "Shows the Output-Synapses",
                        synapsesPropertyPanel
                );
        }

        if(act.isAbstract()) {
            AbstractPropertyPanel templateInstancesPropertyPanel = TemplateInstancesPropertyPanel.create(act);
            if (templateInstancesPropertyPanel != null)
                addTab(
                        "Template-Instances",
                        "Shows the Template-Instances",
                        templateInstancesPropertyPanel
                );
        }

        ActivationsPropertyPanel activationsPanel = ActivationsPropertyPanel.create(act.getNeuron());

        addTab(
                "Activations",
                "Activations of this Neurons",
                activationsPanel
        );

        TextReferencesPropertyPanel preActivationPanel = TextReferencesPropertyPanel.create(act.getNeuron());

        addTab(
                "Text-References",
                "Text-References of the Activations of this Neuron",
                preActivationPanel
        );
    }

    protected void setConsoleManager(AbstractConsoleManager cm) {
        super.setConsoleManager(cm);

        setSelectedIndex(consoleManager.getSelectedActPanelTab());
        addChangeListener(e -> {
            JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
            consoleManager.setSelectedActPanelTab(
                    sourceTabbedPane.getSelectedIndex()
            );
        });
    }

    @Override
    protected void addTab(String title, String tip, AbstractPropertyPanel panel) {
        super.addTab(title, tip, panel);
        panel.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    private void doPop(MouseEvent e) {
        ActivationPanelContextMenu menu = new ActivationPanelContextMenu((ActivationConsoleManager) consoleManager);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
