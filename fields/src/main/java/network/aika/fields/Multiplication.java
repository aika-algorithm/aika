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
package network.aika.fields;

import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.fields.field.Field;
import network.aika.type.Type;
import network.aika.type.Obj;

/**
 * @author Lukas Molzberger
 */
public class Multiplication<
        T extends Type<T, O>,
        O extends Obj<T, O>
        > extends AbstractFunctionDefinition<T, O> {

    public static <
            T extends Type<T, O>,
            O extends Obj<T, O>
            > Multiplication<T, O> mul(T ref, String name) {
        return new Multiplication<>(
                ref,
                name
        );
    }

    public Multiplication(T ref, String name) {
        super(ref, name, 2);
    }

    @Override
    public <RT extends Type<RT, RO>, RO extends Obj<RT, RO>> void initializeField(Field<T, O> field) {
        O toObj = field.getObject();
        double valueArg0 = getInputValueByArg(toObj, 0);
        double valueArg1 = getInputValueByArg(toObj, 1);

        field.setValue(valueArg0 * valueArg1);
    }

    @Override
    protected double computeUpdate(O obj, FieldLinkDefinition<?, ?, T, O> fl, double u) {
        return u * getInputValueByArg(
                obj,
                fl.getArgument() == 0 ?
                                1 :
                                0
                );
    }
}
