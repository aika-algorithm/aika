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

import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.RelationOne;
import network.aika.type.relations.RelationSelf;

import java.util.List;

/**
 *
 * @author Lukas Molzberger
 */
public class TestType extends Type {

    public static final RelationSelf SELF = new RelationSelf(0, "TEST_SELF");

    public static final RelationOne TEST_RELATION_FROM = new RelationOne(1, "TEST_FROM");
    public static final RelationOne TEST_RELATION_TO = new RelationOne(2, "TEST_TO");

    static {
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM);
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO);
    }

    public TestType(TypeRegistry registry, String name) {
        super(registry, name);

        relations.addAll(List.of(SELF, TEST_RELATION_FROM, TEST_RELATION_TO));
    }

    public TestObject instantiate() {
        return new TestObject(this);
    }
}
