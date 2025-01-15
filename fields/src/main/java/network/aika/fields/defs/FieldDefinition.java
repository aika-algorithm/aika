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

import network.aika.fields.direction.Direction;
import network.aika.fields.field.Field;
import network.aika.type.FlattenedTypeRelation;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;
import network.aika.type.Obj;
import network.aika.queue.ProcessingPhase;
import network.aika.type.Type;
import network.aika.utils.ToleranceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static network.aika.fields.defs.FieldLinkDefinition.link;

/**
 * @author Lukas Molzberger
 */
public class FieldDefinition<T extends Type<T, O>, O extends Obj<T, O>> implements Comparable<FieldDefinition<T, O>> {

    protected Integer fieldId;

    protected String name;

    protected List<FieldLinkDefinitionInputSide<T, O, ?, ?>> outputs = new ArrayList<>();

    protected FieldDefinition<T, O> parent;
    protected List<FieldDefinition<T, O>> children = new ArrayList<>();

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

    public void transmit(Field<T, O> targetField, FieldLinkDefinitionOutputSide<T, O, ?, ?> fieldLink, double update) {
        receiveUpdate(targetField, update);
    }

    protected void receiveUpdate(Field<T, O> field, double update) {
        if(!field.getObject().isInstanceOf(objectType))
            return;

        if(ToleranceUtils.belowTolerance(getTolerance(), update))
            return;

        field.receiveUpdate(update);
    }

    public FieldDefinition<T, O> getParent() {
        return parent;
    }

    public FieldDefinition<T, O> setParent(FieldDefinition<T, O> parent) {
        this.parent = parent;
        parent.children.add(this);

        return this;
    }

    public List<FieldDefinition<T, O>> getChildren() {
        return children;
    }

    public boolean isFieldRequired(Set<FieldDefinition<T, O>> fieldDefs) {
        return resolveInheritedFieldDefinition(fieldDefs) == this;
    }

    public FieldDefinition<T, O> resolveInheritedFieldDefinition(Set<FieldDefinition<T, O>> fieldDefs) {
        return children.stream()
                .filter(fieldDefs::contains)
                .map(fd ->
                        fd.resolveInheritedFieldDefinition(fieldDefs)
                )
                .findFirst()
                .orElse(this);
    }

    public <RT extends Type<RT, RO>, RO extends Obj<RT, RO>> void initializeField(Field<T, O> field) {
        getObjectType()
                .getFlattenedTypeInputSide()
                .followLinks(field);
    }

    public <RT extends Type<RT, RO>, RO extends Obj<RT, RO>> void propagateUpdate(Field<T, O> field) {
        getObjectType()
                .getFlattenedTypeOutputSide()
                .followLinks(field);
    }

    public void addInput(FieldLinkDefinitionOutputSide<T, O, ?, ?> fl) {
        throw new UnsupportedOperationException();
    }

    public Stream<FieldLinkDefinitionOutputSide<T, O, ?, ?>> getInputs() {
        throw new UnsupportedOperationException();
    }

    public void addOutput(FieldLinkDefinitionInputSide<T, O, ?, ?> fl) {
        outputs.add(fl);
    }

    public Stream<FieldLinkDefinitionInputSide<T, O, ?, ?>> getOutputs() {
        return outputs.stream();
    }

    public <
            OT extends Type<OT, OO>,
            OO extends Obj<OT, OO>
            >
    FieldDefinition<T, O> out(RelationOne<T, O, OT, OO> relation, FixedArgumentsFieldDefinition<OT, OO> output, int arg) {
        link(this, output, relation, arg);

        assert relation != null || objectType.isInstanceOf(output.objectType) || output.objectType.isInstanceOf(objectType);

        return this;
    }

    public <
            OT extends Type<OT, OO>,
            OO extends Obj<OT, OO>
            >
    FieldDefinition<T, O> out(Relation<T, O, OT, OO> relation, VariableArgumentsFieldDefinition<OT, OO> output) {
        link(this, output, relation, null);

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

    @Override
    public int compareTo(FieldDefinition<T, O> fd) {
        return fieldId.compareTo(fd.fieldId);
    }
}
