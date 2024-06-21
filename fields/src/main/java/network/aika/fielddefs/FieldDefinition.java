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

import network.aika.fields.Field;
import network.aika.fields.FieldObject;
import network.aika.fields.ReferencedUpdateListener;
import network.aika.fields.UpdateListener;
import network.aika.queue.ProcessingPhase;
import network.aika.queue.QueueProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukas Molzberger
 */
public class FieldDefinition<F extends Field> implements FieldInputDefinition, FieldOutputDefinition {

    protected int fieldId;

    protected Class<F> clazz;

    protected FieldObjectDefinition ref;

    protected String fieldName;

    protected Double tolerance;

    protected List<FieldLinkDefinition> inputs = new ArrayList<>();
    protected List<FieldLinkDefinition> outputs = new ArrayList<>();

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(Class<F> clazz, FieldObjectDefinition ref, String name) {
        this.clazz = clazz;
        this.fieldName = name;
        this.ref = ref;

        ref.addFieldDefinition(this);
    }

    public FieldDefinition(Class<F> clazz, FieldObjectDefinition ref, String name, double tolerance) {
        this(clazz, ref, name);

        this.tolerance = tolerance;
    }

    public F instantiate(FieldObject reference) {
        try {
            F instance = clazz
                    .getConstructor(FieldObject.class)
                    .newInstance(reference);

            instance.setFieldDefinition(this);
            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public FieldDefinition<F> setFieldId(int id) {
        fieldId = id;

        return this;
    }

    public FieldDefinition<F> addListener(String name, ReferencedUpdateListener<?> listener) {

        return this;
    }

    public int getFieldId() {
        return fieldId;
    }

    public Class<F> getClazz() {
        return clazz;
    }

    public FieldDefinition<F> setClazz(Class<F> clazz) {
        this.clazz = clazz;

        return this;
    }

    public FieldObjectDefinition getReference() {
        return ref;
    }

    public FieldDefinition<F> setRef(FieldObjectDefinition ref) {
        this.ref = ref;

        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FieldDefinition<F> setFieldName(String fieldName) {
        this.fieldName = fieldName;

        return this;
    }

    public Double getTolerance() {
        return tolerance;
    }

    public FieldDefinition<F> setTolerance(Double tolerance) {
        this.tolerance = tolerance;

        return this;
    }

    public ProcessingPhase getPhase() {
        return phase;
    }

    public FieldDefinition<F> setPhase(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    public FieldDefinition<F> setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return this;
    }

    public FieldDefinition<F> setQueued(ProcessingPhase phase) {
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
