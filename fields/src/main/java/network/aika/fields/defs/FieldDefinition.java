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
package network.aika.fields.defs;

import network.aika.fields.field.Field;
import network.aika.fields.link.ArgFieldLinkDefinition;
import network.aika.fields.link.FieldLinkDefinition;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;
import network.aika.type.Obj;
import network.aika.queue.ProcessingPhase;
import network.aika.type.Type;
import network.aika.utils.ToleranceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public class FieldDefinition<
        T extends Type<T, O>,
        O extends Obj<T, O>
        > {

    protected Integer fieldId;

    protected String name;

    protected List<FieldLinkDefinition<T, O, ?, ?>> outputs = new ArrayList<>();

    protected FieldDefinition<T, O> parent;

    protected T objectType;

    protected Double tolerance;

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(T objectType, String name) {
        this.name = name;
        this.objectType = objectType;

        objectType.setFieldDefinition(this);
    }

    public FieldDefinition(T objectType, String name, double tolerance) {
        this(objectType, name);

        this.tolerance = tolerance;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public void receiveUpdate(O toObj, FieldLinkDefinition<?, ?, T, O> fieldLink, double update) {
        receiveUpdate(toObj, update);
    }

    protected void receiveUpdate(O toObj, double update) {
        if(!toObj.isInstanceOf(objectType))
            return;

        if(ToleranceUtils.belowTolerance(getTolerance(), update))
            return;

        Field field = toObj.getOrCreateField(this);
        field.receiveUpdate(update);
    }

    public FieldDefinition<T, O> getParent() {
        return parent;
    }

    public FieldDefinition<T, O> setParent(FieldDefinition<T, O> parent) {
        this.parent = parent;

        return this;
    }

    @SuppressWarnings("unchecked")
    public <RT extends Type<RT, RO>, RO extends Obj<RT, RO>> void initializeField(O obj) {
        followLinks(
                obj,
                getObjectType().getFlattenedType().getInputs(),
                (fl, relObj) -> fl.fetchFromObject(relObj, obj)
        );
    }

    public <RT extends Type<RT, RO>, RO extends Obj<RT, RO>> void propagateUpdate(O obj, double update) {
        followLinks(
                obj,
                getObjectType().getFlattenedType().getOutputs(),
                (fl, relObj) -> fl.getOutput().receiveUpdate(relObj, fl, update)
        );
    }

    private <RT extends Type<RT, RO>, RO extends Obj<RT, RO>> void followLinks(O obj, FieldLinkDefinition[][][] a, BiConsumer<FieldLinkDefinition, RO> perFieldLink) {
        for(int relationId = 0; relationId < a.length; relationId++) {
            FieldLinkDefinition[][] b = a[relationId];

            if(b != null) {
                getObjectType().getRelations()[relationId].followAll(obj)
                        .forEach(relatedObj -> {
                                    for (FieldLinkDefinition fl : b[relatedObj.getType().getId()]) {
                                        perFieldLink.accept(fl, (RO) relatedObj);
                                    }
                                }
                        );
            }
        }
    }

    public <
            IT extends Type<IT, IO>,
            IO extends Obj<IT, IO>
            > void addInput(FieldLinkDefinition<IT, IO, T, O> fl) {
        throw new UnsupportedOperationException();
    }

    public Stream<? extends FieldLinkDefinition<?, ?, T, O>> getInputs() {
        throw new UnsupportedOperationException();
    }

    public void addOutput(FieldLinkDefinition<T, O, ?, ?> fl) {
        outputs.add(fl);
    }

    public Stream<? extends FieldLinkDefinition<T, O, ?, ?>> getOutputs() {
        return outputs.stream();
    }

    public Stream<? extends FieldLinkDefinition<T, O, ?, ?>> getAllOutputs() {
        return parent != null ?
                Stream.concat(outputs.stream(), parent.getAllOutputs()) :
                outputs.stream();
    }

    public <
            OT extends Type<OT, OO>,
            OO extends Obj<OT, OO>
            > FieldDefinition<T, O> out(RelationOne<T, O, OT, OO> relationType, FixedArgumentsFieldDefinition<OT, OO> output, int arg) {
        ArgFieldLinkDefinition<T, O, OT, OO> fl = new ArgFieldLinkDefinition<>(this, output, relationType, arg);
        output.addInput(fl);
        addOutput(fl);

        assert relationType != null || objectType.isInstanceOf(output.objectType) || output.objectType.isInstanceOf(objectType);

        return this;
    }

    public <
            OT extends Type<OT, OO>,
            OO extends Obj<OT, OO>
            > FieldDefinition<T, O> out(Relation<T, O, OT, OO> relation, VariableArgumentsFieldDefinition<OT, OO> output) {
        FieldLinkDefinition<T, O, OT, OO> fl = new FieldLinkDefinition<>(this, output, relation);
        output.addInput(fl);
        addOutput(fl);

        assert relation != null || objectType.isInstanceOf(output.objectType) || output.objectType.isInstanceOf(objectType);

        return this;
    }

    public FieldDefinition<T, O> setName(String name) {
        this.name = name;

        return this;
    }

    public String getName() {
        return name;
    }

    public T getObjectType() {
        return objectType;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public FieldDefinition<T, O> setObjectType(T objectType) {
        this.objectType = objectType;

        return this;
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
        return getFieldId() + ":" + name;
    }
}
