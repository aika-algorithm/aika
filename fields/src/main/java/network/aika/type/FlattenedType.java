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
public class FlattenedType {

    private final Direction direction;
    private final Type type;

    private final short[] fields;
    private final FieldDefinition[][] fieldsReverse;
    private final int numberOfFields;

    private FlattenedTypeRelation[][] mapping;

    @SuppressWarnings("unchecked")
    private FlattenedType(Direction dir, Type type, Map<FieldDefinition, Short> fieldMappings, int numberOfFields) {
        this.direction = dir;
        this.type = type;
        this.numberOfFields = numberOfFields;

        fields = new short[type.getTypeRegistry().getNumberOfFieldDefinitions()];
        Arrays.fill(fields, (short) -1);

        Map<Short, List<FieldDefinition>> groupedMap = new HashMap<>();
        for (Map.Entry<FieldDefinition, Short> e : fieldMappings.entrySet()) {
            fields[e.getKey().getId()] = e.getValue();

            groupedMap.computeIfAbsent(e.getValue(), k -> new ArrayList<>())
                    .add(e.getKey());
        }

        fieldsReverse = new FieldDefinition[numberOfFields][];
        for(Map.Entry<Short, List<FieldDefinition>> e: groupedMap.entrySet()) {
            fieldsReverse[e.getKey()] = e.getValue().toArray(new FieldDefinition[0]);
        }
    }

    public static FlattenedType createInputFlattenedType(Type type, Set<FieldDefinition> fieldDefs) {
        Map<FieldDefinition, Short> fieldMappings = new TreeMap<>();

        List<FieldDefinition> requiredFields = fieldDefs
                .stream()
                .filter(fd -> fd.isFieldRequired(fieldDefs))
                .toList();

        for(short i = 0; i < requiredFields.size(); i++) {
            FieldDefinition fd = requiredFields.get(i);
            fieldMappings.put(fd, i);
        }

       return new FlattenedType(Direction.INPUT, type, fieldMappings, requiredFields.size());
    }

    public static FlattenedType createOutputFlattenedType(Type type, Set<FieldDefinition> fieldDefs, FlattenedType inputSide) {
        Map<FieldDefinition, Short> fieldMappings = new TreeMap<>();
        for(FieldDefinition fd: fieldDefs) {
            FieldDefinition resolvedFD = fd.resolveInheritedFieldDefinition(fieldDefs);
            short fieldIndex = inputSide.fields[resolvedFD.getId()];

            fieldMappings.put(fd, fieldIndex);
        }

        return new FlattenedType(Direction.OUTPUT, type, fieldMappings, inputSide.numberOfFields);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void flatten() {
        mapping = new FlattenedTypeRelation[type.getRelations().length][];

        for(Relation rel: type.getRelations()) {
            FlattenedTypeRelation[] resultsPerRelation = new FlattenedTypeRelation[type.getTypeRegistry().getTypes().size()];
            for (Type relatedType : type.getTypeRegistry().getTypes()) {
                resultsPerRelation[relatedType.getId()] = flattenPerType(rel, relatedType);
            }

            if(!ArrayUtils.isAllNull(resultsPerRelation))
                mapping[rel.getRelationId()] = resultsPerRelation;
        }
    }

    private FlattenedTypeRelation flattenPerType(
            Relation relation,
            Type relatedType
    ) {
        List<FieldLinkDefinition> fieldLinks = Stream.of(fieldsReverse)
                .flatMap(Stream::of)
                .<FieldLinkDefinition>flatMap(direction::getFieldLinkDefinitions)
                .filter(fl ->
                        fl.getRelation().getRelationId() == relation.getRelationId()
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
                new FlattenedTypeRelation(this, fieldLinks);
    }

    public void followLinks(Field field) {
        for(int relationId = 0; relationId < mapping.length; relationId++) {
            FlattenedTypeRelation[] ftr = mapping[relationId];

            if(ftr != null) {
                Relation relation = type.getRelations()[relationId];

                relation.followMany(field.getObject())
                        .forEach(relatedObj ->
                                followLinks(
                                        ftr[relatedObj.getType().getId()],
                                        relatedObj,
                                        field
                                )
                        );
            }
        }
    }

    private void followLinks(FlattenedTypeRelation ftr, Obj relatedObj, Field field) {
        if(ftr != null)
            ftr.followLinks(direction, relatedObj, field);
    }

    public FieldDefinition[][] getFieldsReverse() {
        return fieldsReverse;
    }

    public short getFieldIndex(FieldDefinition fd) {
        return fields[fd.getId()];
    }

    public short getNumberOfFields() {
        return (short) fieldsReverse.length;
    }

    public Type getType() {
        return type;
    }

    public FieldDefinition getFieldDefinitionIdByIndex(short idx) {
        return fieldsReverse[idx][0];
    }
}
