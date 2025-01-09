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
import network.aika.fields.defs.FieldLinkDefinitionOutputSide;
import network.aika.type.Obj;
import network.aika.type.Type;

/**
 * @author Lukas Molzberger
 */
public class FieldActivationFunction<
        T extends Type<T, O>,
        O extends Obj<T, O>
        > extends AbstractFunctionDefinition<T, O> {

    private ActivationFunction actFunction;


    public static <
            T extends Type<T, O>,
            O extends Obj<T, O>
            > FieldActivationFunction<T, O> actFunc(T ref, String name, ActivationFunction actF, Double tolerance) {
        return new FieldActivationFunction<>(
                ref,
                name,
                actF,
                tolerance
        );
    }

    public FieldActivationFunction(T ref, String name, ActivationFunction actFunction, Double tolerance) {
        super(ref, name, 1);

        this.tolerance = tolerance;
        this.actFunction = actFunction;
    }

    @Override
    protected double computeUpdate(O obj, FieldLinkDefinitionOutputSide<T, O, ?, ?> fl, double u) {
        double value = obj.getOrCreateField(this).getValue();
        return actFunction.f(fl.getUpdatedInputValue(obj)) - value;
    }
}
