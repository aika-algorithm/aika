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
public class FieldDefinition<D extends ObjectDefinition<D, O>, O extends FieldObject<D, O>> implements FieldInputDefinition<D, O> {

    protected int fieldId;

    protected Class<? extends Field> clazz;

    protected D object;

    protected String label;

    protected Double tolerance;

    protected FieldInputsDefinition<D, O, ?> inputs;
    protected List<FieldLinkDefinition> outputs = new ArrayList<>();

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<D, O, ?> inputs, D object, String label) {
        this.clazz = clazz;
        this.label = label;
        this.object = object;
        this.inputs = inputs;

        inputs.setObject(object);
        object.setFieldDefinition(this);
    }

    public FieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<D, O, ?> inputs, D object, String label, double tolerance) {
        this(clazz, inputs, object, label);

        this.tolerance = tolerance;
    }

    @Override
    public FieldInputsDefinition getInputs() {
        return inputs;
    }

    public FieldDefinition<D, O> out(BiFunction<D, ObjectPath, FieldInputDefinition> pathProvider, boolean propagateUpdates) {
        ObjectPath objectPath = new ObjectPath(Direction.OUTPUT);
        objectPath.add(new ObjectRelationDefinition(object, o -> List.of(o)));

        FieldOutputDefinition in = object.getFieldOutput(getLabel());
        FieldInputDefinition out = pathProvider.apply(object, objectPath);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, in, out.getInputs(), propagateUpdates);
        out.getInputs().addInput(fl);
        in.addOutput(fl);

        return this;
    }

    public FieldDefinition<D, O> out(BiFunction<D, ObjectPath, FieldInputDefinition> pathProvider) {
        return out(pathProvider, true);
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
                fl.getObjectPath().resolve(f.getObject())
        );
    }

    public FieldDefinition<D, O> setFieldId(int id) {
        fieldId = id;

        return this;
    }

    public int getFieldId() {
        return fieldId;
    }

    public Class<? extends Field> getClazz() {
        return clazz;
    }

    public FieldDefinition<D, O> setClazz(Class<? extends Field> clazz) {
        this.clazz = clazz;

        return this;
    }

    @Override
    public D getObject() {
        return object;
    }

    public FieldDefinition<D, O> setObject(D object) {
        this.object = object;

        return this;
    }

    public String getLabel() {
        return label;
    }

    public FieldDefinition<D, O> setLabel(String label) {
        this.label = label;

        return this;
    }

    public FieldOutputDefinition getFieldOutput() {
        return getObject().getFieldOutput(getLabel());
    }

    public Double getTolerance() {
        return tolerance;
    }

    public FieldDefinition<D, O> setTolerance(Double tolerance) {
        this.tolerance = tolerance;

        return this;
    }

    public ProcessingPhase getPhase() {
        return phase;
    }

    public FieldDefinition<D, O> setPhase(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    public FieldDefinition<D, O> setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return this;
    }

    public FieldDefinition<D, O> setQueued(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }
}
