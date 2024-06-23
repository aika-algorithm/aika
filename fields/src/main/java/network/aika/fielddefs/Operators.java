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
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import static network.aika.fielddefs.FieldLinkDefinition.link;
import static network.aika.fielddefs.FieldLinkDefinition.linkAll;

/**
 * @author Lukas Molzberger
 */
public class Operators {

    public static <O extends FieldObjectDefinition, F extends Field> FieldDefinition<F> max(O ref, String label, String... in) {
        if(in == null)
            return null;

        FieldDefinition max = new FieldDefinition(MaxField.class, ref, label, ToleranceUtils.TOLERANCE);
        for(FieldOutputDefinition fi: in)
            link(fi, max);

        return max;
    }

    public static <O extends FieldObjectDefinition, F extends Field> FieldDefinition<F> add(O ref, String label, BiConsumer<O, Path> pathProvider1, String in1, BiConsumer<O, Path> pathProvider2, String in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition add = new FieldDefinition(Addition.class, ref, label);
        link(ref, pathProvider1, in1, 0, label);
        link(ref, pathProvider2, in2, 1, label);

        return add;
    }

    public static <O extends FieldObjectDefinition, F extends Field> FieldDefinition<F> sub(O ref, String label, BiConsumer<O, Path> pathProvider1, String in1, BiConsumer<O, Path> pathProvider2, String in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition sub = new FieldDefinition(Subtraction.class, ref, label);
        link(ref, pathProvider1, in1, 0, label);
        link(ref, pathProvider2, in2, 1, label);

        return sub;
    }

    public static <O extends FieldObjectDefinition, F extends Field> FieldDefinition<F> mix(O ref, String label, BiConsumer<O, Path> pathProviderX, String x, BiConsumer<O, Path> pathProvider1, String in1, BiConsumer<O, Path> pathProvider2, String in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition mix = new FieldDefinition(MixFunction.class, ref, label);

        link(ref, pathProviderX, x, 0, label);
        link(ref, pathProvider1, in1, 1, label);
        link(ref, pathProvider2, in2, 2, label);

        return mix;
    }

    public static <O extends FieldObjectDefinition, F extends Field> FieldDefinition<F> excludeInput(O ref, String label, BiConsumer<O, Path> pathProvider1, String in1, BiConsumer<O, Path> pathProvider2, String in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition sub = new FieldDefinition(ExcludeInput.class, ref, label);
        link(ref, pathProvider1, in1, 0, label);
        link(ref, pathProvider2, in2, 1, label);

        return sub;
    }

    public static <O extends FieldObjectDefinition, F extends Field> FieldDefinition<F> mul(O ref, String label, BiConsumer<O, Path> pathProvider1, String in1, boolean propagateUpdates1, BiConsumer<O, Path> pathProvider2, String in2, boolean propagateUpdates2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition mul = new FieldDefinition(Multiplication.class, ref, label);
        FieldLinkDefinition fl1 = link(ref, pathProvider1, in1, 0, label);
        fl1.setPropagateUpdates(propagateUpdates1);

        FieldLinkDefinition fl2 = link(ref, pathProvider2, in2, 1, label);
        fl2.setPropagateUpdates(propagateUpdates2);

        return mul;
    }

    public static <O extends FieldObjectDefinition, F extends Field> FieldDefinition<F> mul(O ref, String label, BiConsumer<O, Path> pathProvider1, String in1, boolean connect1, boolean propagateUpdates1, BiConsumer<O, Path> pathProvider2, String in2, boolean connect2, boolean propagateUpdates2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition mul = new FieldDefinition(Multiplication.class, ref, label);
        FieldLinkDefinition fl1 = link(ref, pathProvider1, in1, 0, label);
        fl1.setPropagateUpdates(propagateUpdates1);

        FieldLinkDefinition fl2 = link(ref, pathProvider2, in2, 1, label);
        fl2.setPropagateUpdates(propagateUpdates2);

        return mul;
    }

    public static <O extends FieldObjectDefinition, F extends Field> FieldDefinition<F> mul(O ref, String label, BiConsumer<O, Path> pathProvider1, String in1, BiConsumer<O, Path> pathProvider2, String in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition mul = new FieldDefinition(Multiplication.class, ref, label);
        link(ref, pathProvider1, in1, 0, label);
        link(ref, pathProvider2, in2, 1, label);

        return mul;
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<FieldFunction> func(O ref, String label, Double tolerance, BiConsumer<O, Path> pathProvider, String in, ReferencedFunction f) {
        if(in == null)
            return null;

        FieldDefinition<FieldFunction> func = new FieldFunctionDefinition(ref, label, tolerance, f);
        link(ref, pathProvider,in, 0, label);

        return func;
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<BiFunction> func(O ref, String label, BiConsumer<O, Path> pathProvider1, String in1, BiConsumer<O, Path> pathProvider2, String in2, ReferencedBiFunction f) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition<BiFunction> func = new BiFunctionFieldDefinition(ref, label, f);
        link(ref, pathProvider1, in1, 0, label);
        link(ref, pathProvider1, in2, 1, label);

        return func;
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<ThresholdOperator> threshold(O ref, String label, double threshold, ThresholdOperator.Type type, BiConsumer<O, Path> pathProvider, String in) {
        if(in == null)
            return null;

        FieldDefinition<ThresholdOperator> op = new ThresholdOperatorFieldDefinition(ref, label, threshold, type);
        link(ref, pathProvider, in, 0, label);
        return op;
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<ThresholdOperator> threshold(O ref, String label, double threshold, ThresholdOperator.Type type, boolean isFinal, BiConsumer<O, Path> pathProvider, String in) {
        if(in == null)
            return null;

        FieldDefinition<ThresholdOperator> op = new ThresholdOperatorFieldDefinition(ref, label, threshold, type, isFinal);
        link(ref, pathProvider, in, 0, label);
        return op;
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<ThresholdOperator> invert(O ref, String label, BiConsumer<O, Path> pathProvider, String in) {
        if(in == null)
            return null;

        FieldDefinition<ThresholdOperator> f = new FieldDefinition(ThresholdOperator.class, ref, label);
        link(ref, pathProvider, in, 0, label);
        return f;
    }

    public static <O extends FieldObjectDefinition> FieldDefinition<ScaleFunction> scale(O ref, String label, double scale, BiConsumer<O, Path> pathProvider, String in) {
        if(in == null)
            return null;

        FieldDefinition<ScaleFunction> f = new FieldDefinition(ScaleFunction.class, ref, label, scale);
        link(ref, pathProvider, in, 0, label);
        return f;
    }
}
