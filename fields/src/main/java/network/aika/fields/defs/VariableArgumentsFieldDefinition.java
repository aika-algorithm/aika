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
package network.aika.fields.defs;

import network.aika.fields.direction.Direction;
import network.aika.type.Obj;
import network.aika.type.relations.Relation;
import network.aika.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static network.aika.fields.defs.FieldLinkDefinition.link;

/**
 *
 * @author Lukas Molzberger
 */
public class VariableArgumentsFieldDefinition extends FieldDefinition {

    protected List<FieldLinkDefinitionOutputSide> inputs = new ArrayList<>();

    public VariableArgumentsFieldDefinition(Type objectType, String name) {
        super(objectType, name);
    }

    public VariableArgumentsFieldDefinition(Type objectType, String name, double tolerance) {
        super(objectType, name, tolerance);
    }

    public VariableArgumentsFieldDefinition in(Relation relation, FieldDefinition input) {

        link(input, this, relation.getReverse(), null);

        return this;
    }

    @Override
    public Stream<FieldLinkDefinitionOutputSide> getInputs() {
        return inputs.stream();
    }

    public int size() {
        return inputs.size();
    }

    public void addInput(FieldLinkDefinitionOutputSide fl) {
        inputs.add(fl);
    }
}
