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

import network.aika.fields.Obj;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

/**
 * @author Lukas Molzberger
 */
public class Type<D extends Type<D, O>, O extends Obj<D, O>> {

    private String name;

    protected Class<? extends O> clazz;

    protected List<D> parents = new ArrayList<>();


    Map<String, FieldDefinition<D, O>> fieldDefinitions = new TreeMap<>();
    Map<String, FieldOutputDefinition> fieldOutputDefinitions = new TreeMap<>();

    public Type(String name, Class<? extends O> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    protected O instantiate(List<Class<?>> parameterTypes, List<Object> parameters) {
        try {
            O instance = clazz.getConstructor(parameterTypes.toArray(new Class[0]))
                    .newInstance(parameters.toArray(new Object[0]));

            instance.setType((D) this);

            instantiateFields(instance);

            return instance;
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public FieldDefinition<D, O> getField(String name) {
        FieldDefinition<D, O> fieldDef = fieldDefinitions.get(name);
        if(fieldDef != null)
            return fieldDef;

        return parents.stream()
                .map(p -> p.getField(name))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public boolean isInstance(O type) {
        return this == type.getType() ||
                parents.stream().anyMatch(p ->
                        p.isInstance(type)
                );
    }

    protected void addPathEntry(ObjectPath objectPath, Type relatedObject, Function<O, Set<Obj>> mapping) {
        objectPath.add(new ObjectRelationDefinition(relatedObject, mapping));
    }

    public String getName() {
        return name;
    }

    public Class<? extends O> getClazz() {
        return clazz;
    }

    public D addParent(D p) {
        parents.add(p);

        return (D) this;
    }

    public List<D> getParents() {
        return parents;
    }

    public void instantiateFields(Obj o) {
        fieldDefinitions.values().stream()
                .map(fd ->
                        fd.instantiate(o)
                )
                .toList().stream()
                .forEach(f ->
                        f.getFieldDefinition().instantiateLinks(f)
                );
    }

    public void setFieldDefinition(FieldDefinition<D, O> fieldDef) {
        fieldDef.setFieldId(fieldDefinitions.size());
        fieldDefinitions.put(fieldDef.getLabel(), fieldDef);
    }

    public FieldOutputDefinition getFieldOutput(String label) {
        return fieldOutputDefinitions.computeIfAbsent(label, k -> new FieldOutputDefinition());
    }

    public void setFieldOutputDefinition(String label, FieldOutputDefinition fieldOutDef) {
        fieldOutputDefinitions.put(label, fieldOutDef);
    }

    public int getNumberOfFields() {
        return fieldDefinitions.size();
    }

}
