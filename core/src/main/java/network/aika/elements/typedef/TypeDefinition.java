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
import network.aika.fielddefs.FieldObjectDefinition;
import network.aika.fields.FieldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class TypeDefinition<D extends TypeDefinition<D, T>, T extends Type<D, T> & FieldObject> extends FieldObjectDefinition {

    private String name;

    protected Class<? extends T> clazz;

    protected List<TypeDefinition> parents = new ArrayList<>();

    public TypeDefinition(String name, Class<? extends T> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public FieldDefinition<?> getFieldDef(String name) {
        FieldDefinition<?> fieldDef = getFieldDef(name);
        if(fieldDef != null)
            return fieldDef;

        return parents.stream()
                .map(p -> p.getFieldDef(name))
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
