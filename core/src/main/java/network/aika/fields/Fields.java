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
import java.util.function.DoubleFunction;

import static network.aika.fields.link.FieldLink.*;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class Fields {

    public static boolean isTrue(FieldOutput f) {
        if(f == null)
            return false;

        return isTrue(f.getValue());
    }

    public static boolean isTrue(FieldOutput f, boolean updatedValue) {
        if(f == null)
            return false;

        return isTrue(
                updatedValue ?
                        f.getUpdatedValue() :
                        f.getValue()
        );
    }

    private static boolean isTrue(double v) {
        return v > 0.0;
    }

    public static MaxField max(FieldObject ref, String label, boolean allowNegativeInput, FieldOutput... in) {
        if(in == null)
            return null;

        MaxField max = new MaxField(ref, label, allowNegativeInput, TOLERANCE);
        for(FieldOutput fi: in)
            link(fi, max);

        max.connectInputs(true);

        return max;
    }

    public static Addition add(FieldObject ref, String label, FieldOutput in1, FieldOutput in2) {
        if(in1 == null || in2 == null)
            return null;

        Addition add = new Addition(ref, label);
        link(in1, 0, add);
        link(in2, 1, add);
        add.connectInputs(true);

        return add;
    }

    public static Addition add(FieldObject ref, String label, FieldOutput in1, FieldOutput in2, FieldInput... out) {
        Addition add = add(ref, label, in1, in2);
        linkAndConnectAll(add, out);
        return add;
    }

    public static Subtraction sub(FieldObject ref, String label, FieldOutput in1, FieldOutput in2) {
        if(in1 == null || in2 == null)
            return null;

        Subtraction sub = new Subtraction(ref, label);
        link(in1, 0, sub);
        link(in2, 1, sub);
        sub.connectInputs(true);

        return sub;
    }

    public static MixFunction mix(FieldObject ref, String label, FieldOutput x, FieldOutput in1, FieldOutput in2) {
        if(in1 == null || in2 == null)
            return null;

        MixFunction mix = new MixFunction(ref, label);

        link(x, 0, mix);
        link(in1, 1, mix);
        link(in2, 2, mix);
        mix.connectInputs(true);

        return mix;
    }

    public static MixFunction mix(FieldObject ref, String label, FieldOutput x, FieldOutput in1, FieldOutput in2, FieldInput... out) {
        MixFunction mix = mix(ref, label, x, in1, in2);
        linkAndConnectAll(mix, out);
        return mix;
    }

    public static ExcludeInput excludeInput(FieldObject ref, String label, FieldOutput in1, FieldOutput in2) {
        if(in1 == null || in2 == null)
            return null;

        ExcludeInput sub = new ExcludeInput(ref, label);
        link(in1, 0, sub);
        link(in2, 1, sub);
        sub.connectInputs(true);

        return sub;
    }

    public static Multiplication mul(FieldObject ref, String label, FieldOutput in1, boolean propagateUpdates1, FieldOutput in2, boolean propagateUpdates2) {
        if(in1 == null || in2 == null)
            return null;

        Multiplication mul = new Multiplication(ref, label);
        FieldLink fl1 = link(in1, 0, mul);
        fl1.setPropagateUpdates(propagateUpdates1);

        FieldLink fl2 = link(in2, 1, mul);
        fl2.setPropagateUpdates(propagateUpdates2);

        mul.connectInputs(true);

        return mul;
    }

    public static Multiplication mul(FieldObject ref, String label, FieldOutput in1, boolean connect1, boolean propagateUpdates1, FieldOutput in2, boolean connect2, boolean propagateUpdates2) {
        if(in1 == null || in2 == null)
            return null;

        Multiplication mul = new Multiplication(ref, label);
        FieldLink fl1 = link(in1, 0, mul);
        fl1.setPropagateUpdates(propagateUpdates1);

        FieldLink fl2 = link(in2, 1, mul);
        fl2.setPropagateUpdates(propagateUpdates2);

        if(connect1)
            fl1.connect(true);

        if(connect2)
            fl2.connect(true);

        return mul;
    }

    public static Multiplication mul(FieldObject ref, String label, FieldOutput in1, FieldOutput in2) {
        if(in1 == null || in2 == null)
            return null;

        Multiplication mul = new Multiplication(ref, label);
        link(in1, 0, mul);
        link(in2, 1, mul);
        mul.connectInputs(true);

        return mul;
    }

    public static Multiplication mul(FieldObject ref, String label, FieldOutput in1, FieldOutput in2, FieldInput... out) {
        Multiplication mul = mul(ref, label, in1, in2);
        linkAndConnectAll(mul, out);
        return mul;
    }

    public static FieldFunction func(FieldObject ref, String label, Double tolerance, FieldOutput in, DoubleFunction<Double> f) {
        if(in == null)
            return null;

        FieldFunction func = new FieldFunction(ref, label, tolerance, f);
        link(in, 0, func)
                .connect(true);

        return func;
    }

    public static FieldFunction func(FieldObject ref, String label, Double tolerance, FieldOutput in, DoubleFunction<Double> f, FieldInput... out) {
        if(in == null)
            return null;

        FieldFunction func = func(ref, label, tolerance, in, f);
        linkAndConnectAll(func, out);
        return func;
    }

    public static BiFunction func(FieldObject ref, String label, FieldOutput in1, FieldOutput in2, DoubleBinaryOperator f) {
        if(in1 == null || in2 == null)
            return null;

        BiFunction func = new BiFunction(ref, label, f);
        link(in1, 0, func);
        link(in2, 1, func);
        func.connectInputs(true);

        return func;
    }

    public static BiFunction func(FieldObject ref, String label, FieldOutput in1, FieldOutput in2, DoubleBinaryOperator f, FieldInput... out) {
        BiFunction func = func(ref, label, in1, in2, f);
        linkAndConnectAll(func, out);
        return func;
    }

    public static ThresholdOperator threshold(FieldObject ref, String label, double threshold, ThresholdOperator.Type type, FieldOutput in) {
        if(in == null)
            return null;

        ThresholdOperator op = new ThresholdOperator(ref, label, threshold, type);
        link(in, 0, op)
                .connect(true);
        return op;
    }

    public static ThresholdOperator threshold(FieldObject ref, String label, double threshold, ThresholdOperator.Type type, boolean isFinal, FieldOutput in) {
        if(in == null)
            return null;

        ThresholdOperator op = new ThresholdOperator(ref, label, threshold, type, isFinal);
        link(in, 0, op)
                .connect(true);
        return op;
    }

    public static InvertFunction invert(FieldObject ref, String label, FieldOutput in) {
        if(in == null)
            return null;

        InvertFunction f = new InvertFunction(ref, label);
        link(in, 0, f)
                .connect(true);
        return f;
    }

    public static ScaleFunction scale(FieldObject ref, String label, double scale, FieldOutput in) {
        if(in == null)
            return null;

        ScaleFunction f = new ScaleFunction(ref, label, scale);
        link(in, 0, f)
                .connect(true);
        return f;
    }

    public static ScaleFunction scale(FieldObject ref, String label, double scale, FieldOutput in, FieldInput... out) {
        ScaleFunction f = scale(ref, label, scale, in);
        linkAndConnectAll(f, out);
        return f;
    }
}
