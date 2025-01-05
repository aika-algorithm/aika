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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.fields.InputField.inputField;
import static network.aika.fields.SumField.sum;
import static network.aika.fields.oneobject.TestObject.linkObjectsAndInitFields;
import static network.aika.fields.oneobject.TestType.TEST_RELATION_TO;


/**
 * @author Lukas Molzberger
 */
public class FieldInstantiationWithObjectsTest extends AbstractTestWithObjects {
    

    private FieldDefinition<TestType, TestObject> fieldA;
    private FieldDefinition<TestType, TestObject> fieldB;


    @BeforeEach
    public void init() {
        // Type and Math Model initialization
        super.init();

        fieldA = inputField(typeA, "a");
        fieldB = sum(typeB, "b")
                .in(TEST_RELATION_TO, fieldA);
    }

    @Test
    public void testPropagateValue() {
        // Object and Field initialization

        TestObject objA = typeA.instantiate();
        TestObject objB = typeB.instantiate();
        linkObjectsAndInitFields(objA, objB);

        objA.setFieldValue(fieldA, 5.0);

        Assertions.assertEquals(
                5.0,
                objB.getOrCreateField(fieldB).getValue()
        );
    }

    @Test
    public void testInitFields() {

        // Object and Field initialization

        TestObject objA = new TestObject(typeA);
        objA.setFieldValue(fieldA, 5.0);

        TestObject objB = new TestObject(typeB);

        linkObjectsAndInitFields(objA, objB);

        Assertions.assertEquals(
                5.0,
                objB.getField(fieldB).getValue()
        );
    }
}
