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
package network.aika.elements.typedef;

import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.ObjectDefinition;
import network.aika.fielddefs.ObjectRelationDefinition;
import network.aika.fielddefs.ObjectPath;
import network.aika.fields.FieldObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class TypeDefinition<D extends TypeDefinition<D, T>, T extends Type<D, T> & FieldObject> extends ObjectDefinition<D> {

    private String name;

    protected Class<? extends T> clazz;

    protected List<TypeDefinition> parents = new ArrayList<>();

    public TypeDefinition(String name, Class<? extends T> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    protected T instantiate(List<Class<?>> parameterTypes, List<Object> parameters) {
        try {
            T instance = clazz.getConstructor(parameterTypes.toArray(new Class[0]))
                    .newInstance(parameters.toArray(new Object[0]));

            instance.setTypeDefinition((D) this);

            instantiateFields(instance);

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FieldDefinition<D> getField(String name) {
        FieldDefinition<D> fieldDef = super.getField(name);
        if(fieldDef != null)
            return fieldDef;

        return parents.stream()
                .map(p -> p.getField(name))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public boolean isInstance(T type) {
        return this == type.typeDef ||
                parents.stream().anyMatch(p ->
                        p.isInstance(type)
                );
    }

    protected void addPathEntry(ObjectPath objectPath, ObjectDefinition relatedObject, Function<T, Set<FieldObject>> mapping) {
        objectPath.add(new ObjectRelationDefinition(relatedObject, mapping));
    }

    public String getName() {
        return name;
    }

    public Class<? extends T> getClazz() {
        return clazz;
    }

    public D addParent(TypeDefinition p) {
        parents.add(p);

        return (D) this;
    }

    public List<TypeDefinition> getParents() {
        return parents;
    }
}
