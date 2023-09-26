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

import network.aika.debugger.EventListener;
import network.aika.debugger.EventType;
import network.aika.debugger.*;
import network.aika.debugger.activations.layout.ParticleLink;
import network.aika.debugger.activations.particles.ActivationParticle;
import network.aika.debugger.stepmanager.StepManager;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.queue.Step;
import network.aika.text.Document;
import network.aika.text.GroundRef;
import network.aika.text.Range;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_X;
import static network.aika.debugger.AbstractGraphManager.STANDARD_DISTANCE_Y;
import static network.aika.debugger.stepmanager.StepManager.When.*;

/**
 * @author Lukas Molzberger
 */
public class ActivationViewManager extends AbstractViewManager<Activation, ActivationGraphManager> implements EventListener {

    private static final Logger log = LoggerFactory.getLogger(ActivationViewManager.class);

    private Document doc;

    private Range tokenRange;

    private ActivationConsoleManager consoleManager;

    private AIKADebugManager debugger;

    protected StepManager stepManager;

    protected LayoutState layoutState = new LayoutState();

    public ActivationViewManager(Document doc, ActivationConsoleManager consoleManager, AIKADebugManager debugger) {
        super(doc.getModel());

        if(consoleManager != null) {
            this.consoleManager = consoleManager;
            consoleManager.setActivationViewManager(this);
        }
        this.debugger = debugger;

        double width = 5 * STANDARD_DISTANCE_X;
        double height = 3 * STANDARD_DISTANCE_Y;

        getCamera().setGraphViewport(-(width / 2), -(height / 2), (width / 2), (height / 2));
        getCamera().setViewCenter(0.20, 0.20, 0.0);

        graphManager = new ActivationGraphManager(graph, doc);

        this.doc = doc;
        register();

        view = initView();
    }

    public void register() {
        doc.addEventListener(this);
    }

    public void unregister() {
        doc.removeEventListener(this);
    }

    public Range getTokenRange() {
        return tokenRange;
    }

    public void setTokenRange(Range tokenRange) {
        this.tokenRange = tokenRange;
    }

    public StepManager getStepManager() {
        return stepManager;
    }

    public void setStepManager(StepManager stepManager) {
        this.stepManager = stepManager;
    }

    public void pumpAndWaitForUserAction() {
        pump();

        stepManager.waitForClick();
    }

    @Override
    protected AbstractGraphMouseManager initMouseManager() {
        return new ActivationGraphMouseManager(this);
    }

    @Override
    public ActivationConsoleManager getConsoleManager() {
        return consoleManager;
    }

    public void showElementContext(GraphicElement ge) {
        consoleManager.setMode(ActivationPanelMode.SELECTED);

        if(ge instanceof Node) {
            Node n = (Node) ge;

            Activation act = graphManager.getAikaNode(n);
            if(act == null)
                return;

            consoleManager.showSelectedElementContext(act);
        } else if(ge instanceof Edge) {
            Edge e = (Edge) ge;

            Link l = graphManager.getLink(e);
            if(l == null)
                return;

            consoleManager.showSelectedElementContext(l);
        }
    }

    @Override
    public void reactToAltSelection(GraphicElement ge) {
        if (ge instanceof Node) {
            Node n = (Node) ge;

            Activation act = graphManager.getAikaNode(n);
            if (act == null)
                return;

            debugger.getNeuronViewManager()
                    .showSelectedNeuron(act.getNeuron());

            debugger.showNeuronView();
        }
    }

    @Override
    public void onElementEvent(EventType et, network.aika.elements.Element e) {
        if(e instanceof Activation<?>)
            onActivationEvent(et, (Activation) e);
        else if(e instanceof Link<?,?,?>)
            onLinkEvent(et, (Link) e);
    }

    public void onActivationEvent(EventType et, Activation act) {
        if(!within(tokenRange, act))
            return;

        ActivationParticle p = graphManager.lookupParticle(act);
        p.processLayout(layoutState);
        p.onEvent(et);

        if(!stepManager.stopHere(NEW))
            return;

        pumpAndWaitForUserAction();
    }

    public void onLinkEvent(EventType et, Link l) {
        if(!within(tokenRange, l))
            return;

        Edge e = onLinkEvent(l);
        if(e == null)
            return;

        if (!stepManager.stopHere(NEW))
            return;

        pumpAndWaitForUserAction();
    }

    @Override
    public void onQueueEvent(EventType et, Step s) {
        if(!within(tokenRange, s.getElement()))
            return;

        switch (et) {
            case ADDED:
                queueEntryAddedEvent(s);
                break;
            case BEFORE:
                beforeProcessedEvent(s);
                break;
            case AFTER:
                afterProcessedEvent(s);
                break;
        }
    }

