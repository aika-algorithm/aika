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
public class FieldDefinition implements Comparable<FieldDefinition> {

    protected Integer fieldId;

    protected String name;

    protected List<FieldLinkDefinitionInputSide> outputs = new ArrayList<>();

    protected FieldDefinition parent;
    protected List<FieldDefinition> children = new ArrayList<>();

    protected Type objectType;

    protected Double tolerance;

    protected ProcessingPhase phase;
    protected boolean isNextRound;


    public FieldDefinition(Type objectType, String name) {
        this.name = name;
        this.objectType = objectType;

        objectType.setFieldDefinition(this);
    }

    public FieldDefinition(Type objectType, String name, double tolerance) {
        this(objectType, name);

        this.tolerance = tolerance;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public void transmit(Field targetField, FieldLinkDefinitionOutputSide fieldLink, double update) {
        receiveUpdate(targetField, update);
    }

    protected void receiveUpdate(Field field, double update) {
        if(!field.getObject().isInstanceOf(objectType))
            return;

        if(ToleranceUtils.belowTolerance(getTolerance(), update))
            return;

        field.receiveUpdate(update);
    }

    public FieldDefinition getParent() {
        return parent;
    }

    public FieldDefinition setParent(FieldDefinition parent) {
        this.parent = parent;
        parent.children.add(this);

        return this;
    }

    public List<FieldDefinition> getChildren() {
        return children;
    }

    public boolean isFieldRequired(Set<FieldDefinition> fieldDefs) {
        return resolveInheritedFieldDefinition(fieldDefs) == this;
    }

    public FieldDefinition resolveInheritedFieldDefinition(Set<FieldDefinition> fieldDefs) {
        return children.stream()
                .filter(fieldDefs::contains)
                .map(fd ->
                        fd.resolveInheritedFieldDefinition(fieldDefs)
                )
                .findFirst()
                .orElse(this);
    }

    public void initializeField(Field field) {
        field.getObject()
                .getType()
                .getFlattenedTypeInputSide()
                .followLinks(field);
    }

    public void addInput(FieldLinkDefinitionOutputSide fl) {
        throw new UnsupportedOperationException();
    }

    public Stream<FieldLinkDefinitionOutputSide> getInputs() {
        throw new UnsupportedOperationException();
    }

    public void addOutput(FieldLinkDefinitionInputSide fl) {
        outputs.add(fl);
    }

    public Stream<FieldLinkDefinitionInputSide> getOutputs() {
        return outputs.stream();
    }

    public FieldDefinition out(RelationOne relation, FixedArgumentsFieldDefinition output, int arg) {
        link(this, output, relation, arg);

        assert relation != null || objectType.isInstanceOf(output.objectType) || output.objectType.isInstanceOf(objectType);

        return this;
    }

    public FieldDefinition out(Relation relation, VariableArgumentsFieldDefinition output) {
        link(this, output, relation, null);

        assert relation != null || objectType.isInstanceOf(output.objectType) || output.objectType.isInstanceOf(objectType);

        return this;
    }

    public FieldDefinition setName(String name) {
        this.name = name;

        return this;
    }

    public String getName() {
        return name;
    }

    public Type getObjectType() {
        return objectType;
    }

    public Integer getId() {
        return fieldId;
    }

    public FieldDefinition setObjectType(Type objectType) {
        this.objectType = objectType;

        return this;
    }

    public Double getTolerance() {
        return tolerance;
    }

    public FieldDefinition setTolerance(Double tolerance) {
        this.tolerance = tolerance;

        return this;
    }

    public ProcessingPhase getPhase() {
        return phase;
    }

    public FieldDefinition setPhase(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }

    public boolean isNextRound() {
        return isNextRound;
    }

    public FieldDefinition setNextRound(boolean nextRound) {
        isNextRound = nextRound;

        return this;
    }

    public FieldDefinition setQueued(ProcessingPhase phase) {
        this.phase = phase;

        return this;
    }

    public String toString() {
        return getId() + ":" + name;
    }

    @Override
    public int compareTo(FieldDefinition fd) {
        return fieldId.compareTo(fd.fieldId);
    }
}
