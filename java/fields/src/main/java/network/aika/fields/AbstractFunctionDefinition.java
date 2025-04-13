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

import network.aika.fields.defs.FieldLinkDefinitionOutputSide;
import network.aika.fields.defs.FixedArgumentsFieldDefinition;
import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.type.Obj;
import network.aika.type.Type;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class AbstractFunctionDefinition extends FixedArgumentsFieldDefinition {

    public AbstractFunctionDefinition(Type objectType, String name, int numArgs) {
        super(objectType, name, numArgs);
    }

    public AbstractFunctionDefinition(Type objectType, String name, int numArgs, double tolerance) {
        super(objectType, name, numArgs, tolerance);
    }

    protected abstract double computeUpdate(Obj obj, FieldLinkDefinitionOutputSide fl, double u);

    @Override
    public void transmit(Field targetField, FieldLinkDefinitionOutputSide fl, double u) {
        double update = computeUpdate(targetField.getObject(), fl, u);

        receiveUpdate(targetField, update);
    }
}
