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
import network.aika.fielddefs.inputs.FieldInputsDefinition;
import network.aika.fielddefs.link.FieldLinkDefinition;
import network.aika.fields.Field;
import network.aika.fields.FieldObject;
import network.aika.queue.ProcessingPhase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Lukas Molzberger
 */
public class FieldDefinition<O extends ObjectDefinition<O>> implements FieldInputDefinition<O>, FieldOutputDefinition {

    protected int fieldId;

    protected Class<? extends Field> clazz;

    protected O object;

    protected String label;

    protected Double tolerance;

    protected FieldInputsDefinition<O, ?> inputs;
    protected List<FieldLinkDefinition> outputs = new ArrayList<>();

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<O, ?> inputs, O object, String label) {
        this.clazz = clazz;
        this.label = label;
        this.object = object;
        this.inputs = inputs;

        inputs.setObject(object);
        object.setFieldDefinition(this);
    }

    public FieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<O, ?> inputs, O object, String label, double tolerance) {
        this(clazz, inputs, object, label);

        this.tolerance = tolerance;
    }

    @Override
    public FieldInputsDefinition getInputs() {
        return inputs;
    }

    public FieldDefinition<O> out(Integer arg, BiFunction<O, ObjectPath, FieldInputDefinition> pathProvider, boolean propagateUpdates) {
        ObjectPath objectPath = new ObjectPath(Direction.OUTPUT);
        objectPath.add(new ObjectRelationDefinition(object, o -> List.of(o)));
        FieldInputDefinition out = pathProvider.apply(object, objectPath);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, this, out.getInputs(), propagateUpdates);
        out.getInputs().addInput(fl);
        addOutput(fl);

        return this;
    }

    public FieldDefinition<O> out(BiFunction<O, ObjectPath, FieldInputDefinition> pathProvider, boolean propagateUpdates) {
        return out(null, pathProvider, propagateUpdates);
    }

    public FieldDefinition<O> out(BiFunction<O, ObjectPath, FieldInputDefinition> pathProvider) {
        return out(null, pathProvider, true);
    }

    public Field instantiate(FieldObject fo) {
        try {
            Field instance = clazz.newInstance();

            instance.setObject(fo);
            instance.setFieldDefinition(this);

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void instantiateLinks(Field f) {
        inputs.instantiateLinks(f);

        outputs.forEach(fl ->
                fl.getObjectPath().resolve(f.getObject()
                )
        );
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

    @Override
    public O getObject() {
        return object;
    }

    public FieldDefinition<O> setObject(O object) {
        this.object = object;

        return this;
    }

    public String getLabel() {
        return label;
    }

    public FieldDefinition<O> setLabel(String label) {
        this.label = label;

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
    public void addOutput(FieldLinkDefinition fl) {
        outputs.add(fl);
    }

}
