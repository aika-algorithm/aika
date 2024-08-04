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


import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.Type;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;

import java.util.List;

/**
 * @author Lukas Molzberger
 */
public class ObjImpl<T extends Type<T, O>, O extends Obj<T, O>> implements Obj<T, O>, QueueProvider {

    private T type;

    private Field[] fields;


    public Field getField(String fieldName) {
        return getField(
                getType()
                        .getField(fieldName)
        );
    }

    @Override
    public void setType(T type) {
        this.type = type;
    }

    @Override
    public T getType() {
        return type;
    }

    @Override
    public void setFields(List<Field> fields) {
        this.fields = fields.toArray(new Field[0]);
    }

    @Override
    public Field getField(FieldDefinition<T, O> fieldDef) {
        return fields[fieldDef.getFieldId()];
    }

    @Override
    public void disconnect() {
        for(int i = 0; i < fields.length; i++) {
            fields[i].getInputs().disconnectAndUnlinkInputs(false);
        }
    }

    @Override
    public Queue getQueue() {
        return null;
    }

    @Override
    public boolean isNextRound() {
        return false;
    }
}
