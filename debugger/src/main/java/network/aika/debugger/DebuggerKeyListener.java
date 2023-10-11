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


import network.aika.debugger.activations.ActivationViewManager;
import network.aika.debugger.stepmanager.StepManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Lukas Molzberger
 */
public class DebuggerKeyListener implements KeyListener {

    private AIKADebugger debugger;
    private Map<Character, KeyEventAction> actions;

    public DebuggerKeyListener(AIKADebugger debugger) {
        this.debugger = debugger;
        actions = new HashMap<>();
        actions.put('m', new MAction());
        actions.put('e', new EAction());
        actions.put('r', new RAction());
        actions.put('l', new LAction());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        ActivationViewManager avm = debugger.getActivationViewManager();
        if(avm == null)
            return;

        char c = e.getKeyChar();
        KeyEventAction action = actions.get(c);
        if (action != null) {
            action.execute(avm);
        } else if(e.isAltDown() && Character.isDigit(c)) {
            int testCaseId = Integer.parseInt("" + c);
            Runnable testCase = debugger.getTestCaseListeners().get(testCaseId);
            StepManager sm = avm.getStepManager();

            if(testCase != null && testCase != debugger.getCurrentTestCase()) {
                sm.setRestartTestcaseSignal(true);
                debugger.setCurrentTestCase(testCase);
                sm.click();
            }
        }

        updateDisabledForces(e);
    }

    private void updateDisabledForces(KeyEvent e) {
        debugger.getActivationViewManager().graphManager.setDisabledForces(e.isControlDown());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        updateDisabledForces(e);
    }

    private interface KeyEventAction {
        void execute(ActivationViewManager avm);
    }

    private class MAction implements KeyEventAction {
        @Override
        public void execute(ActivationViewManager avm) {
            System.out.println("Metric: " + debugger.getActivationViewManager().getCamera().getMetrics());
        }
    }

    private class EAction implements KeyEventAction {
        @Override
        public void execute(ActivationViewManager avm) {
            StepManager sm = avm.getStepManager();
            sm.setStopAfterProcessed(true);
            sm.click();
        }
    }

    private class RAction implements KeyEventAction {
        @Override
        public void execute(ActivationViewManager avm) {
            StepManager sm = avm.getStepManager();
            sm.setStepMode(false);
//            debugger.getActivationViewManager().getConsoleManager().unregister();
            sm.setBreakpoint(null);
            sm.resetTimestamp();
            sm.click();
        }
    }

    private class LAction implements KeyEventAction {
        @Override
        public void execute(ActivationViewManager avm) {
            StepManager sm = avm.getStepManager();
            sm.setStepMode(true);
//            debugger.getActivationViewManager().getConsoleManager().register();
            sm.click();
        }
    }
}
