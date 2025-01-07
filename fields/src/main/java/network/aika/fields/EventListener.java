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
package network.aika.fields;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.defs.FixedArgumentsFieldDefinition;
import network.aika.fields.field.Field;
import network.aika.type.Obj;
import network.aika.type.Type;

import java.util.function.BiConsumer;

/**
 * @author Lukas Molzberger
 */
public class EventListener<
        T extends Type<T, O>,
        O extends Obj<T, O>
        > extends FixedArgumentsFieldDefinition<T, O> {

    private BiConsumer<FieldDefinition<T, O>, O> triggerFunction;

    public static <
            T extends Type<T, O>,
            O extends Obj<T, O>
            > EventListener<T, O> eventListener(T ref, String name, BiConsumer<FieldDefinition<T, O>, O> triggerFunction, Double tolerance) {
        return new EventListener<>(
                ref,
                name,
                triggerFunction,
                tolerance
        );
    }

    public EventListener(T ref, String name, BiConsumer<FieldDefinition<T, O>, O> triggerFunction, Double tolerance) {
        super(ref, name, 1, tolerance);

        this.triggerFunction = triggerFunction;
    }

    @Override
    public void receiveUpdate(Field<T, O> field, double u) {
        super.receiveUpdate(field, u);
       // Activation act = (Activation) obj;

        //Field field = obj.getFieldOrCreate(this);
        //act.updateFiredStep(field);

        triggerFunction.accept(this, field.getObject());
    }
}