    public static boolean within(Range tokenRange, network.aika.elements.Element e) {
        if(e instanceof Activation<?>)
            return within(tokenRange, (Activation) e);

        if(e instanceof Link)
            return within(tokenRange, (Link) e);

        return true;
    }

    public static boolean within(Range tokenRange, Activation act) {
        if(tokenRange == null)
            return true;

        GroundRef gr = act.getGroundRef();
        if(gr == null || gr.getTokenPosRange() == null)
            return false;

        return tokenRange.contains(gr.getTokenPosRange());
    }

    public static boolean within(Range tokenRange, Link l) {
        return (l.getInput() == null || within(tokenRange, l.getInput())) &&
                within(tokenRange, l.getOutput());
    }

    public void queueEntryAddedEvent(Step s) {

    }

    public void beforeProcessedEvent(Step s) {
        if(s.getElement() instanceof Activation) {
            beforeActivationProcessedEvent((Activation) s.getElement());
        } else if(s.getElement() instanceof Link) {
            beforeLinkProcessedEvent((Link) s.getElement());
        }

        if (!stepManager.stopHere(BEFORE))
            return;

        pumpAndWaitForUserAction();
    }

    public void afterProcessedEvent(Step s) {
        if(s.getElement() instanceof Activation) {
            afterActivationProcessedEvent((Activation) s.getElement());
        }

        if (!stepManager.stopHere(AFTER))
            return;

        pumpAndWaitForUserAction();
    }

    private void beforeActivationProcessedEvent(Activation act) {
        AbstractParticle p = graphManager.getParticle(act);
        if(p != null)
            p.onEvent(null);
    }

    private void afterActivationProcessedEvent(Activation act) {
        AbstractParticle p = graphManager.getParticle(act);
        if(p != null)
            p.onEvent(null);
    }

    private void highlightCurrentOnly(Element e) {
        if(lastHighlighted != e && lastHighlighted != null) {
            unhighlightElement(lastHighlighted);
        }
    }

    private void beforeLinkProcessedEvent(Link l) {
        onLinkEvent(l);
    }

    private Edge onLinkEvent(Link l) {
        if(l.getInput() == null || l.getOutput() == null)
            return null;

        if(!within(tokenRange, l))
            return null;

        ParticleLink pl = graphManager.lookupParticleLink(l);
        pl.processLayout();

        highlightCurrentOnly(pl.getEdge());

        pl.onEvent();

        return pl.getEdge();
    }

    public void viewClosed(String id) {
    }

    public Document getDocument() {
        return doc;
    }

    public void importNetworkLayout(DataInput in) throws IOException {
        getCamera().setViewPercent(in.readDouble());
        getCamera().setViewCenter(
                in.readDouble(),
                in.readDouble(),
                0.0
        );

        HashMap<String, Activation> actMap = new HashMap<>();
        doc.getActivations().stream().forEach(act ->
                actMap.put(act.getClass().getSimpleName() + ":" + act.getLabel(), act)
        );
        while(in.readBoolean()) {
            String label = in.readUTF();
            String type = in.readUTF();
            double x = in.readDouble();
            double y = in.readDouble();

            Activation act = actMap.get(type + ":" + label);
            if(act != null) {
                ActivationParticle p = graphManager.getParticle(act);
                p.x = x;
                p.y = y;
            }
        }
    }

    public void exportNetworkLayout(DataOutput out) throws IOException {
        out.writeDouble(getCamera().getViewPercent());
        out.writeDouble(getCamera().getViewCenter().x);
        out.writeDouble(getCamera().getViewCenter().y);

        List<Activation> acts = doc.getActivations().stream().toList();
        for(Activation act: acts) {
            ActivationParticle p = graphManager.getParticle(act);
            if(p != null && p.getPosition() != null) {
                out.writeBoolean(true);
                out.writeUTF(act.getLabel());
                out.writeUTF(act.getClass().getSimpleName());
                out.writeDouble(p.getPosition().x);
                out.writeDouble(p.getPosition().y);
             }
        }
        out.writeBoolean(false);
    }

    @Override
    public void moveNodeGroup(Node n, int x, int y) {
        Activation<?> act = getGraphManager().getAikaNode(n);
        if(!(act instanceof PatternActivation))
            return;

        DefaultCamera2D camera = (DefaultCamera2D) getGraphView().getCamera();

        GraphicGraph gg = viewer.getGraphicGraph();
        GraphicElement gn = (GraphicElement) n;

        act.getInputLinks()
                .map(l -> l.getInput())
                .map(iAct -> getGraphManager().getNode(iAct))
                .map(in -> (GraphicElement) gg.getNode(in.getId()))
                .forEach(in -> {
                    Point3 p = camera.transformPxToGuSwing(x, y);
                    in.move((in.getX() - gn.getX()) + p.x, (in.getY() - gn.getY()) + p.y, in.getZ());
                });
    }
}
