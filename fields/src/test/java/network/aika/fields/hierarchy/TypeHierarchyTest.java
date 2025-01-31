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
package network.aika.fields.hierarchy;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.oneobject.TestObject;
import network.aika.fields.oneobject.TestType;
import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.fields.Addition.add;
import static network.aika.fields.IdentityFunction.identity;
import static network.aika.fields.InputField.inputField;
import static network.aika.fields.Subtraction.sub;
import static network.aika.fields.oneobject.TestType.SELF;


/**
 * @author Lukas Molzberger
 */
public class TypeHierarchyTest {

    TypeRegistry registry;
    TestType parent;
    TestType child;

    @BeforeEach
    public void init() {
        registry = new TypeRegistryImpl();

        parent = new TestType(registry, "Parent");

        child = (TestType) new TestType(registry, "Child")
                .addParent(parent);
    }

    @Test
    public void testInheritance() {

        // Type and Math Model initialization

        FieldDefinition a = inputField(parent, "a");
        FieldDefinition b = inputField(parent, "b");

        FieldDefinition parentC = add(parent, "c")
                .in(SELF, a, 0)
                .in(SELF, b, 1);

        FieldDefinition c = sub(child, "c")
                .in(SELF, a, 0)
                .in(SELF, b, 1)
                .setParent(parentC);

        FieldDefinition d = identity(parent, "d")
                .in(SELF, c, 0);

        registry.flattenTypeHierarchy();

        Assertions.assertEquals(4, child.getFlattenedTypeOutputSide().getNumberOfFields());

        // Object and Field initialization

        TestObject obj = child.instantiate();
        obj.setFieldValue(a, 50.0);
        obj.setFieldValue(b, 20.0);

        Assertions.assertEquals(4, obj.getFields().count());

        Assertions.assertEquals(
                30.0,
                obj.getFieldOutput(c).getValue()
        );

        Assertions.assertEquals(
                30.0,
                obj.getFieldOutput(d).getValue()
        );
    }
}
