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

import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;
import network.aika.fields.UpdateListener;

import java.util.Objects;

/**
 * @author Lukas Molzberger
 */
public class FieldLink {

    private Integer port;

    protected FieldOutput input;

    protected UpdateListener output;

    private int arg;

    protected boolean connected;
    protected boolean withinConnectionChange;

    protected boolean propagateUpdates = true;

    public FieldLink(FieldOutput input, int arg, FieldInput output) {
        this.input = input;
        this.arg = arg;
        this.output = output;
    }

    public FieldLink(FieldOutput input, Integer port, int arg, FieldInput output) {
        this.input = input;
        this.port = port;
        this.arg = arg;
        this.output = output;
    }

    protected void propagateUpdate(double u) {
        output.receiveUpdate(this, u);
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


    public static FieldLink linkAndConnect(FieldOutput in, FieldInput out) {
        FieldLink fl = link(in, out.size(), out);

        fl.connect(true);
        return fl;
    }

    public static FieldLink link(FieldOutput in, FieldInput out) {
        return link(in, out.size(), out);
    }

    public static FieldLink linkAndConnect(FieldOutput in, int arg, FieldInput out) {
        FieldLink fl = link(in, arg, out);
        fl.connect(true);
        return fl;
    }

    public static FieldLink link(FieldOutput in, int arg, FieldInput out) {
        FieldLink fl = new FieldLink(in, arg, out);
        out.addInput(fl);
        in.addOutput(fl);
        return fl;
    }

    public static void linkAndConnectAll(FieldOutput in, FieldInput... out) {
        assert in != null;

        for(FieldInput o : out) {
            if(o != null) {
                link(in, 0, o)
                        .connect(true);
            }
        }
    }

    public static void linkAll(FieldOutput in, FieldInput... out) {
        assert in != null;

        for(FieldInput o : out) {
            if(o != null) {
                link(in, 0, o);
            }
        }
    }

    public void unlinkInput() {
        input.removeOutput(this);
    }

    public void unlinkOutput() {
        output.removeInput(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldLink fieldLink = (FieldLink) o;
        return arg == fieldLink.arg && Objects.equals(port, fieldLink.port) && Objects.equals(input, fieldLink.input) && Objects.equals(output, fieldLink.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, input, output, arg);
    }

    @Override
    public String toString() {
        return input + " --" + arg + "-->" + output;
    }
}
