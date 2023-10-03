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

import network.aika.debugger.graphics.CubicCurveShape;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.view.util.InteractiveElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.EnumSet;

/**
 * @author Lukas Molzberger
 */
public abstract class AbstractGraphMouseManager implements MouseInputListener, org.graphstream.ui.view.util.MouseManager, MouseWheelListener {

    private static final Logger log = LoggerFactory.getLogger(AbstractGraphMouseManager.class);

    protected View view;
    protected GraphicGraph graph;
    private final EnumSet<InteractiveElement> types;
    protected GraphicElement curElement;
    protected GraphicElement lastClickedElement;
    private AbstractViewManager viewManager;
    private MouseEvent lastMouseDragEvent;

    public AbstractGraphMouseManager(AbstractViewManager viewManager) {
        this(EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE));
        this.viewManager = viewManager;
    }

    private AbstractGraphMouseManager(EnumSet<InteractiveElement> types) {
        this.types = types;
    }

    public void init(GraphicGraph graph, View view) {
        this.view = view;
        this.graph = graph;
        view.addListener("Mouse", this);
        view.addListener("MouseMotion", this);
    }

    public EnumSet<InteractiveElement> getManagedTypes() {
        return this.types;
    }

    public void release() {
        this.view.removeListener("Mouse", this);
        this.view.removeListener("MouseMotion", this);
    }

    protected void mouseButtonPressOnElement(GraphicElement element, MouseEvent event) {
        lastClickedElement = element;

        this.view.freezeElement(element, true);
        if(event.isAltDown()) {
            viewManager.reactToAltSelection(element);
        } else if (event.getButton() == 3) {
            element.setAttribute("ui.selected", new Object[0]);
        } else {
            element.setAttribute("ui.clicked", new Object[0]);

            viewManager.showElementContext(element);
        }
    }

    protected void elementMoving(GraphicElement element, MouseEvent event) {
        if(element instanceof Node) {
            this.viewManager.graphManager
                    .getParticle((Node) element)
                    .setManuallyMoved(true);

            if (event.isControlDown()) {
                Node n = (Node) element;
                viewManager.moveNodeGroup(n, event.getX(), event.getY());
            }
        }

        this.view.moveElementAtPx(element, event.getX(), event.getY());
    }

    protected void mouseButtonReleaseOffElement(GraphicElement element, MouseEvent event) {
        this.view.freezeElement(element, false);
        if (event.getButton() != 3) {
            element.removeAttribute("ui.clicked");
        }
    }

    protected abstract void doContextMenuPop(MouseEvent e);

    public void mouseClicked(MouseEvent event) {
        viewManager.getGraphView().requestFocusInWindow();
    }

    public void mousePressed(MouseEvent event) {
       //this.curElement = view.findGraphicElementAt(this.types, event.getX(), event.getY());

        if (event.isPopupTrigger())
            doContextMenuPop(event);

        double x = event.getX();
        double y = event.getY();

        DefaultCamera2D camera = (DefaultCamera2D) view.getCamera();
        Point3 pointGU = camera.transformPxToGuSwing(x, y);

        double lengthInGu = camera.getMetrics().lengthToGu(20, StyleConstants.Units.PX);
        curElement = getSelectedNode(pointGU, lengthInGu, lastClickedElement);
        if(curElement == null)
            curElement = getSelectedNode(pointGU, lengthInGu, null);

        if(curElement == null)
            curElement = getSelectedEdge(pointGU, lastClickedElement);
        if(curElement == null)
            curElement = getSelectedEdge(pointGU, null);

        if (this.curElement != null) {
            this.mouseButtonPressOnElement(this.curElement, event);
        }
    }

    private GraphicNode getSelectedNode(Point3 pointGU, double sizePx, Element exclude) {
        return (GraphicNode) graph.nodes()
                .filter(n -> nodeSelected(n, pointGU, sizePx))
                .filter(n -> n != exclude)
                .findAny()
                .orElse(null);
    }

    private boolean nodeSelected(Node n, Point3 dst, double sizePx) {
        double[] p = getCoords(n);
        double x = p[0];
        double y = p[1];

        double rad = sizePx / 2.0;

        double x1 = (dst.x) - rad;
        double x2 = (dst.x) + rad;
        double y1 = (dst.y) - rad;
        double y2 = (dst.y) + rad;

        if (x < x1)
            return false;
        else if (y < y1)
            return false;
        else if (x > x2)
            return false;
        else if (y > y2)
            return false;
        else
            return true;
    }


    private GraphicEdge getSelectedEdge(Point3 pointGU, Element exclude) {
        return (GraphicEdge) graph.edges()
                .filter(e -> edgeSelected(e, pointGU))
                .filter(e -> e != exclude)
                .findAny()
                .orElse(null);
    }

    private boolean edgeSelected(Edge e, Point3 p) {
        double[] ps = getCoords(e.getSourceNode());
        double[] pt = getCoords(e.getTargetNode());

        CubicCurveShape edgeShape = new CubicCurveShape();
        edgeShape.makeSingle(ps[0], ps[1], pt[0], pt[1], 0.0, 0.0);

        return edgeShape.contains(p.x, p.y, 0.005);
    }

    private double[] getCoords(Node n) {
        AbstractParticle ap = viewManager.graphManager.getParticle(n);
        return new double[] { ap.x, ap.y };
    }

    public void mouseDragged(MouseEvent event) {
        if (this.curElement != null) {
            this.elementMoving(this.curElement, event);
        } else {
            if (lastMouseDragEvent != null) {
                dragGraphMouseMoved(event, lastMouseDragEvent, (DefaultCamera2D) view.getCamera());
            }
            lastMouseDragEvent = event;
        }
    }

    public void mouseReleased(MouseEvent event) {
        lastMouseDragEvent = null;

        if (event.isPopupTrigger())
            doContextMenuPop(event);

        if (this.curElement != null) {
            this.mouseButtonReleaseOffElement(this.curElement, event);
            this.curElement = null;
        }
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseMoved(MouseEvent event) {

    }

    public void dragGraphMouseMoved(MouseEvent me, MouseEvent lastMe, DefaultCamera2D camera) {
        Point3 centerGU = camera.getViewCenter();
        Point3 centerPX = camera.transformGuToPxSwing(centerGU.x, centerGU.y, 0);

        Point3 newCenterGU = camera.transformPxToGuSwing(
                centerPX.x - (me.getX() - lastMe.getX()),
                centerPX.y - (me.getY() - lastMe.getY())
        );

        camera.setViewCenter(newCenterGU.x, newCenterGU.y, newCenterGU.z);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        zoomGraphMouseWheelMoved(mwe, view.getCamera());
    }

    public static void zoomGraphMouseWheelMoved(MouseWheelEvent mwe, Camera camera) {
        // https://github.com/graphstream/gs-core/issues/301

        if (mwe.getWheelRotation() > 0) {
            double newViewPercent = camera.getViewPercent() + 0.05;
            camera.setViewPercent(newViewPercent);
        } else if (mwe.getWheelRotation() < 0) {
            double currentViewPercent = camera.getViewPercent();
            if (currentViewPercent > 0.05) {
                camera.setViewPercent(currentViewPercent - 0.05);
            }
        }
    }
}
