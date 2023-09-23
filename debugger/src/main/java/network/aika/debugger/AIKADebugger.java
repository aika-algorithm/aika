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
import network.aika.debugger.activations.ActivationConsoleManager;
import network.aika.debugger.activations.ActivationViewManager;
import network.aika.debugger.neurons.NeuronConsoleManager;
import network.aika.debugger.neurons.NeuronViewManager;
import network.aika.debugger.stepmanager.DebugStepManager;
import network.aika.debugger.stepmanager.StepManager;
import network.aika.debugger.stepmanager.TestCaseRestartException;
import network.aika.text.Document;
import network.aika.text.Range;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.*;


/**
 * @author Lukas Molzberger
 */
public class AIKADebugger extends JPanel implements AIKADebugManager {

    private JTabbedPane tabbedPane;

    private ActivationViewManager actViewManager;
    private NeuronViewManager neuronViewManager;

    private DebuggerKeyListener keyListener;

    final public static Integer ACTIVATION_TAB_INDEX = 0;
    final public static Integer NEURON_TAB_INDEX = 1;

    private Runnable currentTestCase;
    private Map<Integer, Runnable> testCaseListeners = new TreeMap<>();

    private NavigableSet<Long> breakpoints = new TreeSet<>();
    private boolean templatesOnly;

    public AIKADebugger() {
        super(new GridLayout(1, 1));

        tabbedPane = new JTabbedPane();

        add(tabbedPane);

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFocusCycleRoot(true);

        keyListener = new DebuggerKeyListener(this);

        registerGlobalKeyListener();

        tabbedPane.addChangeListener(event-> {
            if(tabbedPane.getSelectedIndex() == NEURON_TAB_INDEX) {
                neuronViewManager.updateGraphNeurons(templatesOnly);
            }
        });
    }

    private void createMenu(JFrame frame) {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);

        menuItem = new JMenuItem("Open",
                KeyEvent.VK_O);

        menuItem.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(this);

            if(returnVal == JFileChooser.APPROVE_OPTION)
                System.out.println(fc.getSelectedFile());
        });

        menu.add(menuItem);

        menuItem = new JMenuItem("Save",
                KeyEvent.VK_S);
        menuItem.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(this);

            if(returnVal == JFileChooser.APPROVE_OPTION)
                System.out.println(fc.getSelectedFile());
        });

        menu.add(menuItem);

        frame.setJMenuBar(menuBar);
    }

    private void registerGlobalKeyListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    int id = e.getID();
                    switch (id) {
                        case KeyEvent.KEY_TYPED:
                            keyListener.keyTyped(e);
                            break;
                        case KeyEvent.KEY_PRESSED:
                            keyListener.keyPressed(e);
                            break;
                        case KeyEvent.KEY_RELEASED:
                            keyListener.keyReleased(e);
                            break;
                    }
                    return false;
                });
    }

    public void addBreakpoints(Long... bps) {
        breakpoints.addAll(Arrays.asList(bps));
    }

    public Long getNextBreakpoint() {
        return breakpoints.ceiling(
                actViewManager.getDocument().getCurrentTimestamp().getTimestamp()
        );
    }

    public void run() {
        boolean restart = false;
        do {
            try {
                currentTestCase.run();
            } catch (TestCaseRestartException e) {
                restart = true;
            }
        } while (restart);
    }

    public void setModel(Model model) {
        neuronViewManager = new NeuronViewManager(model, new NeuronConsoleManager());
        addTab(NEURON_TAB_INDEX, "Neurons", KeyEvent.VK_N, neuronViewManager.getView());
    }

    public AIKADebugger setDocument(Document doc) {
        actViewManager = new ActivationViewManager(doc, new ActivationConsoleManager(doc), this);
        actViewManager.setStepManager(new DebugStepManager(doc));
        addTab(ACTIVATION_TAB_INDEX, "Activations", KeyEvent.VK_A, actViewManager.getView());
        actViewManager.enableAutoLayout();
        actViewManager.getCamera().setViewCenter(0.678, 0.563, 0);

        setModel(doc.getModel());
        return this;
    }

    public Runnable getCurrentTestCase() {
        return currentTestCase;
    }

    public void setCurrentTestCase(Runnable currentTestCase) {
        this.currentTestCase = currentTestCase;
    }

    public Map<Integer, Runnable> getTestCaseListeners() {
        return testCaseListeners;
    }

    public ActivationViewManager getActivationViewManager() {
        return actViewManager;
    }

    public NeuronViewManager getNeuronViewManager() {
        return neuronViewManager;
    }

    public DebuggerKeyListener getKeyListener() {
        return keyListener;
    }

    public StepManager getStepManager() {
        return actViewManager.getStepManager();
    }

    public void addTab(int tabIndex, String label, int ke, JComponent panel) {
        tabbedPane.addTab(label, null, panel, "Does nothing");
        tabbedPane.setMnemonicAt(tabIndex, ke);
    }

    @Override
    public void showNeuronView() {
        selectTab(1);
    }

    public void removeTab(int tabIndex) {
        tabbedPane.removeTabAt(tabIndex);
    }

    public void selectTab(int tabIndex) {
        tabbedPane.setSelectedIndex(tabIndex);
    }

    public AIKADebugger setTemplatesOnly(boolean templatesOnly) {
        this.templatesOnly = templatesOnly;
        return this;
    }

    public AIKADebugger setTokenRange(Range r) {
        actViewManager.setTokenRange(r);
        return this;
    }

    public static AIKADebugger createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AIKADebugger d = new AIKADebugger();

        EventQueue.invokeLater(() -> {
            // Create and set up the window.
            d.createMainFrame();
        });

        return d;
    }

    private void createMainFrame() {
        JFrame f = new JFrame("AIKA Debugger");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        URL logoURL = AIKADebugger.class.getResource("logo.png");
        ImageIcon logo = new ImageIcon(logoURL);
        f.setIconImage(logo.getImage());

        // Add content to the window.
        f.add(this, BorderLayout.CENTER);
        f.pack();
        f.setLocationByPlatform(true);
        f.setVisible(true);
        f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        createMenu(f);
    }
}
