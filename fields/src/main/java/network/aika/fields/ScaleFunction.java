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

import network.aika.fielddefs.FieldTag;
import network.aika.fielddefs.FunctionFieldDefinition;
import network.aika.fielddefs.Type;
import network.aika.fields.link.FixedFieldLink;

/**
 * @author Lukas Molzberger
 */
public class ScaleFunction<O extends Obj> extends AbstractFunction<O> {

    public static <T extends Type<T, O>, O extends Obj<T, O>> FunctionFieldDefinition<T, O> scale(T ref, FieldTag fieldTag, double scale) {
        return new FunctionFieldDefinition(ScaleFunction.class, ref, fieldTag, scale);
    }

    private double scale;

    public ScaleFunction() {
        super(1);
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    protected double computeUpdate(FixedFieldLink fl, double u) {
        return scale * u;
    }
}
