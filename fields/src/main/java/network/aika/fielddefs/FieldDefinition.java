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

import network.aika.fielddefs.inputs.FieldInputsDefinition;
import network.aika.fielddefs.link.FieldLinkDefinition;
import network.aika.fields.Field;
import network.aika.fields.Obj;
import network.aika.queue.ProcessingPhase;

import java.util.List;
import java.util.function.BiFunction;

import static network.aika.enums.Direction.OUTPUT;

/**
 * @author Lukas Molzberger
 */
public class FieldDefinition<T extends Type<T, O>, O extends Obj<T, O>> implements FieldInputDefinition<T, O> {

    protected FieldTag fieldTag;

    protected Class<? extends Field> clazz;

    protected T objectType;

    protected Double tolerance;

    protected FieldInputsDefinition<T, O, ?> inputs;

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<T, O, ?> inputs, T objectType, FieldTag fieldTag) {
        this.clazz = clazz;
        this.fieldTag = fieldTag;
        this.objectType = objectType;
        this.inputs = inputs;

        inputs.setFieldDefinition(this);
        objectType.setFieldDefinition(this);
    }

    public FieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<T, O, ?> inputs, T objectType, FieldTag fieldTag, double tolerance) {
        this(clazz, inputs, objectType, fieldTag);

        this.tolerance = tolerance;
    }

    @Override
    public FieldInputsDefinition getInputs() {
        return inputs;
    }


    public FieldDefinition<T, O> out(BiFunction<T, ObjectPath, FieldDefinition> pathProvider, boolean propagateUpdates) {
        ObjectPath objectPath = new ObjectPath(OUTPUT);
        objectPath.add(new ObjectRelationDefinition("OUT", objectType, o -> List.of(o)));

        FieldOutputDefinition in = objectType.getFieldOutput(getFieldTag());
        FieldDefinition out = pathProvider.apply(objectType, objectPath);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, in, out, propagateUpdates);
        out.getInputs().addInput(fl);
        in.addOutput(fl);

        return this;
    }

    public FieldDefinition<T, O> out(BiFunction<T, ObjectPath, FieldDefinition> pathProvider) {
        return out(pathProvider, true);
    }

    public Field instantiate(O o) {
        try {
            Field instance = clazz.newInstance();

            instance.setObject(o);
            instance.setFieldDefinition(this);

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void instantiateInputLinks(Field f) {
        inputs.instantiateLinks(f);
    }

    public FieldDefinition<T, O> setFieldTag(FieldTag fieldTag) {
        this.fieldTag = fieldTag;

        return this;
    }

    public FieldTag getFieldTag() {
        return fieldTag;
    }

    public Class<? extends Field> getClazz() {
        return clazz;
    }

    public FieldDefinition<T, O> setClazz(Class<? extends Field> clazz) {
        this.clazz = clazz;

        return this;
    }

    @Override
    public T getObjectType() {
        return objectType;
    }

    public FieldDefinition<T, O> setObjectType(T objectType) {
        this.objectType = objectType;

        return this;
    }

    public FieldOutputDefinition getFieldOutput() {
        return getObjectType().getFieldOutput(getFieldTag());
    }

    public Double getTolerance() {
        return tolerance;
    }

    public FieldDefinition<T, O> setTolerance(Double tolerance) {
        this.tolerance = tolerance;

        return this;
    }

    public ProcessingPhase getPhase() {
        return phase;
    }

    public FieldDefinition<T, O> setPhase(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    public FieldDefinition<T, O> setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return this;
    }

    public FieldDefinition<T, O> setQueued(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }
}
