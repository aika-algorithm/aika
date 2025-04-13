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

import network.aika.type.Obj;
import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;

import java.util.stream.Stream;

import static network.aika.fields.oneobject.TestType.TEST_RELATION_FROM;
import static network.aika.fields.oneobject.TestType.TEST_RELATION_TO;
import static network.aika.fields.softmax.SoftmaxInputType.CORRESPONDING_OUTPUT_LINK;
import static network.aika.fields.softmax.SoftmaxInputType.INPUT_TO_NORM;

/**
 *
 * @author Lukas Molzberger
 */
public class TestObject extends ObjImpl {

    TestObject relatedTestObject;

    public TestObject(TestType type) {
        super(type);
    }

    @Override
    public Obj followSingleRelation(Relation rel) {
        if(rel == TEST_RELATION_FROM)
            return getRelatedTestObject();
        else if(rel == TEST_RELATION_TO)
            return getRelatedTestObject();
        else
            throw new RuntimeException("Invalid Relation");
    }

    public TestObject getRelatedTestObject() {
        return relatedTestObject;
    }

    public static void linkObjects(TestObject objA, TestObject objB) {
        objA.relatedTestObject = objB;
        objB.relatedTestObject = objA;
    }

    @Override
    public Stream<Obj> followManyRelation(Relation rel) {
        return Stream.of(relatedTestObject);
    }
}
