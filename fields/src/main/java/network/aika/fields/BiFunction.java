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

import network.aika.fields.link.FieldLink;

import java.util.function.DoubleBinaryOperator;

/**
 * @author Lukas Molzberger
 */
public class BiFunction extends AbstractFunction {

    private ReferencedBiFunction function;

    public BiFunction(FieldObject ref, String label) {
        super(ref, label);
    }

    public void setFunction(ReferencedBiFunction f) {
        this.function = f;
    }

    @Override
    protected int getNumberOfFunctionArguments() {
        return 2;
    }

    @Override
    protected double computeUpdate(FieldLink fl, double u) {
        return switch (fl.getArgument()) {
            case 0 -> function.f(
                    getReference(),
                    fl.getUpdatedInputValue(),
                    getInputValueByArg(1)
                );
            case 1 -> function.f(
                    getReference(),
                    getInputValueByArg(0),
                    fl.getUpdatedInputValue()
                );
            default -> throw new IllegalArgumentException();
        } - value;
    }
}