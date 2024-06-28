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

import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.FieldObjectDefinition;
import network.aika.fields.link.FieldLink;

/**
 * @author Lukas Molzberger
 */
public class Multiplication<O extends FieldObject> extends AbstractFunction<O> {

    public static <O extends FieldObjectDefinition<O>> FieldDefinition<O> mul(O ref, String label) {
        return new FieldDefinition<>(Multiplication.class, ref, label);
    }

    @Override
    protected int getNumberOfFunctionArguments() {
        return 2;
    }

    @Override
    protected double computeUpdate(FieldLink fl, double u) {
        return u * getInputValueByArg(fl.getArgument() == 0 ? 1 : 0);
    }
}
