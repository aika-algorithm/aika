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

import network.aika.type.Type;
import network.aika.type.Obj;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class RelationOne extends AbstractRelation {

    public RelationOne(int relationId, String relationName) {
        super(relationId, relationName);
    }

    public Obj followOne(Obj fromObj) {
        return fromObj.followSingleRelation(this);
    }

    @Override
    public Stream<Obj> followMany(Obj fromObj) {
        Obj toObj = followOne(fromObj);
        return toObj != null ?
                Stream.of(toObj) :
                Stream.empty();
    }

    @Override
    public boolean testRelation(Obj fromObj, Obj toObj) {
        return followOne(fromObj) == toObj;
    }

    @Override
    public String getRelationLabel() {
        return relationName + " (One)";
    }
}
