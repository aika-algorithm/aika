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

import network.aika.elements.activations.State;
import network.aika.elements.typedef.StateDefinition;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.link.FieldLink;

/**
 * @author Lukas Molzberger
 */
public class FieldActivationFunction extends AbstractFunction<State> {

    public static FieldDefinition<StateDefinition> actFunc(StateDefinition ref, String label, Double tolerance) {
        return new FieldDefinition<>(FieldActivationFunction.class, ref, label, tolerance);
    }

    public FieldActivationFunction(State ref, String label, Double tolerance) {
        super(ref, label, tolerance);
    }

    @Override
    protected double computeUpdate(FieldLink fl, double u) {
        return getReference().getActivation().getActivationFunction().f(fl.getUpdatedInputValue()) - value;
    }
}
