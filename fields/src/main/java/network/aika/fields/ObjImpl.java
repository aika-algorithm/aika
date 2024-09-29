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
package network.aika.fields;

import network.aika.fielddefs.FieldTag;
import network.aika.fielddefs.Type;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static network.aika.fielddefs.FieldTag.FIELD_TAG_COMPARATOR;
import static network.aika.utils.StringUtils.depthToSpace;


/**
 * @author Lukas Molzberger
 */
public abstract class ObjImpl<T extends Type<T, O>, O extends Obj<T, O>> implements Obj<T, O>, QueueProvider {

    protected T type;

    private Map<FieldTag, Field> fields = new TreeMap<>(FIELD_TAG_COMPARATOR);


    @Override
    public T getType() {
        return type;
    }

    @Override
    public void setFields(Map<FieldTag, Field> fields) {
        this.fields = fields;
    }

    @Override
    public Field getField(FieldTag fieldTag) {
        return fields.get(fieldTag);
    }

    @Override
    public FieldInput getFieldInput(FieldTag fieldTag) {
        return getField(fieldTag);
    }

    @Override
    public FieldOutput getFieldOutput(FieldTag fieldTag) {
        return getField(fieldTag);
    }

    @Override
    public Stream<Field> getFields() {
        return fields.values().stream();
    }

    @Override
    public void disconnect() {
        fields.values().forEach(f ->
                f.getInputs().disconnectAndUnlinkInputs(false)
        );
    }

    @Override
    public Queue getQueue() {
        return null;
    }

    @Override
    public boolean isNextRound() {
        return false;
    }

    @Override
    public String toString() {
        return "" + type;
    }

    @Override
    public String dumpObject(int depth) {
        return depthToSpace(depth) + this + "\n" +
                dumpFields(depth + 2);
    }

    @Override
    public String dumpFields(int depth) {
        return getFields()
                .map(f -> f.dumpField(depth))
                .collect(Collectors.joining("\n"));
    }
}
