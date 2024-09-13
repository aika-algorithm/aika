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
package network.aika.fielddefs;

import network.aika.fielddefs.link.FieldLinkDefinition;
import network.aika.fielddefs.link.FixedFieldLinkDefinition;
import network.aika.fielddefs.link.VariableFieldLinkDefinition;
import network.aika.fields.Field;
import network.aika.fields.Obj;

import java.util.ArrayList;
import java.util.List;

import static network.aika.enums.Direction.INPUT;

/**
 * @author Lukas Molzberger
 */
public class FieldInputDefinition<T extends Type<T, O>, O extends Obj<T, O>, F extends FieldLinkDefinition<F>> {

    protected FieldTag fieldTag;

    protected List<F> inputs = new ArrayList<>();

    public static FixedFieldLinkDefinition argLink(Integer arg) {
        return argLink(arg, true);
    }

    public static FixedFieldLinkDefinition argLink(Integer arg, boolean propagateUpdates) {
        return new FixedFieldLinkDefinition(arg, propagateUpdates);
    }

    public static VariableFieldLinkDefinition varLink(boolean propagateUpdates) {
        return new VariableFieldLinkDefinition(propagateUpdates);
    }

    public static VariableFieldLinkDefinition varLink() {
        return new VariableFieldLinkDefinition(true);
    }

    public FieldInputDefinition(FieldTag fieldTag) {
        this.fieldTag = fieldTag;
    }

    public FieldTag getFieldTag() {
        return fieldTag;
    }

    public void instantiateLinks(Field f) {
        inputs.forEach(fl ->
                fl.instantiate(INPUT, f)
        );
    }

    public List<F> getInputs() {
        return inputs;
    }

    public int size() {
        return inputs.size();
    }

    public void addLink(FieldLinkDefinition fl) {
        inputs.add((F)fl);
    }

    public String toString() {
        return "" + fieldTag;
    }
}
