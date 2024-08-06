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
package network.aika.fielddefs.inputs;

import network.aika.enums.Direction;
import network.aika.fielddefs.*;
import network.aika.fielddefs.link.VariableFieldLinkDefinition;
import network.aika.fields.Obj;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Lukas Molzberger
 */
public class VariableFieldInputsDefinition<T extends Type<T, O>, O extends Obj<T, O>> extends FieldInputsDefinition<T, O, VariableFieldLinkDefinition> {


    public VariableFieldInputsDefinition<T, O> in(BiFunction<T, ObjectPath, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        ObjectPath objectPath = new ObjectPath(Direction.INPUT);
        objectPath.add(new ObjectRelationDefinition("IN", field.getObject(), o -> List.of(o)));
        FieldOutputDefinition in = pathProvider.apply(field.getObject(), objectPath);

        VariableFieldLinkDefinition fl = new VariableFieldLinkDefinition(objectPath, in, field, propagateUpdates);
        addInput(fl);
        in.addOutput(fl);

        return this;
    }

    public VariableFieldInputsDefinition<T, O> in(BiFunction<T, ObjectPath, FieldOutputDefinition> pathProvider) {
        return in(pathProvider, true);
    }
}
