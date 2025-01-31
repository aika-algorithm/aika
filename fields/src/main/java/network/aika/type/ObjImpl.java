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
import network.aika.queue.QueueProvider;
import network.aika.type.relations.Relation;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public abstract class ObjImpl implements Obj, QueueProvider, Writable<TypeRegistry> {

    protected Type type;

    private Field[] fields;

    @SuppressWarnings("unchecked")
    public ObjImpl(Type type) {
        this.type = type;

        if(type != null)
            fields = new Field[type.getFlattenedTypeInputSide().getNumberOfFields()];
    }

    @Override
    public void initFields() {
        for(short i = 0; i < type.getFlattenedTypeInputSide().getNumberOfFields(); i++) {
            FieldDefinition fd = type.getFlattenedTypeInputSide().getFieldDefinitionIdByIndex(i);

            Field field = getOrCreateFieldInput(fd);
            fd.initializeField(field);
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Stream<Obj> followManyRelation(Relation rel) {
        return Stream.empty();
    }

    @Override
    public Obj followSingleRelation(Relation rel) {
        return null;
    }

    @Override
    public boolean isInstanceOf(Type t) {
        return type.isInstanceOf(t);
    }

    @Override
    public Field getFieldOutput(FieldDefinition fd) {
        short fieldIndex = type.getFlattenedTypeOutputSide().getFieldIndex(fd);

        return fields[fieldIndex];
    }

    @SuppressWarnings("unchecked")
    @Override
    public Field getOrCreateFieldInput(FieldDefinition fd) {
        short fieldIndex = type.getFlattenedTypeInputSide().getFieldIndex(fd);

        Field f = fields[fieldIndex];
        if(f == null) {
            f = fields[fieldIndex] = new Field(this, fd, fieldIndex);
        }

        return f;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Obj setFieldValue(FieldDefinition fd, double v) {
        getOrCreateFieldInput(fd)
                .setValue(v);
        return this;
    }

    @Override
    public double getFieldValue(FieldDefinition fd) {
        Field f = getFieldOutput(fd);
        return f != null ?
                f.getValue() :
                0.0;
    }

    @Override
    public double getFieldUpdatedValue(FieldDefinition fd) {
        Field f = getFieldOutput(fd);
        return f != null ?
                f.getUpdatedValue() :
                0.0;
    }

    @Override
    public Stream<Field> getFields() {
        return Stream.of(fields)
                .filter(Objects::nonNull);
    }

    @Override
    public Queue getQueue() {
        return null;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        // TODO: implement
    }

    @Override
    public void readFields(DataInput in, TypeRegistry m) throws IOException {
        // TODO: implement
    }

    @Override
    public String toKeyString() {
        return "";
    }

    @Override
    public String toString() {
        return "" + type;
    }
}
