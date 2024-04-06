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
import network.aika.fielddefs.FieldObjectDefinition;
import network.aika.queue.QueueProvider;

/**
 * @author Lukas Molzberger
 */
public abstract class FieldObjectImpl<R extends FieldObject<R, D>, D extends FieldObjectDefinition<R>> implements FieldObject<R, D>, QueueProvider {

    private D typeDefinition;

    private Field[] fields;

    @Override
    public void setTypeDefinition(D typeDef) {
        this.fields = new Field[typeDef.getNumberOfFields()];
        this.typeDefinition = typeDef;
    }

    @Override
    public D getTypeDefinition() {
        return typeDefinition;
    }

    @Override
    public void setField(int i, Field f) {
        fields[i] = f;
    }

    @Override
    public void setField(FieldDefinition fieldDef, Field f) {
        fields[fieldDef.getFieldId()] = f;
    }

    @Override
    public Field getField(FieldDefinition fieldDef) {
        return fields[fieldDef.getFieldId()];
    }

    @Override
    public void disconnect() {
        for(int i = 0; i < fields.length; i++) {
            fields[i].disconnectAndUnlinkInputs(false);
        }
    }
}
