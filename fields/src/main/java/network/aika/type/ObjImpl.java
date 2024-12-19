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
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public class ObjImpl<T extends Type<T, O>, O extends Obj<T, O>, M> implements Obj<T, O>, QueueProvider, Writable<M> {

    protected T type;

    private Map<Integer, Field> fields = new TreeMap<>();

    public ObjImpl(T type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initFields(Obj<?, ?> sourceObj) {
        type.getFieldDefinitions()
                .forEach(fd ->
                        fd.initializeField(sourceObj, (O) this)
                );
    }

    @Override
    public T getType() {
        return type;
    }

    @Override
    public boolean isInstanceOf(T t) {
        return type.isInstanceOf(t);
    }

    @Override
    public void setFields(Map<Integer, Field> fields) {
        this.fields = fields;
    }

    @Override
    public Field getField(FieldDefinition<T, O> fd) {
        return fields.get(fd.getFieldId());
    }

    @Override
    public Field getOrCreateField(FieldDefinition<T, O> fd) {
        return fields.computeIfAbsent(
                fd.getFieldId(),
                _ -> new Field(this, fd)
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public O setFieldValue(FieldDefinition<T, O> fd, double v) {
        getOrCreateField(fd)
                .setValue(v);
        return (O) this;
    }

    @Override
    public double getFieldValue(FieldDefinition<T, O> fd) {
        Field f = getField(fd);
        return f != null ?
                f.getValue() :
                0.0;
    }

    @Override
    public double getFieldUpdatedValue(FieldDefinition<T, O> fd) {
        Field f = getField(fd);
        return f != null ?
                f.getUpdatedValue() :
                0.0;
    }

    @Override
    public Stream<Field> getFields() {
        return fields.values().stream();
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
    public void readFields(DataInput in, M m) throws IOException {
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
