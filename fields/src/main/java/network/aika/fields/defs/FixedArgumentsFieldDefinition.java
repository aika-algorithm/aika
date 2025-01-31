package network.aika.fields.defs;

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
import network.aika.fields.direction.Direction;
import network.aika.type.relations.RelationOne;
import network.aika.type.Obj;
import network.aika.type.Type;

import java.util.Objects;
import java.util.stream.Stream;

import static network.aika.fields.defs.FieldLinkDefinition.link;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class FixedArgumentsFieldDefinition extends FieldDefinition {

    private final FieldLinkDefinitionOutputSide[] inputs;

    public FixedArgumentsFieldDefinition(Type objectType, String name, int numArgs) {
        super(objectType, name);

        inputs = new FieldLinkDefinitionOutputSide[numArgs];
    }

    public FixedArgumentsFieldDefinition(Type objectType, String name, int numArgs, double tolerance) {
        super(objectType, name, tolerance);

        inputs = new FieldLinkDefinitionOutputSide[numArgs];
    }

    @Override
    public Stream<FieldLinkDefinitionOutputSide> getInputs() {
        return Stream.of(inputs)
                .filter(Objects::nonNull);
    }

    @Override
    public void addInput(FieldLinkDefinitionOutputSide fl) {
        inputs[fl.getArgument()] = fl;
    }

    public double getInputValueByArg(Obj obj, int arg) {
        var fl = inputs[arg];
        return fl.getInputValue(obj);
    }

    public double getUpdatedInputValueByArg(Obj obj, int arg) {
        var fl = inputs[arg];
        return fl.getUpdatedInputValue(obj);
    }

    public FixedArgumentsFieldDefinition in(RelationOne relation, FieldDefinition input, int arg) {
        link(input, this, relation.getReverse(), arg);

        assert relation != null || objectType.isInstanceOf(input.objectType) || input.objectType.isInstanceOf(objectType);

        return this;
    }
}
