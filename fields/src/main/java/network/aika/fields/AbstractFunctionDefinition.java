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

import network.aika.fields.defs.FixedArgumentsFieldDefinition;
import network.aika.fields.link.ArgFieldLinkDefinition;
import network.aika.fields.link.FieldLinkDefinition;
import network.aika.type.Obj;
import network.aika.type.Type;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class AbstractFunctionDefinition<
        T extends Type<T, O>,
        O extends Obj<T, O>
        > extends FixedArgumentsFieldDefinition<T, O> {

    public AbstractFunctionDefinition(T objectType, String name, int numArgs) {
        super(objectType, name, numArgs);
    }

    public AbstractFunctionDefinition(T objectType, String name, int numArgs, double tolerance) {
        super(objectType, name, numArgs, tolerance);
    }

    protected abstract double computeUpdate(O obj, ArgFieldLinkDefinition<?, ?, T, O> fl, double u);

    @Override
    public void receiveUpdate(O obj, FieldLinkDefinition<?, ?, T, O> fl, double u) {
        double update = computeUpdate(obj, (ArgFieldLinkDefinition<?, ?, T, O>) fl, u);

        receiveUpdate(obj, update);
    }
}