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
import network.aika.fielddefs.ObjectDefinition;
import network.aika.fields.link.FieldLink;
import network.aika.fields.link.FixedInputs;

/**
 * @author Lukas Molzberger
 */
public class ScaleFunction<O extends FieldObject> extends AbstractFunction<O> {

    public static <O extends ObjectDefinition<O>> FieldDefinition<O> scale(O ref, String label, double scale) {
        return new FieldDefinition(ScaleFunction.class, ref, label, scale);
    }

    private double scale;

    public ScaleFunction() {
        super(1);
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    protected double computeUpdate(FieldLink fl, double u) {
        return scale * u;
    }
}
