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

import network.aika.fields.Field;
import network.aika.fields.Obj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

import static network.aika.fielddefs.FieldTag.FIELD_TAG_COMPARATOR;

/**
 * @author Lukas Molzberger
 */
public abstract class Type<T extends Type<T, O>, O extends Obj<T, O>> {

    protected static final Logger LOG = LoggerFactory.getLogger(Type.class);

    private String name;

    protected Class<? extends O> clazz;

    protected List<T> parents = new ArrayList<>();

    Map<FieldTag, FieldDefinition<T, O>> fieldDefinitions = new TreeMap<>(FIELD_TAG_COMPARATOR);
    Map<FieldTag, FieldOutputDefinition> fieldOutputDefinitions = new TreeMap<>(FIELD_TAG_COMPARATOR);

    public Type(TypeRegistry registry, String name, Class<? extends O> clazz) {
        this.name = name;
        this.clazz = clazz;
        registry.register(this);
    }

    protected O instantiate(List<Class<?>> parameterTypes, List<Object> parameters) {
        try {
            O instance = clazz.getConstructor(parameterTypes.toArray(new Class[0]))
                    .newInstance(parameters.toArray(new Object[0]));

            instantiateFields(instance);

//            LOG.info(instance.toString());
            System.out.println(instance);

            instance.getFields()
                    .forEach(f -> {
                                System.out.println("  " + f);
                                f.getInputs().getInputs()
                                        .forEach(fl ->
                                                System.out.println("    " + fl.toString())
                                        );
                            }
                    );

            System.out.println();

            return instance;
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public FieldDefinition<T, O> getField(FieldTag fieldTag) {
        FieldDefinition<T, O> fieldDef = fieldDefinitions.get(fieldTag);
        if(fieldDef != null)
            return fieldDef;

        return parents.stream()
                .map(p -> p.getField(fieldTag))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public void collectFieldDefinitions(Map<FieldTag, FieldDefinition<T, O>> results) {
        parents.forEach(p ->
                p.collectFieldDefinitions(results)
        );

        results.putAll(fieldDefinitions);
    }

    public void collectFieldOutputDefinitions(Map<FieldTag, FieldOutputDefinition> results) {
        parents.forEach(p ->
                p.collectFieldOutputDefinitions(results)
        );

        results.putAll(fieldOutputDefinitions);
    }

    public boolean isInstance(O type) {
        return this == type.getType() ||
                parents.stream().anyMatch(p ->
                        p.isInstance(type)
                );
    }

    protected void addPathEntry(ObjectPath objectPath, String relLabel, Type relatedObject, Function<O, Set<Obj>> mapping) {
        objectPath.add(new ObjectRelationDefinition(relLabel, relatedObject, mapping));
    }

    public String getName() {
        return name;
    }

    public Class<? extends O> getClazz() {
        return clazz;
    }

    public T addParent(T p) {
        parents.add(p);

        return (T) this;
    }

    public List<T> getParents() {
        return parents;
    }

    public void instantiateFields(O o) {
        Map<FieldTag, FieldDefinition<T, O>> fieldDefs = new HashMap<>();
        collectFieldDefinitions(fieldDefs);

        TreeMap<FieldTag, Field> fields = new TreeMap<>();
                fieldDefs.values().stream()
                .map(fd ->
                        fd.instantiate(o)
                )
                .forEach(f ->
                        fields.put(f.getFieldDefinition().getFieldTag(), f)
                );

        o.setFields(fields);

        Map<FieldTag, FieldOutputDefinition> fieldOutputDefs = new HashMap<>();
        collectFieldOutputDefinitions(fieldOutputDefs);

        fields.values().forEach(f -> {
            FieldDefinition fd = f.getFieldDefinition();
            fd.instantiateInputLinks(f);
            FieldOutputDefinition fod = fieldOutputDefs.get(fd.getFieldTag());
            if(fod != null)
                fod.instantiateOutputLinks(f);
        });
    }

    public void setFieldDefinition(FieldDefinition<T, O> fieldDef) {
        fieldDefinitions.put(fieldDef.getFieldTag(), fieldDef);
    }

    public FieldOutputDefinition getFieldOutput(FieldTag fieldTag) {
        return fieldOutputDefinitions.computeIfAbsent(fieldTag, k -> new FieldOutputDefinition(k));
    }

    public void setFieldOutputDefinition(FieldTag fieldTag, FieldOutputDefinition fieldOutDef) {
        fieldOutputDefinitions.put(fieldTag, fieldOutDef);
    }

    public String toKeyString() {
        return getClazz().getSimpleName() + "." + getName();
    }

    public abstract void dumpType(StringBuilder sb);

    public void dumpFields(StringBuilder sb) {
        fieldDefinitions.values()
                .forEach(fd ->
                                sb.append("  " + fd.fieldTag)
                        );
    }
}
