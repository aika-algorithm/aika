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
public class RelationSelf extends RelationOne {

    public RelationSelf(int relationId, String relationName) {
       super(relationId, relationName);
    }

    @Override
    public void setReversed(Relation reversed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Relation getReverse() {
        return this;
    }

    @Override
    public Obj followOne(Obj fromObj) {
        return fromObj;
    }

    @Override
    public Stream<Obj> followMany(Obj fromObj) {
        return Stream.of(fromObj);
    }

    @Override
    public boolean testRelation(Obj fromObj, Obj toObj) {
        return fromObj == toObj;
    }
}
