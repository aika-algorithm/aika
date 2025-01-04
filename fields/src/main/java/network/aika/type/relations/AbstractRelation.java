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

/**
 *
 * @author Lukas Molzberger
 */
public abstract class AbstractRelation<
        FD extends Type<FD, F>,
        F extends Obj<FD, F>,
        TD extends Type<TD, T>,
        T extends Obj<TD, T>
        > implements Relation<FD, F, TD, T> {

    private int relationId;
    protected String relationName;

    private Relation<TD, T, FD, F> reversed;

    public AbstractRelation(int relationId, String relationName) {
        this.relationId = relationId;
        this.relationName = relationName;
    }

    @Override
    public int getRelationId() {
        return relationId;
    }

    @Override
    public void setReversed(Relation<TD, T, FD, F> reversed) {
        this.reversed = reversed;
    }

    public Relation<TD, T, FD, F> getReverse() {
        return reversed;
    }

    @Override
    public String toString() {
        return getRelationLabel() + " -> " + getReverse().getRelationLabel();
    }

}
