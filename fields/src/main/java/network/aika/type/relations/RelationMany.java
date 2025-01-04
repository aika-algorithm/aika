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
public class RelationMany<
        FD extends Type<FD, F>,
        F extends Obj<FD, F>,
        TD extends Type<TD, T>,
        T extends Obj<TD, T>
        > extends AbstractRelation<FD, F, TD, T> {

    private final Function<F, Stream<T>> transitionFunction;

    public RelationMany(Function<F, Stream<T>> transitionFunction, int relationId, String relationName) {
        super(relationId, relationName);
        this.transitionFunction = transitionFunction;
    }

    public Stream<T> followAll(F fromObj) {
        if(transitionFunction == null)
            return Stream.empty();

        return transitionFunction.apply(fromObj);
    }

    @Override
    public boolean testRelation(F fromObj, T toObj) {
        return getReverse().testRelation(toObj, fromObj);
    }

    @Override
    public String getRelationLabel() {
        return relationName + " (Many)";
    }
}
