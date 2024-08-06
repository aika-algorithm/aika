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
import network.aika.fielddefs.link.FixedFieldLinkDefinition;
import network.aika.fields.Obj;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Lukas Molzberger
 */
public class FixedFieldInputsDefinition<T extends Type<T, O>, O extends Obj<T, O>>  extends FieldInputsDefinition<T, O, FixedFieldLinkDefinition> {


    public FixedFieldInputsDefinition<T, O> in(Integer arg, BiFunction<T, ObjectPath, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        ObjectPath objectPath = new ObjectPath(Direction.INPUT);
        objectPath.add(new ObjectRelationDefinition("IN", field.getObjectType(), o -> List.of(o)));
        FieldOutputDefinition in = pathProvider.apply(field.getObjectType(), objectPath);

        FixedFieldLinkDefinition fl = new FixedFieldLinkDefinition(objectPath, in, arg, field, propagateUpdates);
        addInput(fl);
        in.addOutput(fl);

        return this;
    }

    public FixedFieldInputsDefinition<T, O> in(Integer arg, BiFunction<T, ObjectPath, FieldOutputDefinition> pathProvider) {
        return in(arg, pathProvider, true);
    }
}
