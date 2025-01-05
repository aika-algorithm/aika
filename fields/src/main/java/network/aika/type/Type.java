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
import network.aika.fields.field.Field;
import network.aika.type.relations.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

/**
 * @author Lukas Molzberger
 */
public abstract class Type<T extends Type<T, O>, O extends Obj<T, O>> {

    protected static final Logger LOG = LoggerFactory.getLogger(Type.class);

    public static final Comparator<Type<?, ?>> TYPE_COMPARATOR = Comparator.
            <Type<?, ?>>comparingInt(Type::getDepth)
            .thenComparing(Type::getId);

    private final short id;
    private final String name;

    private Class<? extends O> clazz;

    protected final List<T> parents = new ArrayList<>();
    protected final List<T> children = new ArrayList<>();

    private final TypeRegistry registry;

    private final Set<FieldDefinition<T, O>> fieldDefinitions = new HashSet<>();

    private Integer depth;

    private FlattenedType<T, O> flattenedType;

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

    public abstract Relation<T, O, ?, ?>[] getRelations();

    public void initFlattenedType() {
        flattenedType = new FlattenedType<>((T)this);
    }

    public SortedSet<Type<T, O>> collectTypes() {
        TreeSet<Type<T, O>> sortedTypes = new TreeSet<>(TYPE_COMPARATOR);
        collectTypesRecursiveStep(sortedTypes);
        return sortedTypes;
    }

    public void collectTypesRecursiveStep(SortedSet<Type<T, O>> sortedTypes) {
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

    protected O instantiate(List<Class<?>> parameterTypes, List<Object> parameters) {
        if(isAbstract())
            throw new RuntimeException("Unable to instantiate abstract type " + name);

        try {
            O instance = getClazz().getConstructor(parameterTypes.toArray(new Class[0]))
                    .newInstance(parameters.toArray(new Object[0]));

            if(LOG.isDebugEnabled()) {
                LOG.debug(instance.toString());
            }

            instance.setFields(new Field[flattenedType.getNumberOfFields()]);

            return instance;
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public T setClazz(Class<? extends O> clazz) {
        this.clazz = clazz;
        return (T) this;
    }

    public Class<? extends O> getClazz() {
        return clazz != null ?
                clazz :
                getFromParent(Type::getClazz);
    }

    @SuppressWarnings("rawtypes")
    public boolean isInstanceOf(Obj obj) {
        return isInstanceOf(obj.getType());
    }

    @SuppressWarnings("rawtypes")
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

    public FlattenedType<T, O> getFlattenedType() {
        return flattenedType;
    }

    public void setFieldDefinition(FieldDefinition<T, O> fieldDef) {
        fieldDef.setFieldId(registry.createFieldId());
        fieldDefinitions.add(fieldDef);
    }

    public Set<FieldDefinition<T, O>> getFieldDefinitions() {
        HashSet<FieldDefinition<T, O>> results = new HashSet<>();
        collectFieldDefinitions(results);
        return results;
    }

    protected void collectFieldDefinitions(Set<FieldDefinition<T, O>> results) {
        results.addAll(fieldDefinitions);
        parents.forEach(p ->
                p.collectFieldDefinitions(results)
        );
    }

    @SuppressWarnings("unchecked")
    public T addParent(T p) {
        parents.add(p);
        p.children.add((T) this);

        return (T) this;
    }

    public List<T> getParents() {
        return parents;
    }

    public List<T> getChildren() {
        return children;
    }

    protected <X> X getFromParent(Function<T, X> f) {
        return parents.stream()
                .map(f::apply)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public String toString() {
        return name;
    }
}
