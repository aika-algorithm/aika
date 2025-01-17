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
import network.aika.fields.field.Field;
import network.aika.type.relations.Relation;
import network.aika.utils.ArrayUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class FlattenedType<
        T extends Type<T, O>,
        O extends Obj<T, O>,
        RT extends Type<RT, RO>,
        RO extends Obj<RT, RO>
        > {

    private final Direction direction;
    private final T type;

    private final short[] fields;
    private final FieldDefinition<T, O>[][] fieldsReverse;
    private final int numberOfFields;

    private FlattenedTypeRelation<T, O, RT, RO>[][] mapping;

    @SuppressWarnings("unchecked")
    private FlattenedType(Direction dir, T type, Map<FieldDefinition<T, O>, Short> fieldMappings, int numberOfFields) {
        this.direction = dir;
        this.type = type;
        this.numberOfFields = numberOfFields;

        fields = new short[type.getTypeRegistry().getNumberOfFieldDefinitions()];
        Arrays.fill(fields, (short) -1);

        Map<Short, List<FieldDefinition<T, O>>> groupedMap = new HashMap<>();
        for (Map.Entry<FieldDefinition<T, O>, Short> e : fieldMappings.entrySet()) {
            fields[e.getKey().getId()] = e.getValue();

            groupedMap.computeIfAbsent(e.getValue(), k -> new ArrayList<>())
                    .add(e.getKey());
        }

        fieldsReverse = new FieldDefinition[numberOfFields][];
        for(Map.Entry<Short, List<FieldDefinition<T, O>>> e: groupedMap.entrySet()) {
            fieldsReverse[e.getKey()] = e.getValue().toArray(new FieldDefinition[0]);
        }
    }

    public static <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
            >
    FlattenedType<T, O, RT, RO> createInputFlattenedType(T type, Set<FieldDefinition<T, O>> fieldDefs) {
        Map<FieldDefinition<T, O>, Short> fieldMappings = new TreeMap<>();

        List<FieldDefinition<T, O>> requiredFields = fieldDefs
                .stream()
                .filter(fd -> fd.isFieldRequired(fieldDefs))
                .toList();

        for(short i = 0; i < requiredFields.size(); i++) {
            FieldDefinition<T, O> fd = requiredFields.get(i);
            fieldMappings.put(fd, i);
        }

       return new FlattenedType<>(Direction.INPUT, type, fieldMappings, requiredFields.size());
    }

    public static <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
            >
    FlattenedType<T, O, RT, RO> createOutputFlattenedType(T type, Set<FieldDefinition<T, O>> fieldDefs, FlattenedType<T, O, RT, RO> inputSide) {
        Map<FieldDefinition<T, O>, Short> fieldMappings = new TreeMap<>();
        for(FieldDefinition<T, O> fd: fieldDefs) {
            FieldDefinition<T, O> resolvedFD = fd.resolveInheritedFieldDefinition(fieldDefs);
            short fieldIndex = inputSide.fields[resolvedFD.getId()];

            fieldMappings.put(fd, fieldIndex);
        }

        return new FlattenedType<>(Direction.OUTPUT, type, fieldMappings, inputSide.numberOfFields);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void flatten() {
        mapping = new FlattenedTypeRelation[type.getRelations().length][];

        for(Relation rel: type.getRelations()) {
            FlattenedTypeRelation<T, O, RT, RO>[] resultsPerRelation = new FlattenedTypeRelation[type.getTypeRegistry().getTypes().size()];
            for (Type relatedType : type.getTypeRegistry().getTypes()) {
                resultsPerRelation[relatedType.getId()] = flattenPerType(rel, relatedType);
            }

            if(!ArrayUtils.isAllNull(resultsPerRelation))
                mapping[rel.getRelationId()] = resultsPerRelation;
        }
    }

    private FlattenedTypeRelation<T, O, RT, RO> flattenPerType(
            Relation<T, O, RT, RO> relation,
            Type<RT, RO> relatedType
    ) {
        List<FieldLinkDefinition<T, O, ?, ?>> fieldLinks = Stream.of(fieldsReverse)
                .flatMap(Stream::of)
                .<FieldLinkDefinition<T, O, ?, ?>>flatMap(direction::getFieldLinkDefinitions)
                .filter(fl ->
                        direction.getRelation(fl.getRelation()).getRelationId() == relation.getRelationId()
                )
                .filter(fl ->
                        relatedType.isInstanceOf(fl.getRelatedFD().getObjectType())
                )
                .filter(fl ->
                        direction.invert().getFlattenedType(relatedType).fields[fl.getRelatedFD().getId()] >= 0
                )
                .toList();

        return fieldLinks.isEmpty() ?
                null :
                new FlattenedTypeRelation<>(this, fieldLinks);
    }

    @SuppressWarnings("unchecked")
    public void followLinks(Field<T, O> field) {
        for(int relationId = 0; relationId < mapping.length; relationId++) {
            FlattenedTypeRelation<T, O, RT, RO>[] ftr = mapping[relationId];

            if(ftr != null) {
                Relation<T, O, RT, RO> relation = (Relation<T, O, RT, RO>) type.getRelations()[relationId];

                relation.followAll(field.getObject())
                        .forEach(relatedObj ->
                                ftr[relatedObj.getType().getId()]
                                        .followLinks(direction, relatedObj, field)
                        );
            }
        }
    }

    public FieldDefinition<T, O>[][] getFieldsReverse() {
        return fieldsReverse;
    }

    public short getFieldIndex(FieldDefinition<T, O> fd) {
        return fields[fd.getId()];
    }

    public short getNumberOfFields() {
        return (short) fieldsReverse.length;
    }

    public T getType() {
        return type;
    }

    public FieldDefinition<T, O> getFieldDefinitionIdByIndex(short idx) {
        return fieldsReverse[idx][0];
    }
}
