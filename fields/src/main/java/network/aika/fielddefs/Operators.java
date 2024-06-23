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

import network.aika.fields.*;
import network.aika.utils.ToleranceUtils;

import java.util.function.BiConsumer;

import static network.aika.utils.ToleranceUtils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class Operators {

    public static <O extends FieldObjectDefinition> FieldDefinition<O, IdentityFunction> identity(O ref, String label) {
        return new FieldDefinition<>(IdentityFunction.class, ref, label);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, SumField> sum(O ref, String label) {
        return new FieldDefinition<>(SumField.class, ref, label, TOLERANCE);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, MaxField> max(O ref, String label) {
        return new FieldDefinition<>(
                MaxField.class,
                ref,
                label,
                TOLERANCE
        );
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, Addition> add(O ref, String label) {
        return new FieldDefinition<>(Addition.class, ref, label);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, Subtraction> sub(O ref, String label) {
        return new FieldDefinition<>(Subtraction.class, ref, label);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, MixFunction> mix(O ref, String label) {
        return new FieldDefinition<>(MixFunction.class, ref, label);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, ExcludeInput> excludeInput(O ref, String label) {
        return new FieldDefinition<>(ExcludeInput.class, ref, label);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, Multiplication> mul(O ref, String label) {
        return new FieldDefinition(Multiplication.class, ref, label);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, FieldFunction> func(O ref, String label, Double tolerance) {
        return new FieldFunctionDefinition(ref, label, tolerance, null);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, BiFunction> func(O ref, String label) {
        return new BiFunctionFieldDefinition<>(ref, label, null);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, ThresholdOperator> threshold(O ref, String label, double threshold, ThresholdOperator.Type type) {
        return new ThresholdOperatorFieldDefinition<>(ref, label, threshold, type);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, ThresholdOperator> threshold(O ref, String label, double threshold, ThresholdOperator.Type type, boolean isFinal, BiConsumer<O, Path> pathProvider, String in) {
        return new ThresholdOperatorFieldDefinition<>(ref, label, threshold, type, isFinal);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, InvertFunction> invert(O ref, String label) {
        return new FieldDefinition<>(InvertFunction.class, ref, label);
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<O, ScaleFunction> scale(O ref, String label, double scale) {
        return new FieldDefinition(ScaleFunction.class, ref, label, scale);
    }
}
