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

import network.aika.fields.FieldFunction;
import network.aika.fields.FieldObject;
import network.aika.fields.ReferencedFunction;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Lukas Molzberger
 */
public class FieldFunctionDefinition<R extends FieldObject> extends FieldDefinition<R, FieldFunction> {

    ReferencedFunction<R> f;

    public FieldFunctionDefinition(FieldObjectDefinition<R> ref, String name, ReferencedFunction<R> f) {
        super(FieldFunction.class, ref, name);

        this.f = f;
    }

    public FieldFunctionDefinition(FieldObjectDefinition<R> ref, String name, double tolerance, ReferencedFunction<R> f) {
        super(FieldFunction.class, ref, name, tolerance);

        this.f = f;
    }

    @Override
    public FieldFunction instantiate(FieldObject reference) {
        FieldFunction ff = super.instantiate(reference);
        ff.setFunction(f);
        return ff;
    }
}
