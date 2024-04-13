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

import network.aika.fields.FieldObject;

/**
 * @author Lukas Molzberger
 */
public class FieldObjectRelationDefinition<R extends FieldObject> {

    private int relId;

    private FieldObjectDefinition<R> relatedObject;

    public FieldObjectRelationDefinition(int relId, FieldObjectDefinition<R> relatedObject) {
        this.relId = relId;
        this.relatedObject = relatedObject;
    }

    public int getRelId() {
        return relId;
    }

    public FieldObjectDefinition<R> getRelatedObject() {
        return relatedObject;
    }
}
