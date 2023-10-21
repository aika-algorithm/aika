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
package network.aika.debugger.stepmanager;

import network.aika.Document;

/**
 * @author Lukas Molzberger
 */
public class DebugStepManager implements StepManager {

    long MAX_DELAY_TO_DEBUG_MODE = 4000;

    boolean stopAfterProcessed;

    boolean stepMode = true;

    Document doc;

    Long lastTimestamp = null;

    protected boolean clicked;


    public DebugStepManager(Document doc) {
        this.doc = doc;
    }

    public void setStopAfterProcessed(boolean b) {
        stopAfterProcessed = b;
    }

    public synchronized void click() {
        clicked = true;
        notifyAll();
    }

    public void setStepMode(boolean stepMode) {
        this.stepMode = stepMode;
    }

    public void resetTimestamp() {
        lastTimestamp = null;
    }

    public boolean stopHere(When w) {
        long diff = lastTimestamp != null ? System.currentTimeMillis() - lastTimestamp : 0;
        lastTimestamp = System.currentTimeMillis();
        if (diff > MAX_DELAY_TO_DEBUG_MODE)
            stepMode = true;

        if(w == When.AFTER && stopAfterProcessed) {
            stopAfterProcessed = false;
            stepMode = true;
        }

        return stepMode;
    }

    @Override
    public synchronized void waitForClick() {
        try {
            while(!clicked) {
                wait();
            }
            clicked = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
