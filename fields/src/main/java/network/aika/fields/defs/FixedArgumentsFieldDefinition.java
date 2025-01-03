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
import network.aika.fields.link.ArgFieldLinkDefinition;
import network.aika.fields.link.FieldLinkDefinition;
import network.aika.type.relations.RelationTypeOne;
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

    private final ArgFieldLinkDefinition<?, ?, T, O>[] inputs;

    public FixedArgumentsFieldDefinition(T objectType, String name, int numArgs) {
        super(objectType, name);

        inputs = new ArgFieldLinkDefinition[numArgs];
    }

    public FixedArgumentsFieldDefinition(T objectType, String name, int numArgs, double tolerance) {
        super(objectType, name, tolerance);

        inputs = new ArgFieldLinkDefinition[numArgs];
    }

    @Override
    public Stream<? extends FieldLinkDefinition<?, ?, T, O>> getInputs() {
        return Stream.of(inputs)
                .filter(Objects::nonNull);
    }

    @Override
    public <
            IT extends Type<IT, IO>,
            IO extends Obj<IT, IO>
            >
    void addInput(FieldLinkDefinition<IT, IO, T, O> fl) {
        ArgFieldLinkDefinition<?, ?, T, O> afl = (ArgFieldLinkDefinition<IT, IO, T, O>) fl;
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
    FixedArgumentsFieldDefinition<T, O> in(FieldDefinition<IT, IO> input, int arg) {
        return in(SELF, input, arg);
    }

    public <
            IT extends Type<IT, IO>,
            IO extends Obj<IT, IO>
            >
    FixedArgumentsFieldDefinition<T, O> in(RelationTypeOne<T, O, IT, IO> relationType, FieldDefinition<IT, IO> input, int arg) {
        var fl = new ArgFieldLinkDefinition<>(input, this, relationType.getReverse(), arg);
        addInput(fl);
        input.addOutput(fl);

        assert relationType != null || objectType.isInstanceOf(input.objectType) || input.objectType.isInstanceOf(objectType);

        return this;
    }
}
