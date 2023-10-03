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


import network.aika.debugger.EventType;
import network.aika.debugger.AbstractParticle;
import network.aika.debugger.activations.LayoutState;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.miv.pherd.geom.Point3;

import java.util.Random;
import java.util.function.Consumer;

import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_Y;

/**
 * @author Lukas Molzberger
 */
public class NeuronParticle extends AbstractParticle<NeuronGraphManager> {

    Neuron neuron;

    public NeuronParticle(NeuronGraphManager graphManager, Node n, Neuron neuron) {
        super(graphManager, n.getId(), n, 0.0, 0.0, 0.0);

        Long originNeuronId = n.getAttribute("aika.originNeuronId", Long.class);

        Double x;
        Double y;

        if(originNeuronId != null) {
            NeuronParticle originParticle = graphManager.getParticle(originNeuronId);
            Point3 originPos = originParticle.getPosition();

            x = originPos.x;
            y = originPos.y + STANDARD_DISTANCE_Y;
        } else {
            x = (Double) n.getAttribute("x");
            y = (Double) n.getAttribute("y");

            if(x == null)
                x = 0.0;

            if(y == null)
                y = 0.0;
        }

        Random random = graphManager.getRandom();
        x += (random.nextDouble() - 0.5) * 0.1;
        y += (random.nextDouble() - 0.5) * 0.1;

        initPos( x, y, 0.0);
        this.neuron = neuron;
        graphManager.setParticle(neuron, this);
    }

    @Override
    public void processLayout(LayoutState ls) {

    }

    @Override
    public void onEvent(EventType et) {
        updateNode(x, y);
    }

    public void updateNode(double x , double y) {
        node.setAttribute("aika.neuronId", neuron.getId());
        Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(neuron.getClass());
        if (neuronTypeModifier != null) {
            neuronTypeModifier.accept(node);
        }
        node.setAttribute("ui.label", neuron.getLabel());
        node.setAttribute("x", x);
        node.setAttribute("y", y);
    }

    @Override
    protected void attraction(Vector3 delta) {
        SpringBox box = (SpringBox) this.box;
        Energies energies = box.getEnergies();

        for (EdgeSpring edge : neighbours) {
            if (!edge.ignored) {
                Synapse s = lookupSynapse(edge);

               // edgeAttraction(delta, edge, energies);
            }
        }
    }

    private Synapse lookupSynapse(EdgeSpring edge) {
        NeuronParticle linkedNP = (NeuronParticle) edge.getOpposite(this);

        Synapse os = neuron.getOutputSynapse(linkedNP.neuron.getProvider());
        if(os != null)
            return os;

        return neuron.getInputSynapse(linkedNP.neuron.getProvider());
    }
}
