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
import network.aika.fielddefs.FieldTag;
import network.aika.fielddefs.Type;
import network.aika.fielddefs.inputs.ArgInputs;
import network.aika.fields.link.FixedFieldLink;

/**
 * @author Lukas Molzberger
 */
public class Multiplication<O extends Obj> extends AbstractFunction<O> {

    public static <T extends Type<T, O>, O extends Obj<T, O>> FieldDefinition<T, O> mul(T ref, FieldTag fieldTag) {
        return new FieldDefinition<>(
                Multiplication.class,
                new ArgInputs(),
                ref,
                fieldTag
        );
    }

    public Multiplication() {
        super(2);
    }

    @Override
    protected double computeUpdate(FixedFieldLink fl, double u) {
        return u * getInputs()
                .getInputValueByArg(
                        fl.getArgument() == 0 ?
                                1 :
                                0
                );
    }
}
