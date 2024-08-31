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
package network.aika.fielddefs;


import network.aika.fields.Obj;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Lukas Molzberger
 */
public class ObjectRelationDefinition<IT extends Type<IT, IO>, IO extends Obj<IT, IO>, OT extends Type<OT, OO>, OO extends Obj<OT, OO>>  {

    private IT fromObject;

    private OT toObject;

    private Function<IO, Set<OO>> mapping;

    private ObjectRelationType relationType;

    private ObjectRelationDefinition reversed;

    public ObjectRelationDefinition(IT fromObject, OT toObject, ObjectRelationType relType, Function<IO, Set<OO>> mapping, ObjectRelationDefinition<OT, OO, IT, IO> reversed) {
        this.fromObject = fromObject;
        this.toObject = toObject;
        this.relationType = relType;
        this.mapping = mapping;
        if(reversed != null) {
            this.reversed = reversed;
            reversed.reversed = this;
        }
    }

    public static <O> Set<O> single(O o) {
        if(o == null)
            return Collections.emptySet();

        return Set.of(o);
    }

    public Type getFromObject() {
        return fromObject;
    }

    public Type getToObject() {
        return toObject;
    }

    public ObjectRelationType getRelationType() {
        return relationType;
    }

    public ObjectRelationDefinition getReversed() {
        return reversed;
    }

    public Set<OO> followRelation(IO o) {
        return mapping.apply(o);
    }

    public String toString() {
        return fromObject.getName() + "." + toObject.getName() + "." + toObject;
    }
}
