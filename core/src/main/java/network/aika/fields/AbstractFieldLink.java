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
package network.aika.fields;

/**
 * @author Lukas Molzberger
 */
public abstract class AbstractFieldLink<O extends UpdateListener> {

    protected FieldOutput input;
    private int arg;
    protected O output;

    protected boolean connected;
    protected boolean within;

    protected boolean propagateUpdates = true;

    public AbstractFieldLink(FieldOutput input, int arg, O output) {
        this.input = input;
        this.arg = arg;
        this.output = output;
    }

    public void setPropagateUpdates(boolean propagateUpdates) {
        this.propagateUpdates = propagateUpdates;
    }

    public boolean isPropagateUpdates() {
        return propagateUpdates;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setInput(FieldOutput input) {
        this.input = input;
    }

    public void relinkInput(FieldOutput inputValue) {
        input.removeOutput(this);
        input = inputValue;
        input.addOutput(this);
    }

    public void receiveUpdate(boolean nextRound, double u) {
        if(connected && propagateUpdates)
            output.receiveUpdate(this, nextRound, u);
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

        within = true;
        if(initialize) {
            double cv = input.getValue();
            output.receiveUpdate(this, false, cv);
        }
        within = false;

        connected = true;
    }

    public void disconnect(boolean deinitialize) {
        if(!connected)
            return;

        within = true;
        if(deinitialize) {
            double cv = input.getValue();
            output.receiveUpdate(this, false, -cv);
        }
        within = false;

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
        return connected != within ?
                input.getUpdatedValue() :
                0.0;
    }

    public int getArgument() {
        return arg;
    }

    public FieldOutput getInput() {
        return input;
    }

    public O getOutput() {
        return output;
    }

    @Override
    public boolean equals(Object o) {
        AbstractFieldLink fLink = (AbstractFieldLink) o;
        if(arg != fLink.arg)
            return false;

        if(!output.equals(fLink.output))
            return false;

        if(input == fLink.input)
            return true;

        return input != null && input.equals(fLink.input);
    }

    @Override
    public String toString() {
        return input + " --" + arg + "--> " + output;
    }
}
