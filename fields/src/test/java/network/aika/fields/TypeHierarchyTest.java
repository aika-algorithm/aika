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

import network.aika.fields.oneobject.TestObject;
import network.aika.fields.oneobject.TestType;
import network.aika.type.relations.RelationTypeOne;


/**
 * @author Lukas Molzberger
 */
public class TypeHierarchyTest {

    public static RelationTypeOne<TestType, TestObject, TestType, TestObject> TEST_RELATION_FROM = new RelationTypeOne<>(TestObject::getRelatedTestObject, "TEST_FROM");
    public static RelationTypeOne<TestType, TestObject, TestType, TestObject> TEST_RELATION_TO = new RelationTypeOne<>(TestObject::getRelatedTestObject, "TEST_TO");

    static {
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM);
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO);
    }

    protected TestType[][][] type;

/*
    @BeforeEach
    public void init() {
        TypeRegistry registry = new TypeRegistryImpl();

        // TopLevel | Level | Id
        type = new TestType[2][3][2];

        initTypeHierarchy(0, "A", registry);
        initTypeHierarchy(1, "B", registry);
    }

    private void initTypeHierarchy(int topLevel, String topLevelLabel, TypeRegistry registry) {
        TestType root = type[topLevel][0][0] = new TestType(registry, topLevelLabel)
                .setClazz(TestObject.class);

        TestType subType0 = type[topLevel][1][0] = new TestType(registry, topLevelLabel + "-SubType-0")
                .setClazz(TestObject.class)
                .addParent(root);

        TestType subType1 = type[topLevel][1][1] = new TestType(registry, topLevelLabel + "-SubType-1")
                .setClazz(TestObject.class)
                .addParent(root);

        TestType subSubType = type[topLevel][2][0] = new TestType(registry, topLevelLabel + "-SubSubType-0")
                .setClazz(TestObject.class)
                .addParent(subType0)
                .addParent(subType1);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void testHierarchy(int linkingPos) {

        // Type and Math Model initialization

        FieldDefinition<TestType> a = inputField(typeA, "a");
        FieldDefinition<TestType> b = inputField(typeA, "b");

        FieldDefinition<TestType> c = sum(typeB, "c")
                .in(TEST_RELATION_FROM, a)
                .in(TEST_RELATION_FROM, b);

        // Object and Field initialization

        TestObject oa = new TestObject(typeA);
        TestObject ob = new TestObject(typeB);

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

 */
}