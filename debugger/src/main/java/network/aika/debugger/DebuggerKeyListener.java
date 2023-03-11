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


/**
 * @author Lukas Molzberger
 */
public class DebuggerKeyListener implements KeyListener {

    private AIKADebugger debugger;

    public DebuggerKeyListener(AIKADebugger debugger) {
        this.debugger = debugger;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        ActivationViewManager avm = debugger.getActivationViewManager();
        if(avm == null)
            return;

        StepManager sm = avm.getStepManager();

        char c = e.getKeyChar();

        switch(c) {
            case 'm':
                return;
            case 'e':
                sm.setStopAfterProcessed(true);
                sm.click();
                break;
            case 'r':
                sm.setStepMode(false);
                sm.setBreakpoint(null);
                sm.resetTimestamp();
                sm.click();
                break;
            case 'b':
                sm.setStepMode(false);
                sm.resetTimestamp();
                sm.setBreakpoint(debugger.getNextBreakpoint());
                sm.click();
                break;
            case 'l':
                sm.setStepMode(true);
                sm.click();
                break;
            default:
                if(e.isControlDown() && Character.isDigit(c)) {
                    int testCaseId = Integer.parseInt("" + c);
                    Runnable testCase = debugger.getTestCaseListeners().get(testCaseId);

                    if(testCase != null && testCase != debugger.getCurrentTestCase()) {
                        sm.setRestartTestcaseSignal(true);
                        debugger.setCurrentTestCase(testCase);
                        sm.click();
                    }
                }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        char c = e.getKeyChar();
        if(c == 'o') {
    //        debugger.getActivationViewManager().dumpNetworkCoordinates();
            debugger.getNeuronViewManager().dumpNetworkCoordinates();
        }
    }
}
