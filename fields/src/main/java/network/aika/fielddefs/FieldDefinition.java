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

import network.aika.fielddefs.link.FieldLinkTypeDefinition;
import network.aika.fielddefs.link.InputFieldLinkDefinition;
import network.aika.fielddefs.link.OutputFieldLinkDefinition;
import network.aika.fields.Field;
import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;
import network.aika.fields.Obj;
import network.aika.queue.ProcessingPhase;

import java.util.function.Function;

/**
 * @author Lukas Molzberger
 */
public class FieldDefinition<T extends Type<T, O>, O extends Obj<T, O>> {

    protected FieldTag fieldTag;

    protected Class<? extends Field> clazz;

    protected T objectType;

    protected Double tolerance;

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(Class<? extends Field> clazz, T objectType, FieldTag fieldTag) {
        this.clazz = clazz;
        this.fieldTag = fieldTag;
        this.objectType = objectType;

        objectType.setFieldDefinition(this);
    }

    public FieldDefinition(Class<? extends Field> clazz, T objectType, FieldTag fieldTag, double tolerance) {
        this(clazz, objectType, fieldTag);

        this.tolerance = tolerance;
    }

    public FieldDefinition<T, O> in(FieldOutputDefinition fieldOutDef, FieldTag targetFieldDocu, FieldLinkTypeDefinition flType) {
        Function<O, FieldOutput> pathProvider = o -> o.getFieldOutput(fieldOutDef.getFieldTag());
        FieldInputDefinition out = objectType.getFieldInput(getFieldTag());
        out.addInput(new InputFieldLinkDefinition(pathProvider, targetFieldDocu, flType));

        return this;
    }

    public FieldDefinition<T, O> in(Function<O, FieldOutput> pathProvider, FieldTag targetFieldDocu, FieldLinkTypeDefinition flType) {
        FieldInputDefinition out = objectType.getFieldInput(getFieldTag());
        out.addInput(new InputFieldLinkDefinition(pathProvider, targetFieldDocu, flType));

        return this;
    }

    public FieldDefinition<T, O> out(Function<O, FieldInput> pathProvider, FieldTag targetFieldDocu, FieldLinkTypeDefinition flType) {
        FieldOutputDefinition in = objectType.getFieldOutput(getFieldTag());

        in.addOutput(new OutputFieldLinkDefinition(pathProvider, targetFieldDocu, flType));

        return this;
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
        getFieldInput().instantiateLinks(f);
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

    public T getObjectType() {
        return objectType;
    }

    public FieldDefinition<T, O> setObjectType(T objectType) {
        this.objectType = objectType;

        return this;
    }

    public FieldInputDefinition getFieldInput() {
        return getObjectType().getFieldInput(getFieldTag());
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

    public String toString() {
        return fieldTag + " (" + clazz.getSimpleName() + ")";
    }
}
