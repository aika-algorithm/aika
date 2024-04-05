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
package network.aika.fields.link;

import network.aika.fields.FieldOutput;
import network.aika.fields.UpdateListener;

/**
 * @author Lukas Molzberger
 */
public abstract class AbstractFieldLink {

    private Integer port;

    protected FieldOutput input;
    private int arg;

    protected boolean connected;
    protected boolean withinConnectionChange;

    protected boolean propagateUpdates = true;


    public AbstractFieldLink(FieldOutput input, int arg) {
        this.input = input;
        this.arg = arg;
    }

    public AbstractFieldLink(FieldOutput input, Integer port, int arg) {
        this(input, arg);
        this.port = port;
    }

    protected abstract void propagateUpdate(double u);

    public void setPropagateUpdates(boolean propagateUpdates) {
        this.propagateUpdates = propagateUpdates;
    }

    public boolean isPropagateUpdates() {
        return propagateUpdates;
    }

    public boolean isConnected() {
        return connected;
    }

    public void receiveUpdate(double u) {
        if(connected && propagateUpdates)
            propagateUpdate(u);
    }

    public static void updateConnected(FieldLink fl, boolean newConnected, boolean initialize) {
        if(fl != null)
            fl.updateConnected(newConnected, initialize);
    }

    public void updateConnected(boolean newConnected, boolean initialize) {
        if(!connected && newConnected)
            connect(initialize);
        else if(connected && !newConnected)
            disconnect(initialize);
    }

    public void connect(boolean initialize) {
        if(connected)
            return;

        withinConnectionChange = true;
        if(initialize) {
            double cv = input.getValue();
            propagateUpdate(cv);
        }
        withinConnectionChange = false;

        connected = true;
    }

    public void disconnect(boolean deinitialize) {
        if(!connected)
            return;

        withinConnectionChange = true;
        if(deinitialize) {
            double cv = input.getValue();
            propagateUpdate(-cv);
        }
        withinConnectionChange = false;

        connected = false;
    }

    public void unlinkInput() {
        input.removeOutput(this);
    }

    public abstract void unlinkOutput();

    public double getInputValue() {
        return connected ?
                input.getValue() :
                0.0;
    }

    public double getUpdatedInputValue() {
        return connected != withinConnectionChange ?
                input.getUpdatedValue() :
                0.0;
    }

    public int getArgument() {
        return arg;
    }

    public FieldOutput getInput() {
        return input;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;

        AbstractFieldLink fLink = (AbstractFieldLink) o;
        if(arg != fLink.arg)
            return false;

        if(input == fLink.input)
            return true;

        return input != null && input.equals(fLink.input);
    }

    @Override
    public String toString() {
        return input + " --" + arg;
    }
}
