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

import network.aika.enums.Direction;
import network.aika.fields.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Lukas Molzberger
 */
public class FieldInputsDefinition<O extends ObjectDefinition<O>> {

    private O object;

    protected List<FieldLinkDefinition> inputs = new ArrayList<>();

    public FieldInputsDefinition<O> in(Integer arg, BiFunction<O, ObjectPath, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        ObjectPath objectPath = new ObjectPath(Direction.INPUT);
        objectPath.add(new ObjectRelationDefinition(object, o -> List.of(o)));
        FieldOutputDefinition in = pathProvider.apply(object, objectPath);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, in, arg, this, propagateUpdates);
        addInput(fl);
        in.addOutput(fl);

        return this;
    }

    public FieldInputsDefinition<O> in(Integer arg, BiFunction<O, ObjectPath, FieldOutputDefinition> pathProvider) {
        return in(arg, pathProvider, true);
    }

    public void addInput(FieldLinkDefinition fl) {
        inputs.add(fl);
    }

    public O getObject() {
        return object;
    }

    public void setObject(O object) {
        this.object = object;
    }

    public void instantiateLinks(Field f) {
        inputs.forEach(fl ->
                fl.getObjectPath().resolve(f.getObject()
                )
        );
    }

    public int size() {
        return inputs.size();
    }

}
