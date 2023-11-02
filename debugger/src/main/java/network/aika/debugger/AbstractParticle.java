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
package network.aika.debugger;

import network.aika.debugger.activations.LayoutState;
import network.aika.elements.neurons.*;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.GraphCellData;
import org.graphstream.ui.layout.springbox.NodeParticle;
import org.graphstream.ui.layout.springbox.implementations.SpringBoxNodeParticle;
import org.miv.pherd.Particle;
import org.miv.pherd.geom.Point3;
import org.miv.pherd.ntree.Cell;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * @author Lukas Molzberger
 */
public abstract class AbstractParticle<G extends AbstractGraphManager> extends SpringBoxNodeParticle {

    public static double K1Attr = 0.0001;

    public double x;
    public double y;

    protected boolean manuallyMoved;

    protected Map<Long, AbstractParticleLink> inputParticleLinks = new TreeMap<>();
    protected Map<Long, AbstractParticleLink> outputParticleLinks = new TreeMap<>();


    /**
     * Default repulsion.
     */
    protected double K2 = 0.000005f; // 0.12 ??

    protected Node node;

    protected G graphManager;

    public AbstractParticle(G graphManager, String id, Node n, double x, double y, double z) {
        super(graphManager, id, x, y, z);
        this.graphManager = graphManager;
        this.node = n;
    }

    public void setManuallyMoved(boolean mm) {
        manuallyMoved = mm;
    }

    public boolean isManuallyMoved() {
        return manuallyMoved;
    }

    public void addInputParticleLink(AbstractParticleLink pl) {
        inputParticleLinks.put(
                pl.getInputId(),
                pl
        );
    }

    public AbstractParticleLink getInputParticleLink(Long inputId) {
        return inputParticleLinks.get(inputId);
    }

    public void addOutputParticleLink(AbstractParticleLink pl) {
        outputParticleLinks.put(
                pl.getOutputId(),
                pl
        );
    }

    public AbstractParticleLink getOutputParticleLink(Long outputId) {
        return outputParticleLinks.get(outputId);
    }

    public Node getNode() {
        return node;
    }

    public abstract void processLayout(LayoutState ls);

    public abstract void onEvent(EventType et);

    @Override
    protected void repulsionN2(Vector3 delta) {
    }

    @Override
    protected void repulsionNLogN(Vector3 delta) {
    }

    protected void recurseRepulsion(Cell cell, Vector3 delta) {
        AbstractGraphManager box = (AbstractGraphManager) this.box;
        boolean is3D = box.is3D();
        Energies energies = box.getEnergies();

        if (intersection(cell)) {
            if (cell.isLeaf()) {
                Iterator<? extends Particle> i = cell.getParticles();

                while (i.hasNext()) {
                    AbstractParticle node = (AbstractParticle) i.next();

                    if (node != this) {
                        delta.set(node.pos.x - pos.x, 0.0, 0.0);
//                        delta.set(node.pos.x - pos.x, node.pos.y - pos.y, is3D ? node.pos.z - pos.z : 0);

                        double len = delta.normalize();

                        if (len > 0)// && len < ( box.k * box.viewZone ) )
                        {
                            if (len < box.k)
                                len = box.k; // XXX NEW To prevent infinite
                            // repulsion.
                            double factor = ((K2 / (len * len)) * node.weight);
                            energies.accumulateEnergy(factor); // TODO check
                            // this
                            repE += factor;
                            delta.scalarMult(-factor);
                            disp.add(delta);
                        }
                    }
                }
            } else {
                int div = cell.getSpace().getDivisions();

                for (int i = 0; i < div; i++)
                    recurseRepulsion(cell.getSub(i), delta);
            }
        } else {
            if (cell != this.cell) {
                GraphCellData bary = (GraphCellData) cell.getData();

                double dist = bary.distanceFrom(pos);
                double size = cell.getSpace().getSize();

                if ((!cell.isLeaf()) && ((size / dist) > box.getBarnesHutTheta())) {
                    int div = cell.getSpace().getDivisions();

                    for (int i = 0; i < div; i++)
                        recurseRepulsion(cell.getSub(i), delta);
                } else {
                    if (bary.weight != 0) {
                        delta.set(bary.center.x - pos.x, 0.0, 0.0);
//                        delta.set(bary.center.x - pos.x, bary.center.y - pos.y, is3D ? bary.center.z - pos.z : 0);

                        double len = delta.normalize();

                        if (len > 0) {
                            if (len < box.k)
                                len = box.k; // XXX NEW To prevent infinite
                            // repulsion.
                            double factor = ((K2 / (len * len)) * (bary.weight));
                            energies.accumulateEnergy(factor);
                            delta.scalarMult(-factor);
                            repE += factor;

                            disp.add(delta);
                        }
                    }
                }
            }
        }
    }

    protected void edgeAttraction(Vector3 delta, EdgeSpring edge, Energies energies) {
        int neighbourCount = neighbours.size();

        NodeParticle other = edge.getOpposite(this);
        Point3 opos = other.getPosition();

        delta.set(opos.x - pos.x, opos.y - pos.y, 0);

        double len = delta.normalize();
        double factor = K1Attr;// * len;

        delta.scalarMult(factor * (1f / (neighbourCount * 0.1f)));

        disp.add(delta);
        attE += factor;
        energies.accumulateEnergy(factor);
    }
}
