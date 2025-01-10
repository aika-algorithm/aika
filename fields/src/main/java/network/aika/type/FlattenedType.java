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
import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.fields.direction.Direction;
import network.aika.type.relations.Relation;
import network.aika.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class FlattenedType<T extends Type<T, O>, O extends Obj<T, O>> {

    private final T type;

    private final short[] fields;
    private final FieldDefinition<T, O>[] fieldsReverse;

    private FlattenedTypeRelation<?, ?, T, O>[][] inputs; // From-Type, FD-List
    private FlattenedTypeRelation<T, O, ?, ?>[][] outputs; // To-Type, FD-List


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

    public void flatten() {
        inputs = flatten(Direction.INPUT);
        outputs = flatten(Direction.OUTPUT);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
            >
    FlattenedTypeRelation<T, O, RT, RO>[][] flatten(Direction dir) {
        FlattenedTypeRelation<T, O, RT, RO>[][] results = new FlattenedTypeRelation[type.getRelations().length][];

        for(Relation rel: type.getRelations()) {
            FlattenedTypeRelation<T, O, RT, RO>[] resultsPerRelation = new FlattenedTypeRelation[type.getTypeRegistry().getTypes().size()];
            for (Type relatedType : type.getTypeRegistry().getTypes()) {
                resultsPerRelation[relatedType.getId()] = flattenPerType(dir, rel, relatedType);
            }

            if(!ArrayUtils.isAllNull(resultsPerRelation))
                results[rel.getRelationId()] = resultsPerRelation;
        }

        return results;
    }

    private <
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
            >
    FlattenedTypeRelation<T, O, RT, RO> flattenPerType(
            Direction dir,
            Relation<T, O, RT, RO> relation,
            Type<RT, RO> relatedType
    ) {
        List<FieldLinkDefinition<T, O, ?, ?>> fieldLinks = Stream.of(fieldsReverse)
                .<FieldLinkDefinition<T, O, ?, ?>>flatMap(dir::getFieldLinkDefinitions)
                .filter(fl ->
                        fl.getRelation().getRelationId() == relation.getRelationId()
                )
                .filter(fl ->
                        relatedType.isInstanceOf(fl.getRelatedFD().getObjectType())
                )
                .filter(fl ->
                        relatedType.getFlattenedType().fields[fl.getRelatedFD().getFieldId()] >= 0
                )
                .toList();

        return fieldLinks.isEmpty() ?
                null :
                new FlattenedTypeRelation<>(type.getTypeRegistry(), fieldLinks);
    }

    public FlattenedTypeRelation<?, ?, T, O>[][] getInputs() {
        return inputs;
    }

    public FlattenedTypeRelation<T, O, ?, ?>[][] getOutputs() {
        return outputs;
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
