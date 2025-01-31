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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static network.aika.fields.Division.div;
import static network.aika.fields.InputField.inputField;
import static network.aika.fields.oneobject.TestObject.linkObjects;
import static network.aika.fields.oneobject.TestType.TEST_RELATION_FROM;


/**
 * @author Lukas Molzberger
 */
public class DivisionTest extends AbstractTestWithObjects {


    @BeforeEach
    public void init() {
        super.init();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void testDivision(int linkingPos) {

        // Type and Math Model initialization

        FieldDefinition a = inputField(typeA, "a");
        FieldDefinition b = inputField(typeA, "b");

        FieldDefinition c = div(typeB, "c")
                .in(TEST_RELATION_FROM, a, 0)
                .in(TEST_RELATION_FROM, b, 1);

        registry.flattenTypeHierarchy();

        // Object and Field initialization

        TestObject oa = typeA.instantiate();
        TestObject ob = typeB.instantiate();

        if(linkingPos == 0) {
            linkObjects(oa, ob);
            ob.initFields();
        }

        oa.setFieldValue(a, 25.0);

        if(linkingPos == 1) {
            linkObjects(oa, ob);
            ob.initFields();
        }

        Assertions.assertEquals(0.0, ob.getFieldValue(c));

        oa.setFieldValue(b, 5.0);

        if(linkingPos == 2) {
            linkObjects(oa, ob);
            ob.initFields();
        }

        Assertions.assertEquals(
                5.0,
                ob.getFieldOutput(c).getValue()
        );

        oa.setFieldValue(b, 10.0);

        Assertions.assertEquals(
                2.5,
                ob.getFieldOutput(c).getValue()
        );
    }
}
