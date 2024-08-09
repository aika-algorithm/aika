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

import network.aika.fielddefs.*;
import network.aika.fielddefs.inputs.ArgInputs;
import network.aika.fields.link.FixedFieldLink;

import java.util.function.BiConsumer;

/**
 * @author Lukas Molzberger
 */
public class ThresholdOperator<O extends Obj> extends AbstractFunction<O> {

    public static <T extends Type<T, O>, O extends Obj<T, O>> FieldDefinition<T, O> threshold(T ref, FieldTag fieldTag, double threshold, Comparison type) {
        return new ThresholdOperatorFieldDefinition<>(ref, fieldTag, threshold, type);
    }

    public static <D extends Type<D, O>, O extends Obj<D, O>> FieldDefinition<D, O> threshold(D ref, FieldTag fieldTag, double threshold, Comparison type, boolean isFinal, BiConsumer<O, ObjectPath> pathProvider, String in) {
        return new ThresholdOperatorFieldDefinition<>(ref, fieldTag, threshold, type, isFinal);
    }

    public enum Comparison {
        ABOVE,
        BELOW,
        BELOW_OR_EQUAL,
        ABOVE_ABS
    }

    private double threshold;
    private Comparison comparison;
    private boolean isFinal = false;

    public ThresholdOperator() {
        super(1);
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    @Override
    protected double computeUpdate(FixedFieldLink fl, double u) {
        if(isFinal && value > 0.5)
            return 0.0;

        return threshold(fl.getUpdatedInputValue()) - value;
    }

    protected double threshold(double x) {
        return switch (comparison) {
            case ABOVE -> x > threshold ? 1.0 : 0.0;
            case BELOW -> x < threshold ? 1.0 : 0.0;
            case BELOW_OR_EQUAL -> x <= threshold ? 1.0 : 0.0;
            case ABOVE_ABS -> Math.abs(x) > threshold ? 1.0 : 0.0;
        };
    }
}
