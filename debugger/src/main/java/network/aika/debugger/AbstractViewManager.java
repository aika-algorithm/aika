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

import network.aika.Model;
import network.aika.debugger.math.Matrix;
import network.aika.debugger.math.Vector;
import network.aika.elements.Element;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

import static network.aika.debugger.math.Matrix.getWorldMatrix;


/**
 * @author Lukas Molzberger
 */
public abstract class AbstractViewManager<N, G extends AbstractGraphManager>extends JPanel implements KeyListener, MouseInputListener, MouseWheelListener {

    Character pressedKey;

    private Matrix worldMatrix;

    private double lastX, lastY;


    protected boolean highlightEnabled = true;


    protected G graphManager;


    protected JPanel graphView;

    protected JComponent view;

    protected Element lastHighlighted;

    private Model model;

    private Set<String> movedManually = new HashSet<>();


    public AbstractViewManager(Model model) {
        super(new GridLayout(1, 1));

        this.model = model;
        initModifiers();


        MouseManager mouseManager = new MouseManager(this);

        setFocusable(true);
        setRequestFocusEnabled(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
    }

    public void update() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(worldMatrix == null)
            return;

        Canvas c = new CanvasImpl(worldMatrix);
        //sim.getRoot().draw(g, c);
    }


    private void initWorldMatrix(JFrame f) {
        worldMatrix = getWorldMatrix(
                new Vector(new double[]{
                        f.getWidth() / 2.0,
                        f.getHeight() / 2.0,
                        0.0,
                        1.0
                }),
                400.0);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKey = e.getKeyChar();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKey = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        double deltaX = (e.getX() - lastX);
        lastX = e.getX();

        double deltaY = (e.getY() - lastY);
        lastY = e.getY();

        if (!e.isShiftDown()) {
            mouseTranslationUpdate(e.isControlDown(), deltaX, deltaY);
        } else {
            mouseRotationUpdate(e.isControlDown(), deltaX / 3.0, deltaY / 3.0);
        }

        update();
    }

    private void mouseRotationUpdate(boolean ctrl, double xDelta, double yDelta) {
        if(!ctrl) {
            worldMatrix = rotate(worldMatrix, xDelta, yDelta);
        } else {

        }
    }

    private void mouseTranslationUpdate(boolean ctrl, double xDelta, double yDelta) {
        if(!ctrl) {
            worldMatrix = translation(worldMatrix, xDelta, yDelta);
        } else {

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double s = e.getPreciseWheelRotation();

        if(!e.isControlDown()) {
            worldMatrix = scale(worldMatrix, s);
        } else {

        }
        update();
    }

    private Matrix translation(Matrix m, double xDelta, double yDelta) {
        double[] offset = m.getColumn(3, 3);
        offset[0] += xDelta;
        offset[1] += yDelta;

        double scale = m.getRadius();

        Vector rotation = m.getRotation();

        return getWorldMatrix(new Vector(offset), rotation, scale);
    }

    private Matrix rotate(Matrix m, double xDelta, double yDelta) {
        double[] offset = m.getColumn(3, 3);
        double scale = m.getRadius();

        double[] rotation = m.getRotation().getValue();
        rotation[1] -= Math.toRadians(xDelta);
        rotation[0] += Math.toRadians(yDelta);

        return getWorldMatrix(new Vector(offset), new Vector(rotation), scale);
    }

    private Matrix scale(Matrix m, double s) {
        double[] offset = m.getColumn(3, 3);
        double scale = m.getRadius();
        scale += s * 10.0;

        Vector rotation = m.getRotation();

        return getWorldMatrix(new Vector(offset), rotation, scale);
    }

    public boolean hasBeenMovedManually(String key) {
        return movedManually.contains(key);
    }

    public void setMovedManually(String key, boolean v) {
        if(v)
            this.movedManually.add(key);
        else
            this.movedManually.remove(key);
    }

    public abstract Component getConsoleManager();

    public Model getModel() {
        return model;
    }

    public G getGraphManager() {
        return graphManager;
    }

    public abstract void reactToCtrlSelection(GraphicElement element);

    public abstract void showElementContext(GraphicElement ge);


    public JComponent getView() {
        return view;
    }

    protected JComponent initView() {
        Component consoleManager = getConsoleManager();
        if(consoleManager != null) {
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphView, consoleManager);
            splitPane.setOneTouchExpandable(true);
            splitPane.setResizeWeight(0.7);

            return splitPane;
        } else {
            return graphView;
        }
    }
/*
    private Graph initGraph() {
        //        System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");

        Graph graph = new SingleGraph("0");

        graph.setAttribute("ui.stylesheet",
                "node {" +
                        "size: 20px;" +
//                  "fill-color: #777;" +
//                  "text-mode: hidden;" +
                        "z-index: 1;" +
//                  "shadow-mode: gradient-radial; shadow-width: 2px; shadow-color: #999, white; shadow-offset: 3px, -3px;" +
                        "stroke-mode: plain; " +
                        "stroke-width: 2px;" +
                        "text-size: 20px;" +
                        "text-alignment: under;" +
                        "text-color: black;" +
                        "text-style: bold;" +
                        "text-background-mode: rounded-box;" +
                        "text-background-color: rgba(100, 100, 100, 100); " +
                        "text-padding: 2px;" +
                        "text-offset: 0px, 8px;" +
                        "} " +
                        "node:selected {" +
                        "stroke-color: red; " +
                        "stroke-width: 4px;" +
                        "} " +
                        "edge {" +
                        "size: 2px;" +
                        "shape: cubic-curve;" +
                        "z-index: 0;" +
                        "arrow-size: 9px, 6px;" +
                        "text-size: 20px;" +
                        "text-alignment: under;" +
                        "text-color: black;" +
                        "text-style: bold;" +
                        "text-background-mode: rounded-box;" +
                        "text-background-color: rgba(100, 100, 100, 100); " +
                        "text-padding: 2px;" +
                        "text-offset: 0px, 2px;" +
                        "} " +
                        "edge:selected {" +
                        "stroke-mode: plain; " +
                        "fill-color: red;" +
                        "stroke-width: 3px;" +
                        "}"
        );

        graph.setAttribute("ui.antialias");
        graph.setAutoCreate(true);

        return graph;
    }
*/

    protected void initModifiers() {
    }

    public void pump() {
  //      fromViewer.pump();
        // fromViewer.blockingPump();
    }

    public void unhighlightElement(Element ge) {
        if(!highlightEnabled)
            return;

  //      ge.removeAttribute("ui.selected");
  //      fromViewer.pump();
    }

    public void highlightElement(Element ge) {
        if(!highlightEnabled)
            return;

 //       ge.setAttribute("ui.selected");
 //       fromViewer.pump();
    }

    public void viewClosed(String id) {
        //     loop = false;
    }
}
