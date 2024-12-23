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

import network.aika.fields.field.Field;
import network.aika.fields.link.ArgFieldLinkDefinition;
import network.aika.type.Obj;
import network.aika.type.Type;

/**
 * @author Lukas Molzberger
 */
public class ExponentialFunction<
        T extends Type<T, O>,
        O extends Obj<T, O>
        > extends AbstractFunctionDefinition<T, O> {

    public static <
            T extends Type<T, O>,
            O extends Obj<T, O>
            > ExponentialFunction<T, O> exp(T ref, String name) {
        return new ExponentialFunction<>(
                ref,
                name
        );
    }

    public ExponentialFunction(T ref, String name) {
        super(ref, name, 1);
    }

    @Override
    public void initializeField(Obj<?, ?> fromObj, O toObj) {
        double valueArg0 = getInputValueByArg(toObj, 0);

        Field field = toObj.getOrCreateField(this);
        field.setValue(Math.exp(valueArg0));
    }

    @Override
    protected double computeUpdate(O obj, ArgFieldLinkDefinition<?, ?, T, O> fl, double u)  {
        return Math.exp(u);
    }
}