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
import network.aika.fielddefs.inputs.FixedFieldInputsDefinition;
import network.aika.fields.link.FixedFieldLink;
import network.aika.fields.link.FixedFieldInputs;

/**
 * @author Lukas Molzberger
 */
public class MixFunction<O extends FieldObject> extends AbstractFunction<O> {

    public static <D extends ObjectDefinition<D, O>, O extends FieldObject<D, O>> FieldDefinition<D, O> mix(D ref, String label) {
        return new FieldDefinition<>(MixFunction.class, new FixedFieldInputsDefinition<>(), ref, label);
    }

    public MixFunction() {
        super(3);
    }

    @Override
    protected double computeUpdate(FixedFieldLink fl, double u) {
        FixedFieldInputs in = getInputs();
        int arg = fl.getArgument();
        if(arg == 0) {
            double a = in.getInputValueByArg(1);
            double b = in.getInputValueByArg(2);

            return (-u * a + u * b);
        } else {
            double x = in.getInputValueByArg(0);

            return (arg == 2 ? x : 1 - x) * u;
        }
    }
}
