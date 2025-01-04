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

import network.aika.fields.link.FieldLinkDefinition;
import network.aika.type.Obj;
import network.aika.type.relations.Relation;
import network.aika.type.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class VariableArgumentsFieldDefinition<
        T extends Type<T, O>,
        O extends Obj<T, O>
        > extends FieldDefinition<T, O> {

    protected List<FieldLinkDefinition<?, ?, T, O>> inputs = new ArrayList<>();

    public VariableArgumentsFieldDefinition(T objectType, String name) {
        super(objectType, name);
    }

    public VariableArgumentsFieldDefinition(T objectType, String name, double tolerance) {
        super(objectType, name, tolerance);
    }

    public <
            IT extends Type<IT, IO>,
            IO extends Obj<IT, IO>
            > VariableArgumentsFieldDefinition<T, O> in(FieldDefinition<IT, IO> input) {
        return in(SELF, input);
    }

    public <
            IT extends Type<IT, IO>,
            IO extends Obj<IT, IO>
            > VariableArgumentsFieldDefinition<T, O> in(Relation<T, O, IT, IO> relation, FieldDefinition<IT, IO> input) {
        var fl = new FieldLinkDefinition<>(input, this, relation.getReverse());
        addInput(fl);
        input.addOutput(fl);

        return this;
    }

    @Override
    public Stream<FieldLinkDefinition<?, ?, T, O>> getInputs() {
        return inputs.stream();
    }

    public int size() {
        return inputs.size();
    }

    public <
            IT extends Type<IT, IO>,
            IO extends Obj<IT, IO>
            > void addInput(FieldLinkDefinition<IT, IO, T, O> fl) {
        inputs.add(fl);
    }
}
