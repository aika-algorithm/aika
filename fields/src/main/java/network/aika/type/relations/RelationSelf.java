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
package network.aika.type.relations;

import network.aika.type.Obj;
import network.aika.type.Type;

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class RelationSelf<T extends Type<T, O>, O extends Obj<T, O>> extends RelationOne<T, O, T, O> {

    public RelationSelf(int relationId, String relationName) {
       super(null, relationId, relationName);
    }

    @Override
    public void setReversed(Relation<T, O, T, O> reversed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Relation<T, O, T, O> getReverse() {
        return this;
    }

    @Override
    public O followOne(O fromObj) {
        return fromObj;
    }

    @Override
    public Stream<O> followAll(O fromObj) {
        return Stream.of(fromObj);
    }

    @Override
    public boolean testRelation(O fromObj, O toObj) {
        return fromObj == toObj;
    }
}