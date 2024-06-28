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

import network.aika.enums.Direction;
import network.aika.fields.Field;
import network.aika.fields.FieldObject;
import network.aika.queue.ProcessingPhase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Lukas Molzberger
 */
public class FieldDefinition<O extends FieldObjectDefinition<O>> implements FieldInputDefinition, FieldOutputDefinition {

    protected int fieldId;

    protected Class<? extends Field> clazz;

    protected O ref;

    protected String fieldName;

    protected Double tolerance;

    protected List<FieldLinkDefinition> inputs = new ArrayList<>();
    protected List<FieldLinkDefinition> outputs = new ArrayList<>();

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(Class<? extends Field> clazz, O ref, String name) {
        this.clazz = clazz;
        this.fieldName = name;
        this.ref = ref;

        ref.addFieldDefinition(this);
    }

    public FieldDefinition(Class<? extends Field> clazz, O ref, String name, double tolerance) {
        this(clazz, ref, name);

        this.tolerance = tolerance;
    }

    public FieldDefinition<O> in(Integer port, Integer arg, BiFunction<O, Path, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        Path objectPath = new Path(Direction.INPUT);
        objectPath.add(new FieldObjectRelationDefinition(ref, o -> o));
        FieldOutputDefinition in = pathProvider.apply(ref, objectPath);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, port, in, arg, this, propagateUpdates);
        addInput(fl);
        in.addOutput(fl);

        return this;
    }

    public FieldDefinition<O> in(Integer arg, BiFunction<O, Path, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        return in(null, arg, pathProvider, propagateUpdates);
    }

    public FieldDefinition<O> in(Integer arg, BiFunction<O, Path, FieldOutputDefinition> pathProvider) {
        return in(arg, pathProvider, true);
    }

    public FieldDefinition<O> out(Integer port, Integer arg, BiFunction<O, Path, FieldInputDefinition> pathProvider, boolean propagateUpdates) {
        Path objectPath = new Path(Direction.OUTPUT);
        objectPath.add(new FieldObjectRelationDefinition(ref, o -> o));
        FieldInputDefinition out = pathProvider.apply(ref, objectPath);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, port, this, arg, out, propagateUpdates);
        out.addInput(fl);
        addOutput(fl);

        return this;
    }

    public FieldDefinition<O> out(BiFunction<O, Path, FieldInputDefinition> pathProvider, boolean propagateUpdates) {
        return out(null, null, pathProvider, propagateUpdates);
    }

    public FieldDefinition<O> out(Integer port, BiFunction<O, Path, FieldInputDefinition> pathProvider) {
        return out(port, null, pathProvider, true);
    }

    public FieldDefinition<O> out(BiFunction<O, Path, FieldInputDefinition> pathProvider) {
        return out(null, pathProvider);
    }

    public Field instantiate(FieldObject fo) {
        try {
            Field instance = clazz.newInstance();

            instance.setFieldObject(fo);
            instance.setFieldDefinition(this);
            instance.setFieldName(fieldName);
            instance.setTolerance(tolerance);

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void instantiateLinks(Field f) {
        inputs.forEach(fl ->  fl.);
    }

    public FieldDefinition<O> setFieldId(int id) {
        fieldId = id;

        return this;
    }

    public int getFieldId() {
        return fieldId;
    }

    public Class<? extends Field> getClazz() {
        return clazz;
    }

    public FieldDefinition<O> setClazz(Class<? extends Field> clazz) {
        this.clazz = clazz;

        return this;
    }

    public FieldObjectDefinition getReference() {
        return ref;
    }

    public FieldDefinition<O> setRef(O ref) {
        this.ref = ref;

        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FieldDefinition<O> setFieldName(String fieldName) {
        this.fieldName = fieldName;

        return this;
    }

    public Double getTolerance() {
        return tolerance;
    }

    public FieldDefinition<O> setTolerance(Double tolerance) {
        this.tolerance = tolerance;

        return this;
    }

    public ProcessingPhase getPhase() {
        return phase;
    }

    public FieldDefinition<O> setPhase(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    public FieldDefinition<O> setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return this;
    }

    public FieldDefinition<O> setQueued(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }

    @Override
    public void addInput(FieldLinkDefinition fl) {
        inputs.add(fl);
    }

    @Override
    public int size() {
        return inputs.size();
    }

    @Override
    public void addOutput(FieldLinkDefinition fl) {
        outputs.add(fl);
    }

}
