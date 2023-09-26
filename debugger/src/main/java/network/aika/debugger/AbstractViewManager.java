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
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.camera.Camera;

import javax.swing.*;
import java.awt.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Lukas Molzberger
 */
public abstract class AbstractViewManager<N, G extends AbstractGraphManager> {

    protected boolean highlightEnabled = true;

    protected Graph graph;

    protected G graphManager;

    protected SwingViewer viewer;

    protected ViewerPipe fromViewer;

    protected ViewPanel graphView;

    protected JComponent view;

    protected Element lastHighlighted;

    private Model model;

    private Set<String> movedManually = new HashSet<>();


    public AbstractViewManager(Model model){
        this.model = model;
        initModifiers();

        graph = initGraph();
        viewer = new SwingViewer(new ThreadProxyPipe(graph));

        graphView = (DefaultView)viewer.addDefaultView(false, new SwingGraphRenderer());
        graphView.enableMouseOptions();

        AbstractGraphMouseManager mouseManager = initMouseManager();
        graphView.setMouseManager(mouseManager);
        graphView.addMouseWheelListener(mouseManager);

        Camera camera = graphView.getCamera();

        camera.setAutoFitView(false);


        // The default action when closing the view is to quit
        // the program.
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        fromViewer = viewer.newViewerPipe();
        fromViewer.addSink(graph);
    }

    protected abstract AbstractGraphMouseManager initMouseManager();

    public ViewPanel getGraphView() {
        return graphView;
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

    public void enableAutoLayout() {
        viewer.enableAutoLayout(graphManager);
    }

    public void disableAutoLayout() {
        viewer.disableAutoLayout();
    }

    public Model getModel() {
        return model;
    }

    public G getGraphManager() {
        return graphManager;
    }

    public abstract void reactToAltSelection(GraphicElement element);

    public abstract void showElementContext(GraphicElement ge);

    public Graph getGraph() {
        return graph;
    }

    public Camera getCamera() {
        return graphView.getCamera();
    }

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

    protected void initModifiers() {
    }

    public void pump() {
        fromViewer.pump();
        // fromViewer.blockingPump();
    }

    public void unhighlightElement(Element ge) {
        if(!highlightEnabled)
            return;

        ge.removeAttribute("ui.selected");
        fromViewer.pump();
    }

    public void highlightElement(Element ge) {
        if(!highlightEnabled)
            return;

        ge.setAttribute("ui.selected");
        fromViewer.pump();
    }

    public void viewClosed(String id) {
        //     loop = false;
    }

    public abstract void importNetworkLayout(DataInput in) throws IOException;

    public abstract void exportNetworkLayout(DataOutput out) throws IOException;

    public abstract void moveNodeGroup(Node n, int x, int y);
}
