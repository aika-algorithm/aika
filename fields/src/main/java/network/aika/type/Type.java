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
import network.aika.type.relations.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static network.aika.type.FlattenedType.createInputFlattenedType;
import static network.aika.type.FlattenedType.createOutputFlattenedType;

/**
 * @author Lukas Molzberger
 */
public class Type {

    protected static final Logger LOG = LoggerFactory.getLogger(Type.class);

    public static final Comparator<Type> TYPE_COMPARATOR = Comparator.
            comparingInt(Type::getDepth)
            .thenComparing(Type::getId);

    private final short id;
    private final String name;

    protected final List<Type> parents = new ArrayList<>();
    protected final List<Type> children = new ArrayList<>();

    private final TypeRegistry registry;

    private final Set<FieldDefinition> fieldDefinitions = new HashSet<>();

    protected List<Relation> relations = new ArrayList<>();

    private Integer depth;

    private FlattenedType flattenedTypeInputSide;
    private FlattenedType flattenedTypeOutputSide;

    public Type(TypeRegistry registry, String name) {
        this.name = name;
        this.registry = registry;

        id = registry.register(this);
    }

    public short getId() {
        return id;
    }

    public boolean isAbstract() {
        return !children.isEmpty();
    }


    public Relation[] getRelations() {
        return relations.toArray(new Relation[0]);
    }

    public void initFlattenedType() {
        Set<FieldDefinition> fieldDefs = getCollectFlattenedFieldDefinitions();

        flattenedTypeInputSide = createInputFlattenedType(this, fieldDefs);
        flattenedTypeOutputSide = createOutputFlattenedType(this, fieldDefs, flattenedTypeInputSide);
    }

    public Set<FieldDefinition> getCollectFlattenedFieldDefinitions() {
        return collectTypes()
                .stream()
                .flatMap(t -> t.getFieldDefinitions().stream())
                .collect(Collectors.toSet());
    }

    public SortedSet<Type> collectTypes() {
        TreeSet<Type> sortedTypes = new TreeSet<>(TYPE_COMPARATOR);
        collectTypesRecursiveStep(sortedTypes);
        return sortedTypes;
    }

    public void collectTypesRecursiveStep(SortedSet<Type> sortedTypes) {
        parents.forEach(p ->
                p.collectTypesRecursiveStep(sortedTypes)
        );
        sortedTypes.add(this);
    }

    public Integer getDepth() {
        if(depth == null)
            depth = parents.stream()
                .mapToInt(Type::getDepth)
                .max()
                .orElse(0);

        return depth;
    }

    public boolean isInstanceOf(Obj obj) {
        return isInstanceOf(obj.getType());
    }

    public boolean isInstanceOf(Type type) {
        return this == type ||
                parents.stream().anyMatch(p ->
                        p.isInstanceOf(type)
                );
    }

    public String getName() {
        return name;
    }

    public TypeRegistry getTypeRegistry() {
        return registry;
    }

    public FlattenedType getFlattenedTypeInputSide() {
        if(flattenedTypeInputSide == null)
            throw new RuntimeException("Type has not been flattened yet. TypeRegistry.flattenTypeHierarchy() needs to be called beforehand.");

        return flattenedTypeInputSide;
    }

    public FlattenedType getFlattenedTypeOutputSide() {
        if(flattenedTypeOutputSide == null)
            throw new RuntimeException("Type has not been flattened yet. TypeRegistry.flattenTypeHierarchy() needs to be called beforehand.");

        return flattenedTypeOutputSide;
    }

    public void setFieldDefinition(FieldDefinition fieldDef) {
        fieldDef.setFieldId(registry.createFieldId());
        fieldDefinitions.add(fieldDef);
    }

    public Set<FieldDefinition> getFieldDefinitions() {
        return fieldDefinitions;
    }

    public <T extends Type> Type addParent(T p) {
        parents.add(p);
        p.children.add(this);

        return this;
    }

    public List<? extends Type> getParents() {
        return parents;
    }

    public List<? extends Type> getChildren() {
        return children;
    }

    protected <R> R getFromParent(Function<Type, R> f) {
        return parents.stream()
                .map(f)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public String toString() {
        return name;
    }
}
