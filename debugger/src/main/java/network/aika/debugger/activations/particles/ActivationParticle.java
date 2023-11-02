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
package network.aika.debugger.activations.particles;

import network.aika.debugger.EventType;
import network.aika.debugger.AbstractParticle;
import network.aika.debugger.activations.ActivationGraphManager;
import network.aika.debugger.activations.LayoutState;
import network.aika.debugger.activations.layout.ParticleLink;
import network.aika.enums.direction.Direction;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.Link;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.layout.springbox.EdgeSpring;
import org.graphstream.ui.layout.springbox.Energies;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

import java.util.function.Consumer;

import static network.aika.debugger.TypeMapper.neuronTypeModifiers;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 * @author Lukas Molzberger
 */
public class ActivationParticle<E extends Activation> extends AbstractParticle<ActivationGraphManager> {

    E act;

    public static double K1 = 0.12f;
    public static double K2 = 0.03f;

    private Double targetX;
    private Double targetY;

    public ActivationParticle(E act, Node n, ActivationGraphManager gm) {
        super(gm, n.getId(), n, 0.0, 0.0, 0.0);

        this.act = act;

        initPos(
                getAttributeValue(n, "x"),
                getAttributeValue(n, "y"),
                0.0
        );

        gm.setParticle(act, this);
    }

    @Override
    public void onEvent(EventType et) {
        node.setAttribute("ui.label", act.getLabel());
        node.setAttribute("ui.style", getActivationStrokeColor(act));

//        highlightCurrentOnly(node);

        Consumer<Node> neuronTypeModifier = neuronTypeModifiers.get(act.getNeuron().getClass());
        if (neuronTypeModifier != null)
            neuronTypeModifier.accept(node);
    }

    @Override
    public void processLayout(LayoutState ls) {
    }

    private String getActivationStrokeColor(Activation act) {
        if(act.isFired())
            return "stroke-color: black;";

        return "stroke-color: rgb(200, 200, 200);";
    }


    private static Double getAttributeValue(Node n, String x) {
        Double v = (Double) n.getAttribute(x);
        return v != null ? v : 0.0;
    }

    public Double getTargetX() {
        return targetX;
    }

    public void setTargetX(Double targetX) {
        this.targetX = targetX;
    }

    public Double getTargetY() {
        return targetY;
    }

    public void setTargetY(Double targetY) {
        this.targetY = targetY;
    }

    @Override
    protected void attraction(Vector3 delta) {
        if(graphManager.isDisabledForces())
            return;

        SpringBox box = (SpringBox) this.box;
        Energies energies = box.getEnergies();

        computeTargetAttraction(delta, energies);
        computeEdgeAttraction(delta, energies);
    }

    private void computeTargetAttraction(Vector3 delta, Energies energies) {
        if(targetX != null || targetY != null) {
            delta.set(
                    targetX != null ? targetX - pos.x : 0.0,
                    targetY != null ? targetY - pos.y : 0.0,
                    0.0
            );

            disp.add(delta);
            attE += K1;
            energies.accumulateEnergy(K1);
        }
    }

    private void computeEdgeAttraction(Vector3 delta, Energies energies) {
        for (EdgeSpring edge : neighbours) {
            if (!edge.ignored) {
                ActivationParticle other = (ActivationParticle) edge.getOpposite(this);

                Link link = getLink(other.act, act);
                if(link == null)
                    continue;

                Direction dir = getDirection(link);

                if(dir == OUTPUT) // Apply forces only in one direction
                    continue;

                ParticleLink pl = getParticleLink(link, dir);
                if(pl == null)
                    continue;

                pl.calculateForce(delta, pos, dir, other);

                delta.mult(new Vector2(0.0, K1));

                disp.add(delta);
                attE += K1;
                energies.accumulateEnergy(K1);
            }
        }
    }

    private Direction getDirection(Link link) {
        return act == link.getOutput() ? INPUT : OUTPUT;
    }

    private ParticleLink getParticleLink(Link link, Direction dir) {
        Long id = Long.valueOf(dir.getActivation(link).getId());

        return (ParticleLink) (dir == OUTPUT ?
                        getOutputParticleLink(id) :
                        getInputParticleLink(id));
    }

    private Link getLink(Activation actA, Activation actB) {
        Link l = getDirectedLink(actA, actB);
        if(l != null)
            return l;
        return getDirectedLink(actB, actA);
    }

    private Link getDirectedLink(Activation iAct, Activation<?> oAct) {
        return oAct.getInputLinks()
                .filter(l -> l.getInput() == iAct)
                .findFirst()
                .orElse(null);
    }

    public static ActivationParticle create(Activation act, Node n, ActivationGraphManager gm) {
        if(act instanceof PatternActivation) {
            return PatternActivationParticle.create((PatternActivation) act, n, gm);
        } else if(act instanceof BindingActivation) {
            return BindingActivationParticle.create((BindingActivation) act, n, gm);
        }

        return new ActivationParticle(act, n, gm);
    }
}
