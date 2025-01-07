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
import network.aika.fields.link.ArgFieldLinkDefinition;
import network.aika.fields.link.FieldLinkDefinition;
import network.aika.type.relations.RelationOne;
import network.aika.type.Obj;
import network.aika.type.Type;

import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class FixedArgumentsFieldDefinition<
        T extends Type<T, O>,
        O extends Obj<T, O>
        > extends FieldDefinition<T, O> {

    private final ArgFieldLinkDefinition<T, O, ?, ?>[] inputs;

    public FixedArgumentsFieldDefinition(T objectType, String name, int numArgs) {
        super(objectType, name);

        inputs = new ArgFieldLinkDefinition[numArgs];
    }

    public FixedArgumentsFieldDefinition(T objectType, String name, int numArgs, double tolerance) {
        super(objectType, name, tolerance);

        inputs = new ArgFieldLinkDefinition[numArgs];
    }

    @Override
    public Stream<? extends FieldLinkDefinition<T, O, ?, ?>> getInputs() {
        return Stream.of(inputs)
                .filter(Objects::nonNull);
    }

    @Override
    public void addInput(FieldLinkDefinition<T, O, ?, ?> fl) {
        ArgFieldLinkDefinition<T, O, ?, ?> afl = (ArgFieldLinkDefinition<T, O, ?, ?>) fl;
        inputs[afl.getArgument()] = afl;
    }

    public double getInputValueByArg(O obj, int arg) {
        var fl = inputs[arg];
        return fl.getInputValue(obj);
    }

    public double getUpdatedInputValueByArg(O obj, int arg) {
        var fl = inputs[arg];
        return fl.getUpdatedInputValue(obj);
    }

    public <
            IT extends Type<IT, IO>,
            IO extends Obj<IT, IO>
            >
    FixedArgumentsFieldDefinition<T, O> in(RelationOne<T, O, IT, IO> relationType, FieldDefinition<IT, IO> input, int arg) {
        var flIn = new ArgFieldLinkDefinition<>(this, input, relationType, Direction.INPUT, arg);
        addInput(flIn);

        var flOut = new ArgFieldLinkDefinition<>(input, this, relationType.getReverse(), Direction.OUTPUT, arg);
        input.addOutput(flOut);

        assert relationType != null || objectType.isInstanceOf(input.objectType) || input.objectType.isInstanceOf(objectType);

        return this;
    }
}
