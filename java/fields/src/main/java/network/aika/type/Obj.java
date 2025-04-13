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

package network.aika.type;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.field.Field;
import network.aika.queue.Queue;
import network.aika.type.relations.Relation;

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public interface Obj {

    void initFields();

    Type getType();

    Stream<? extends Obj> followManyRelation(Relation rel);

    Obj followSingleRelation(Relation rel);

    Field getOrCreateFieldInput(FieldDefinition fd);

    Field getFieldOutput(FieldDefinition fd);

    Stream<Field> getFields();

    Obj setFieldValue(FieldDefinition fd, double v);

    double getFieldValue(FieldDefinition fd);

    double getFieldUpdatedValue(FieldDefinition fd);

    Queue getQueue();

    String toKeyString();

    boolean isInstanceOf(Type objectType);
}
