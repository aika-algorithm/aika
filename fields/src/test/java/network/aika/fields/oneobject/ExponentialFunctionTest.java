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
package network.aika.fields.oneobject;

import network.aika.fields.defs.FieldDefinition;
import network.aika.type.Obj;
import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static network.aika.fields.ExponentialFunction.exp;
import static network.aika.fields.InputField.inputField;


/**
 * @author Lukas Molzberger
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExponentialFunctionTest {

    @Test
    public void testExponentialFunction() {
        TypeRegistry reg = new TypeRegistryImpl();

        TestType type = new TestType(reg, "test");

        FieldDefinition<TestType, TestObject> a = inputField(type, "a");
        FieldDefinition<TestType, TestObject> b = exp(type, "b")
                .in(a, 0);

        Obj o = new ObjImpl(type);

        Assertions.assertNull(o.getField(a));
        Assertions.assertNull(o.getField(b));

        o.setFieldValue(a, 5.0);

        Assertions.assertNotNull(o.getField(b));

        Assertions.assertEquals(
                148.413159102576603,
                o.getField(b).getValue()
        );
    }
}
