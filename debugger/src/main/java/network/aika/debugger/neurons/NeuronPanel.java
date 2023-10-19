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
import network.aika.debugger.activations.properties.ActivationsPropertyPanel;
import network.aika.debugger.activations.properties.PreActivationPropertyPanel;
import network.aika.debugger.activations.properties.PropagablePropertyPanel;
import network.aika.debugger.activations.properties.SynapsesPropertyPanel;
import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.enums.direction.Direction;
import network.aika.elements.neurons.Neuron;

import javax.swing.*;


/**
 * @author Lukas Molzberger
 */
public class NeuronPanel extends ElementPanel {


    public NeuronPanel(Neuron n) {
        super();

        //The following line enables to use scrolling tabs.
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        setFocusCycleRoot(true);

        initTabs(n);
    }


    private void initTabs(Neuron n) {
        if(n == null)
            return;

        AbstractPropertyPanel neuronPropertyPanel = AbstractPropertyPanel.create(n);

        neuronPropertyPanel.addFinal();
        addTab(
                "Neuron",
                "Shows the Neuron",
                neuronPropertyPanel
        );


        ActivationsPropertyPanel activationsPanel = ActivationsPropertyPanel.create(n);

        addTab(
                "Activations",
                "Shows the Activations of this Neuron",
                activationsPanel
        );

        PreActivationPropertyPanel preActivationPanel = PreActivationPropertyPanel.create(n);

        addTab(
                "Pre-Activation",
                "Shows the Pre-Activation of this Neuron",
                preActivationPanel
        );

        {
            AbstractPropertyPanel inputSynapsesPropertyPanel = SynapsesPropertyPanel.create(n, Direction.INPUT);

            addTab(
                    "Input Synapses",
                    "Shows the Input Synapses",
                    inputSynapsesPropertyPanel
            );
        }
        {
            AbstractPropertyPanel inputSynapsesPropertyPanel = SynapsesPropertyPanel.create(n, Direction.OUTPUT);

            addTab(
                    "Output Synapses",
                    "Shows the Output Synapses",
                    inputSynapsesPropertyPanel
            );
        }
        {
            AbstractPropertyPanel propagablePropertyPanel = PropagablePropertyPanel.create(n);

            addTab(
                    "Propagable Neurons",
                    "Shows the Propagable Neurons",
                    propagablePropertyPanel
            );
        }
    }
}
