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

import network.aika.fields.FieldObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Lukas Molzberger
 */
public class ObjectDefinition<O extends ObjectDefinition<O>> {

    Map<String, FieldDefinition<O>> fieldDefinitions = new TreeMap<>();
    Map<String, FieldOutputDefinition> fieldOutputDefinitions = new TreeMap<>();

    public void instantiateFields(FieldObject o) {
        fieldDefinitions.values().stream()
                .map(fd ->
                        fd.instantiate(o)
                )
                .toList().stream()
                .forEach(f ->
                        f.getFieldDefinition().instantiateLinks(f)
                );
    }

    public void setFieldDefinition(FieldDefinition<O> fieldDef) {
        fieldDef.setFieldId(fieldDefinitions.size());
        fieldDefinitions.put(fieldDef.getLabel(), fieldDef);
    }

    public FieldOutputDefinition getFieldOutput(String label) {
        return fieldOutputDefinitions.computeIfAbsent(label, k -> new FieldOutputDefinition());
    }

    public void setFieldOutputDefinition(String label, FieldOutputDefinition fieldOutDef) {
        fieldOutputDefinitions.put(label, fieldOutDef);
    }

    public int getNumberOfFields() {
        return fieldDefinitions.size();
    }

    public FieldDefinition<O> getField(String name) {
        return fieldDefinitions.get(name);
    }
}
