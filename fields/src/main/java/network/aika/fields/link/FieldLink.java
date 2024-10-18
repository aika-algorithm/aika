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

import java.util.Objects;

import static network.aika.utils.StringUtils.depthToSpace;

/**
 * @author Lukas Molzberger
 */
public class FieldLink {


    protected FieldOutput input;
    protected FieldInput output;

    protected boolean connected;
    protected boolean withinConnectionChange;

    protected boolean propagateUpdates = true;

    public FieldLink(FieldOutput input, FieldInput output) {
        assert input != output;

        this.input = input;
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


    public FieldOutput getInput() {
        return input;
    }


    public void unlinkInput() {
        input.removeOutput(this);
    }

    public void unlinkOutput() {
        output.getInputs().removeInput(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldLink fieldLink = (FieldLink) o;
        return Objects.equals(input, fieldLink.input) && Objects.equals(output, fieldLink.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output);
    }

    protected String getObjectString() {
        return input.getObject() != output.getObject() ?
                "<" + input.getObject().toKeyString() + "> " :
                "<> ";
    }

    @Override
    public String toString() {
        return getObjectString() +
                input +
                arrowToString() +
                output +
                " (" + linkParamsToString() + ")";
    }

    protected String arrowToString() {
        return " ---> ";
    }

    protected String linkParamsToString() {
        return "c:" + connected + ", p:" + propagateUpdates;
    }

    public String dumpFieldLink(int depth) {
        return depthToSpace(depth) + this;
    }
}
