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
public abstract class FieldDefinition<O extends ObjectDefinition<O>, FD extends FieldDefinition<O, FD, FL>, FL extends FieldLinkDefinition<FL>> implements FieldInputDefinition<O, FL>, FieldOutputDefinition {

    protected int fieldId;

    protected Class<? extends Field> clazz;

    protected O object;

    protected String label;

    protected Double tolerance;

    protected FieldInputsDefinition<O, FL> inputs;
    protected List<FieldLinkDefinition> outputs = new ArrayList<>();

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<O, FL> inputs, O object, String label) {
        this.clazz = clazz;
        this.label = label;
        this.object = object;
        this.inputs = inputs;

        object.addFieldDefinition(this);
    }

    public FieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<O, FL> inputs, O object, String name, double tolerance) {
        this(clazz, inputs, object, name);

        this.tolerance = tolerance;
    }

    @Override
    public FieldInputsDefinition getInputs() {
        return inputs;
    }

    public FD out(Integer arg, BiFunction<O, ObjectPath, FieldInputDefinition> pathProvider, boolean propagateUpdates) {
        ObjectPath objectPath = new ObjectPath(Direction.OUTPUT);
        objectPath.add(new ObjectRelationDefinition(object, o -> List.of(o)));
        FieldInputDefinition out = pathProvider.apply(object, objectPath);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, this, out.getInputs(), propagateUpdates);
        out.getInputs().addInput(fl);
        addOutput(fl);

        return (FD) this;
    }

    public FD out(BiFunction<O, ObjectPath, FieldInputDefinition> pathProvider, boolean propagateUpdates) {
        return out(null, pathProvider, propagateUpdates);
    }

    public FD out(BiFunction<O, ObjectPath, FieldInputDefinition> pathProvider) {
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

    public FD setFieldId(int id) {
        fieldId = id;

        return (FD) this;
    }

    public int getFieldId() {
        return fieldId;
    }

    public Class<? extends Field> getClazz() {
        return clazz;
    }

    public FD setClazz(Class<? extends Field> clazz) {
        this.clazz = clazz;

        return (FD) this;
    }

    @Override
    public O getObject() {
        return object;
    }

    public FD setObject(O object) {
        this.object = object;

        return (FD) this;
    }

    public String getLabel() {
        return label;
    }

    public FD setLabel(String label) {
        this.label = label;

        return (FD) this;
    }

    public Double getTolerance() {
        return tolerance;
    }

    public FD setTolerance(Double tolerance) {
        this.tolerance = tolerance;

        return (FD) this;
    }

    public ProcessingPhase getPhase() {
        return phase;
    }

    public FD setPhase(ProcessingPhase phase) {
        this.phase = phase;

        return (FD) this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    public FD setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return (FD) this;
    }

    public FD setQueued(ProcessingPhase phase) {
        this.phase = phase;

        return (FD) this;
    }

    @Override
    public void addOutput(FieldLinkDefinition fl) {
        outputs.add(fl);
    }

}
