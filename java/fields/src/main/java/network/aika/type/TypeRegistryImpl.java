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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static network.aika.type.Type.TYPE_COMPARATOR;

/**
 *
 * @author Lukas Molzberger
 */
public class TypeRegistryImpl implements TypeRegistry {

    private List<Type> types = new ArrayList<>();

    private int fieldIdCounter = 0;

    @Override
    public short register(Type type) {
        short id = (short) types.size();
        types.add(type);
        return id;
    }

    @Override
    public Type getType(short typeId) {
        return types.get(typeId);
    }

    @Override
    public List<Type> getTypes() {
        return types;
    }

    @Override
    public int createFieldId() {
        return fieldIdCounter++;
    }

    @Override
    public int getNumberOfFieldDefinitions() {
        return fieldIdCounter;
    }

    @Override
    public void flattenTypeHierarchy() {
        TreeSet<Type> sortedTypes = new TreeSet<>(TYPE_COMPARATOR);

        sortedTypes.addAll(types);

        sortedTypes
                .forEach(Type::initFlattenedType);

        sortedTypes
                .forEach(t -> {
                    t.getFlattenedTypeInputSide().flatten();
                    t.getFlattenedTypeOutputSide().flatten();
                });
    }
}
