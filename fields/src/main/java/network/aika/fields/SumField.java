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
import network.aika.fields.link.FieldLink;
import network.aika.fields.link.VariableFieldInputs;

import static network.aika.utils.ToleranceUtils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class SumField<O extends Obj> extends Field<O, VariableFieldInputs, FieldLink> {

    public static <T extends Type<T, O>, O extends Obj<T, O>> FieldDefinition<T, O> sum(T ref, FieldTag fieldTag) {
        return new FieldDefinition<>(
                SumField.class,
                ref,
                fieldTag,
                TOLERANCE
        );
    }

    public SumField() {
        super(new VariableFieldInputs());
    }
}
