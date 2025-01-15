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

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public interface Obj<T extends Type<T, O>, O extends Obj<T, O>> {

    void initFields();

    T getType();

    Field<T, O> getOrCreateFieldInput(FieldDefinition<T, O> fd);

    Field<T, O> getFieldOutput(FieldDefinition<T, O> fd);

    Stream<Field<T, O>> getFields();

    O setFieldValue(FieldDefinition<T, O> fd, double v);

    double getFieldValue(FieldDefinition<T, O> fd);

    double getFieldUpdatedValue(FieldDefinition<T, O> fd);

    Queue getQueue();

    String toKeyString();

    boolean isInstanceOf(T objectType);
}
