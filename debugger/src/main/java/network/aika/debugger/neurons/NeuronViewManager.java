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

import network.aika.Model;
import network.aika.debugger.AbstractGraphMouseManager;
import network.aika.debugger.AbstractParticleLink;
import network.aika.debugger.AbstractViewManager;
import network.aika.debugger.activations.particles.ActivationParticle;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.enums.direction.Direction;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.synapses.Synapse;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.camera.DefaultCamera2D;

import java.awt.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_X;
import static network.aika.utils.Utils.doubleToString;

/**
 * @author Lukas Molzberger
 */
public class NeuronViewManager extends AbstractViewManager<Neuron, NeuronGraphManager> {

    protected NeuronConsoleManager consoleManager;

    public NeuronViewManager(Model m, NeuronConsoleManager consoleManager) {
        super(m);

        this.consoleManager = consoleManager;
        graphManager = new NeuronGraphManager(graph);

        viewer.enableAutoLayout(graphManager);

        view = initView();
    }

    @Override
    public void showElementContext(GraphicElement ge) {
        if(ge instanceof Node) {
            Node n = (Node) ge;

            Neuron neuron = graphManager.getAikaNode(n);
            if (neuron == null)
                return;

            if(consoleManager != null)
                consoleManager.showSelectedElementContext(neuron);

        } else if(ge instanceof Edge) {
            Edge e = (Edge) ge;

            Synapse s = graphManager.getLink(e);
            if(s == null)
                return;

            consoleManager.showSelectedElementContext(s);
        }
    }

    public void showSelectedNeuron(Neuron n) {
        double[] x = new double[] {0.0};

        drawNeuron(n, 0.0, 0.0);

        drawInputSynapses(n);
        drawOutputSynapses(n);
    }


    public void expandNeuron(Neuron<?, ?> n, boolean synapsesOnly) {
        List<? extends Synapse> outputSyns = n.getOutputSynapsesAsStream()
                .limit(20)
                .toList();

        double count = outputSyns.size();

        int i = 0;
        for(Synapse s: outputSyns) {
            drawExpandedSynapse(
                    n,
                    s,
                    Direction.OUTPUT,
                    getRelativePosition(i++, count),
                    STANDARD_DISTANCE_X,
                    synapsesOnly
            );
        }

        List<Synapse> inputSyns = n.getInputSynapsesAsStream()
                .limit(20)
                .toList();

        count = inputSyns.size();

        i = 0;
        for(Synapse s: inputSyns) {
            drawExpandedSynapse(
                    n,
                    s,
                    Direction.INPUT,
                    getRelativePosition(i++, count),
                    -STANDARD_DISTANCE_X,
                    synapsesOnly
            );
        }
    }

    private double getRelativePosition(int pos, double count) {
        double stepSize = STANDARD_DISTANCE_X / Math.pow(count, 0.8);
        return (pos * stepSize) - (((count - 1) * stepSize) / 2.0);
    }

    private void drawExpandedSynapse(Neuron<?, ?> n, Synapse s, Direction dir, double relX, double relY, boolean synapsesOnly) {
        Node currentNode = graphManager.getNode(n);
        Neuron<?, ?> relN = dir.getNeuron(s);
        Node relNode = graphManager.getNode(relN);
        if(relNode == null) {
            if(synapsesOnly)
                return;

            drawNeuron(
                    relN,
                    (Double) currentNode.getAttribute("x") + relX,
                    (Double) currentNode.getAttribute("y") + relY
            );

            expandNeuron(relN, true);
        }
        drawSynapse(s);
    }

    @Override
    public void reactToAltSelection(GraphicElement ge) {
        if (ge instanceof Node) {
            Node node = (Node) ge;

            Neuron n = graphManager.getAikaNode(node);
            if (n == null)
                return;

            expandNeuron(n, false);
        }
    }

    public void viewClosed(String id) {
        //     loop = false;
    }

    public void updateGraphNeurons(boolean templateOnly) {
        Stream<Neuron> neurons = getNeurons();
        updateGraphNeurons(
                templateOnly ?
                        neurons.filter(Neuron::isAbstract).toList() :
                        neurons.toList()
        );
    }

