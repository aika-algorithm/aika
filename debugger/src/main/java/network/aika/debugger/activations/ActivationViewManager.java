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

import network.aika.callbacks.EventListener;
import network.aika.callbacks.EventType;
import network.aika.debugger.*;
import network.aika.debugger.activations.layout.LinkEdge;
import network.aika.debugger.stepmanager.StepManager;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.steps.Step;
import network.aika.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static network.aika.debugger.stepmanager.StepManager.When.*;

/**
 * @author Lukas Molzberger
 */
public class ActivationViewManager extends AbstractViewManager<Activation, ActivationGraphManager> implements EventListener {

    private static final Logger log = LoggerFactory.getLogger(ActivationViewManager.class);

    private Document doc;

    private ActivationConsoleManager consoleManager;

    private AIKADebugManager debugger;

    protected StepManager stepManager;

    protected LayoutState layoutState = new LayoutState();

    public ActivationViewManager(Document doc, ActivationConsoleManager consoleManager, AIKADebugManager debugger) {
        super(doc.getModel());
        this.consoleManager = consoleManager;
        this.debugger = debugger;

//        double width = 5 * STANDARD_DISTANCE_X;
//        double height = 3 * STANDARD_DISTANCE_Y;
/*
        getCamera().setGraphViewport(-(width / 2), -(height / 2), (width / 2), (height / 2));
        getCamera().setViewCenter(0.20, 0.20, 0.0);
*/
        graphManager = new ActivationGraphManager(doc);

        this.doc = doc;
        doc.addEventListener(this);

        view = initView();
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
    public Component getConsoleManager() {
        return consoleManager;
    }

    @Override
    public void showElementContext(GraphicElement ge) {
        consoleManager.showSelectedElementContext(ge.getElement());
    }

    @Override
    public void reactToCtrlSelection(GraphicElement ge) {
        if (ge.getElement() instanceof Activation) {
            Activation act = (Activation) ge.getElement();
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
        Node p = graphManager.lookupNode(act);
//        p.update(layoutState, et);

        if(!stepManager.stopHere(NEW))
            return;

        pumpAndWaitForUserAction();
    }

    public void onLinkEvent(EventType et, Link l) {


        if (!stepManager.stopHere(NEW))
            return;

        pumpAndWaitForUserAction();
    }

    @Override
    public void onQueueEvent(EventType et, Step s) {
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
        Node p = graphManager.lookupNode(act);
//        p.onEvent(null);
    }

    private void afterActivationProcessedEvent(Activation act) {
        Node p = graphManager.lookupNode(act);
 //       if(p != null)
 //           p.onEvent(null);
    }

    private void highlightCurrentOnly(LinkEdge e) {
        if(lastHighlighted != e && lastHighlighted != null) {
            unhighlightElement(lastHighlighted);
        }
    }

    private void beforeLinkProcessedEvent(Link l) {
        if(l.getInput() == null || l.getOutput() == null)
            return;

        Edge pl = graphManager.lookupEdge(l);
//        pl.processLayout();

//        highlightCurrentOnly(pl);

//        pl.onEvent(null);

    }

    public void viewClosed(String id) {
    }

    public Document getDocument() {
        return doc;
    }
}
