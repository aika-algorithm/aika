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

import network.aika.fielddefs.Type;
import network.aika.fielddefs.link.FieldLinkDefinition;
import network.aika.fields.Field;
import network.aika.fields.Obj;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukas Molzberger
 */
public class FieldInputsDefinition<T extends Type<T, O>, O extends Obj<T, O>, F extends FieldLinkDefinition<F>> {

    protected T object;

    protected List<F> inputs = new ArrayList<>();


    public void addInput(F fl) {
        inputs.add(fl);
    }


    public void setObject(T object) {
        this.object = object;
    }

    public void instantiateLinks(Field f) {
        inputs.forEach(fl ->
                fl.getObjectPath().resolve(
                        f.getObject()
                )
        );
    }

    public int size() {
        return inputs.size();
    }

}
