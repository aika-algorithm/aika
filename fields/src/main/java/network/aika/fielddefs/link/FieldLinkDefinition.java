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
package network.aika.fielddefs.link;


import network.aika.enums.Direction;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.FieldOutputDefinition;
import network.aika.fielddefs.ObjectPath;
import network.aika.fields.Field;
import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;
import network.aika.fields.Obj;
import network.aika.fields.link.FieldLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Lukas Molzberger
 */
public abstract class FieldLinkDefinition<F extends FieldLinkDefinition<F>> {

    private static final Logger log = LoggerFactory.getLogger(FieldLinkDefinition.class);
    private ObjectPath objectPath;

    private FieldOutputDefinition input;

    private FieldDefinition output;

    boolean propagateUpdates;

    public FieldLinkDefinition(boolean propagateUpdates) {
        this.propagateUpdates = propagateUpdates;
    }

    public void link(ObjectPath objectPath, FieldOutputDefinition input, FieldDefinition output) {
        this.objectPath = objectPath;
        this.input = input;
        this.output = output;

        input.addOutput(this);
    }

    public ObjectPath getObjectPath() {
        return objectPath;
    }

    public FieldOutputDefinition getInput() {
        return input;
    }

    public FieldDefinition getOutput() {
        return output;
    }

    public boolean isPropagateUpdates() {
        return propagateUpdates;
    }

    public void instantiate(Direction dir, Field f) {
        if(dir != getObjectPath().getDirection())
            return;

        List<Obj> objects = getObjectPath().resolve(f.getObject());

        switch (dir) {
            case INPUT:
                objects.stream()
                        .map(o -> o.getField(input.getFieldTag()))
                        .forEach(in -> instantiateAndLink(in, f));
                break;
            case OUTPUT:
                objects.stream()
                        .map(o -> o.getField(output.getFieldTag()))
                        .forEach(out -> instantiateAndLink(f, out));
        }
    }

    public FieldLink instantiateAndLink(FieldOutput input, FieldInput output) {
        if(input == null || output == null) {
            log.warn("Unable to instantiate field link " + this + " because input or output is null.");
            return null;
        }

        FieldLink fl = instantiate(input, output);
        output.getInputs().addInput(fl);
        input.addOutput(fl);

        return fl;
    }

    public FieldLink instantiate(FieldOutput input, FieldInput output) {
        return new FieldLink(input, output);
    }

    public String toString() {
        return "path:" + objectPath + " in: " + input + " out: " + output;
    }
}
