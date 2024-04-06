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

import network.aika.fields.*;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;

/**
 * @author Lukas Molzberger
 */
public class BiFunctionFieldDefinition<R extends FieldObject> extends FieldDefinition<R, BiFunction> {

    ReferencedBiFunction<R> f;

    public BiFunctionFieldDefinition(FieldObjectDefinition<R> ref, String name, ReferencedBiFunction<R> f) {
        super(BiFunction.class, ref, name);

        this.f = f;
    }

    public BiFunctionFieldDefinition(FieldObjectDefinition<R> ref, String name, double tolerance, ReferencedBiFunction<R> f) {
        super(BiFunction.class, ref, name, tolerance);

        this.f = f;
    }

    @Override
    public BiFunction instantiate(R reference) {
        BiFunction ff = super.instantiate(reference);
        ff.setFunction(f);
        return ff;
    }
}
