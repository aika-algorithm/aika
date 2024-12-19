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

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.model.TestObject;
import network.aika.fields.model.TestType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static network.aika.fields.InputField.inputField;
import static network.aika.fields.Subtraction.sub;
import static network.aika.fields.model.TestObject.linkObjectsAndInitFields;


/**
 * @author Lukas Molzberger
 */
public class SubtractionWithObjectsTest extends AbstractTestWithObjects {


    @BeforeEach
    public void init() {
        super.init();
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void testSubtraction(int linkingPos) {

        // Type and Math Model initialization

        FieldDefinition<TestType, TestObject> a = inputField(typeA, "a");
        FieldDefinition<TestType, TestObject> b = inputField(typeA, "b");

        FieldDefinition<TestType, TestObject> c = sub(typeB, "c")
                .in(TEST_RELATION_FROM, a, 0)
                .in(TEST_RELATION_FROM, b, 1);

        // Object and Field initialization

        TestObject oa = typeA.instantiate();
        TestObject ob = typeB.instantiate();

        if(linkingPos == 0)
            linkObjectsAndInitFields(oa, ob);

        oa.setFieldValue(a, 50.0);

        if(linkingPos == 1)
            linkObjectsAndInitFields(oa, ob);

        oa.setFieldValue(b, 20.0);

        if(linkingPos == 2)
            linkObjectsAndInitFields(oa, ob);

        Assertions.assertEquals(
                30.0,
                ob.getField(c).getValue()
        );
    }
}
