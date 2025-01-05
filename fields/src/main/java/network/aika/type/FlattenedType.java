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
package network.aika.type;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.link.FieldLinkDefinition;
import network.aika.type.relations.Relation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

/**
 *
 * @author Lukas Molzberger
 */
public class FlattenedType<T extends Type<T, O>, O extends Obj<T, O>> {

    private final T type;

    private final short[] fields;
    private final FieldDefinition<T, O>[] fieldsReverse;

    private FieldLinkDefinition<?, ?, T, O>[][][] inputs; // From-Type, FD-List
    private FieldLinkDefinition<T, O, ?, ?>[][][] outputs; // To-Type, FD-List


    @SuppressWarnings("unchecked")
    public FlattenedType(T type) {
        this.type = type;

        fields = new short[type.getTypeRegistry().getNumberOfFields()];
        Arrays.fill(fields, (short) -1);

        short numberOfFields = 0;
        ArrayList<FieldDefinition<T, O>> fieldsRev = new ArrayList<>();
        SortedSet<Type<T, O>> sortedTypes = type.collectTypes();
        for (Type<T, O> t : sortedTypes) {
            for (FieldDefinition<T, O> fd : t.getFieldDefinitions()) {
                fields[fd.getFieldId()] = numberOfFields++;
                fieldsRev.add(fd);
            }
        }

        fieldsReverse = fieldsRev.toArray(new FieldDefinition[0]);
    }

    public void flattenFieldLinks() {
        inputs = flattenInputs();
        outputs = flattenOutputs();
    }

    @SuppressWarnings("unchecked")
    private FieldLinkDefinition<?, ?, T, O>[][][] flattenInputs() {
        FieldLinkDefinition<?, ?, T, O>[][][] results = new FieldLinkDefinition[type.getRelationTypes().length][][];

        for(Relation<T, O, ?, ?> rel: type.getRelationTypes()) {
            FieldLinkDefinition<?, ?, T, O>[][] resultsPerRelation = new FieldLinkDefinition[type.getTypeRegistry().getTypes().size()][];
            for (Type<?, ?> relType : type.getTypeRegistry().getTypes()) {
                resultsPerRelation[relType.getId()] = flattenInputsPerType(relType);
            }

            results[rel.getRelationId()] = resultsPerRelation;
        };

        return results;
    }

    public FieldLinkDefinition<?, ?, T, O>[][][] getInputs() {
        return inputs;
    }

    @SuppressWarnings("unchecked")
    private FieldLinkDefinition<?, ?, T, O>[] flattenInputsPerType(Type<?, ?> relType) {
        List<FieldLinkDefinition<?, ?, T, O>> results = new ArrayList<>();
        for (FieldDefinition<T, O> fd : fieldsReverse) {
            fd.getInputs()
                    .filter(fl ->
                            relType.isInstanceOf(fl.getInput().getObjectType())
                    )
                    .filter(fl ->
                            relType.getFlattenedType().fields[fl.getInput().getFieldId()] >= 0
                    )
                    .forEach(results::add);
        }

        return results.toArray(new FieldLinkDefinition[0]);
    }

    @SuppressWarnings("unchecked")
    private FieldLinkDefinition<T, O, ?, ?>[][][] flattenOutputs() {
        FieldLinkDefinition<T, O, ?, ?>[][][] results = new FieldLinkDefinition[type.getRelationTypes().length][][];

        for(Relation<T, O, ?, ?> rel: type.getRelationTypes()) {
            FieldLinkDefinition<T, O, ?, ?>[][] resultsPerRelation = new FieldLinkDefinition[type.getTypeRegistry().getTypes().size()][];
            for (Type<?, ?> relType : type.getTypeRegistry().getTypes()) {
                resultsPerRelation[relType.getId()] = flattenOutputsPerType(relType);
            }

            results[rel.getRelationId()] = resultsPerRelation;
        };

        return results;
    }

    public FieldLinkDefinition<T, O, ?, ?>[][][] getOutputs() {
        return outputs;
    }

    @SuppressWarnings("unchecked")
    private FieldLinkDefinition<T, O, ?, ?>[] flattenOutputsPerType(Type<?, ?> relType) {
        List<FieldLinkDefinition<T, O, ?, ?>> results = new ArrayList<>();
        for (FieldDefinition<T, O> fd : fieldsReverse) {
            fd.getAllOutputs()
                    .filter(fl ->
                            relType.isInstanceOf(fl.getOutput().getObjectType())
                    )
                    .filter(fl ->
                            relType.getFlattenedType().fields[fl.getOutput().getFieldId()] >= 0
                    )
                    .forEach(results::add);
        }

        return results.toArray(new FieldLinkDefinition[0]);
    }

    public short getFieldIndex(FieldDefinition<T, O> fd) {
        return fields[fd.getFieldId()];
    }

    public short getNumberOfFields() {
        return (short) fieldsReverse.length;
    }

    public T getType() {
        return type;
    }

    public FieldDefinition<T, O> getFieldDefinitionIdByIndex(short idx) {
        return fieldsReverse[idx];
    }
}