    public void updateGraphNeurons(Collection<Neuron> neurons) {
        double[] x = new double[] {0.0};

        neurons.forEach(n -> {
            drawNeuron(n, x[0], 0.0);
            x[0] += STANDARD_DISTANCE_X;
        });

        neurons.forEach(n -> {
            drawInputSynapses(n);
            drawOutputSynapses(n);
        });
    }

    private Stream<Neuron> getNeurons() {
        return getModel()
                .getActiveNeurons()
                .stream()
                .map(NeuronProvider::getNeuron);
    }

    public void importNetworkLayout(DataInput in) throws IOException {
        getCamera().setViewPercent(in.readDouble());
        getCamera().setViewCenter(
                in.readDouble(),
                in.readDouble(),
                0.0
        );

        HashMap<String, Neuron> neuronMap = new HashMap<>();
        getNeurons().forEach(n ->
                neuronMap.put(n.getClass().getSimpleName() + ":" + n.getLabel(), n)
        );
        while(in.readBoolean()) {
            String label = in.readUTF();
            String type = in.readUTF();
            double x = in.readDouble();
            double y = in.readDouble();

            Neuron act = neuronMap.get(type + ":" + label);
            if(act != null) {
                NeuronParticle p = graphManager.getParticle(act);
                p.x = x;
                p.y = y;
            }
        }
    }

    public void exportNetworkLayout(DataOutput out) throws IOException {
        out.writeDouble(getCamera().getViewPercent());
        out.writeDouble(getCamera().getViewCenter().x);
        out.writeDouble(getCamera().getViewCenter().y);

        List<Neuron> neurons = getNeurons().toList();
        for(Neuron n: neurons) {
            NeuronParticle p = graphManager.getParticle(n);
            if(p != null && p.getPosition() != null) {
                out.writeBoolean(true);
                out.writeUTF(n.getLabel());
                out.writeUTF(n.getClass().getSimpleName());
                out.writeDouble(p.getPosition().x);
                out.writeDouble(p.getPosition().y);
            }
        }
        out.writeBoolean(false);
    }

    @Override
    public void moveNodeGroup(Node node, int x, int y) {
        Neuron<?, ?> n = getGraphManager().getAikaNode(node);
        if(!(n instanceof PatternNeuron))
            return;

        DefaultCamera2D camera = (DefaultCamera2D) getGraphView().getCamera();

        GraphicGraph gg = viewer.getGraphicGraph();
        GraphicElement gn = (GraphicElement) node;

        n.getInputSynapsesAsStream()
                .map(Synapse::getInput)
                .map(in -> getGraphManager().getNode(in))
                .filter(Objects::nonNull)
                .map(inode -> (GraphicElement) gg.getNode(inode.getId()))
                .forEach(inode -> {
                    Point3 p = camera.transformPxToGuSwing(x, y);
                    inode.move((inode.getX() - gn.getX()) + p.x, (inode.getY() - gn.getY()) + p.y, inode.getZ());
                });
    }
    @Override
    protected AbstractGraphMouseManager initMouseManager() {
        return new NeuronGraphMouseManager(this);
    }

    @Override
    public Component getConsoleManager() {
        return consoleManager;
    }

    protected void drawNeuron(Neuron<?, ?> n, double x, double y) {
        Node node = graphManager.getNode(n);
        if(node != null)
            return;

        NeuronParticle np = graphManager.lookupParticle(n);
        np.updateNode(x, y);
    }

    protected void drawInputSynapses(Neuron<?, ?> n) {
        n.getInputSynapses()
                .forEach(s ->
                        drawSynapse(s)
                );
    }

    protected void drawOutputSynapses(Neuron<?, ?> n) {
        n.getOutputSynapses()
                .forEach(s ->
                        drawSynapse(s)
                );
    }

    protected Edge drawSynapse(Synapse s) {
        if(graphManager.getNode(s.getInput()) == null || graphManager.getNode(s.getOutput()) == null)
            return null;

        AbstractParticleLink pl = graphManager.lookupParticleLink(s);

        return pl.getEdge();
    }
}
