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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author Lukas Molzberger
 */
public class FieldDefinition<O extends FieldObjectDefinition, F extends Field> implements FieldInputDefinition, FieldOutputDefinition {

    protected int fieldId;

    protected Class<F> clazz;

    protected O ref;

    protected String fieldName;

    protected Double tolerance;

    protected List<FieldLinkDefinition> inputs = new ArrayList<>();
    protected List<FieldLinkDefinition> outputs = new ArrayList<>();

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(Class<F> clazz, O ref, String name) {
        this.clazz = clazz;
        this.fieldName = name;
        this.ref = ref;

        ref.addFieldDefinition(this);
    }

    public FieldDefinition(Class<F> clazz, O ref, String name, double tolerance) {
        this(clazz, ref, name);

        this.tolerance = tolerance;
    }

    public FieldDefinition<O, F> in(Integer arg, BiFunction<O, Path, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        Path objectPath = new Path();
        objectPath.add(ref);
        FieldOutputDefinition in = pathProvider.apply(ref, objectPath);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, in, arg, this, propagateUpdates);
        addInput(fl);
        in.addOutput(fl);

        return this;
    }

    public FieldDefinition<O, F> in(Integer arg, BiFunction<O, Path, FieldOutputDefinition> pathProvider) {
        return in(arg, pathProvider, true);
    }

    public FieldDefinition<O, F> out(Integer arg, BiFunction<O, Path, FieldInputDefinition> pathProvider, boolean propagateUpdates) {
        Path objectPath = new Path();
        objectPath.add(ref);
        FieldInputDefinition out = pathProvider.apply(ref, objectPath);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, this, arg, out, propagateUpdates);
        out.addInput(fl);
        addOutput(fl);

        return this;
    }

    public FieldDefinition<O, F> out(Integer arg, BiFunction<O, Path, FieldInputDefinition> pathProvider) {
        return out(arg, pathProvider, true);
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

    public FieldDefinition<O, F> setFieldId(int id) {
        fieldId = id;

        return this;
    }

    public FieldDefinition<O, F> addListener(String name, ReferencedUpdateListener<?> listener) {

        return this;
    }

    public int getFieldId() {
        return fieldId;
    }

    public Class<F> getClazz() {
        return clazz;
    }

    public FieldDefinition<O, F> setClazz(Class<F> clazz) {
        this.clazz = clazz;

        return this;
    }

    public FieldObjectDefinition getReference() {
        return ref;
    }

    public FieldDefinition<O, F> setRef(O ref) {
        this.ref = ref;

        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FieldDefinition<O, F> setFieldName(String fieldName) {
        this.fieldName = fieldName;

        return this;
    }

    public Double getTolerance() {
        return tolerance;
    }

    public FieldDefinition<O, F> setTolerance(Double tolerance) {
        this.tolerance = tolerance;

        return this;
    }

    public ProcessingPhase getPhase() {
        return phase;
    }

    public FieldDefinition<O, F> setPhase(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    public FieldDefinition<O, F> setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return this;
    }

    public FieldDefinition<O, F> setQueued(ProcessingPhase phase) {
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
