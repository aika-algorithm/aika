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
import network.aika.type.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static network.aika.fields.InputField.inputField;
import static network.aika.fields.Multiplication.mul;
import static network.aika.fields.SumField.sum;
import static network.aika.fields.oneobject.TestType.SELF;


/**
 * @author Lukas Molzberger
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FieldInstantiationTest {

    @Test
    public void testFieldInstantiation() {
        TypeRegistry registry = new TypeRegistryImpl();

        TestType type = new TestType(registry, "test");

        FieldDefinition a = inputField(type, "a");
        FieldDefinition b = sum(type, "b")
                .in(SELF, a);

        registry.flattenTypeHierarchy();

        // Object and Field initialization

        Obj o = type.instantiate();

        Assertions.assertNull(o.getFieldOutput(a));
        Assertions.assertNull(o.getFieldOutput(b));

        o.setFieldValue(a, 5.0);

        Assertions.assertNotNull(o.getFieldOutput(b));

        Assertions.assertEquals(
                5.0,
                o.getFieldOutput(b).getValue()
        );
    }

    @Test
    public void testFieldInstantiationChain() {
        TypeRegistry registry = new TypeRegistryImpl();

        TestType type = new TestType(registry, "test");

        FieldDefinition a = inputField(type, "a");
        FieldDefinition b = sum(type, "b")
                .in(SELF, a);
        FieldDefinition c = sum(type, "c")
                .in(SELF, b);

        registry.flattenTypeHierarchy();

        // Object and Field initialization

        Obj o = type.instantiate();

        o.setFieldValue(a, 5.0);

        Assertions.assertEquals(
                5.0,
                o.getFieldOutput(c).getValue()
        );
    }

    @Test
    public void testMultiplication() {
        TypeRegistry registry = new TypeRegistryImpl();

        TestType type = new TestType(registry, "test");

        FieldDefinition a = inputField(type, "a");
        FieldDefinition b = inputField(type, "b");

        FieldDefinition c = mul(type, "c")
                .in(SELF, a, 0)
                .in(SELF, b, 1);

        registry.flattenTypeHierarchy();

        // Object and Field initialization

        Obj o = type.instantiate();

        o.setFieldValue(a, 5.0);
        Assertions.assertEquals(0.0, o.getFieldOutput(c).getValue());

        o.setFieldValue(b, 5.0);

        Assertions.assertEquals(
                25.0,
                o.getFieldOutput(c).getValue()
        );
    }
}
