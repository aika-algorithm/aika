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

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import static network.aika.fielddefs.FieldLinkDefinition.link;
import static network.aika.fielddefs.FieldLinkDefinition.linkAll;

/**
 * @author Lukas Molzberger
 */
public class Operators {

    public static FieldDefinition max(FieldObjectDefinition ref, String label, FieldOutputDefinition... in) {
        if(in == null)
            return null;

        FieldDefinition max = new FieldDefinition(MaxField.class, ref, label, ToleranceUtils.TOLERANCE);
        for(FieldOutputDefinition fi: in)
            link(fi, max);

        return max;
    }

    public static FieldDefinition add(FieldObjectDefinition ref, String label, FieldOutputDefinition in1, FieldOutputDefinition in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition add = new FieldDefinition(Addition.class, ref, label);
        link(in1, 0, add);
        link(in2, 1, add);

        return add;
    }

    public static FieldDefinition add(FieldObjectDefinition ref, String label, FieldOutputDefinition in1, FieldOutputDefinition in2, FieldInputDefinition... out) {
        FieldDefinition add = add(ref, label, in1, in2);
        linkAll(add, out);
        return add;
    }

    public static FieldDefinition sub(FieldObjectDefinition ref, String label, FieldOutputDefinition in1, FieldOutputDefinition in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition sub = new FieldDefinition(Subtraction.class, ref, label);
        link(in1, 0, sub);
        link(in2, 1, sub);

        return sub;
    }

    public static FieldDefinition mix(FieldObjectDefinition ref, String label, FieldOutputDefinition x, FieldOutputDefinition in1, FieldOutputDefinition in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition mix = new FieldDefinition(MixFunction.class, ref, label);

        link(x, 0, mix);
        link(in1, 1, mix);
        link(in2, 2, mix);

        return mix;
    }

    public static FieldDefinition mix(FieldObjectDefinition ref, String label, FieldOutputDefinition x, FieldOutputDefinition in1, FieldOutputDefinition in2, FieldInputDefinition... out) {
        FieldDefinition mix = mix(ref, label, x, in1, in2);
        linkAll(mix, out);
        return mix;
    }

    public static FieldDefinition excludeInput(FieldObjectDefinition ref, String label, FieldOutputDefinition in1, FieldOutputDefinition in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition sub = new FieldDefinition(ExcludeInput.class, ref, label);
        link(in1, 0, sub);
        link(in2, 1, sub);

        return sub;
    }

    public static FieldDefinition mul(FieldObjectDefinition ref, String label, FieldOutputDefinition in1, boolean propagateUpdates1, FieldOutputDefinition in2, boolean propagateUpdates2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition mul = new FieldDefinition(Multiplication.class, ref, label);
        FieldLinkDefinition fl1 = link(in1, 0, mul);
        fl1.setPropagateUpdates(propagateUpdates1);

        FieldLinkDefinition fl2 = link(in2, 1, mul);
        fl2.setPropagateUpdates(propagateUpdates2);

        return mul;
    }

    public static FieldDefinition mul(FieldObjectDefinition ref, String label, FieldOutputDefinition in1, boolean connect1, boolean propagateUpdates1, FieldOutputDefinition in2, boolean connect2, boolean propagateUpdates2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition mul = new FieldDefinition(Multiplication.class, ref, label);
        FieldLinkDefinition fl1 = link(in1, 0, mul);
        fl1.setPropagateUpdates(propagateUpdates1);

        FieldLinkDefinition fl2 = link(in2, 1, mul);
        fl2.setPropagateUpdates(propagateUpdates2);

        return mul;
    }

    public static FieldDefinition mul(FieldObjectDefinition ref, String label, FieldOutputDefinition in1, FieldOutputDefinition in2) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition mul = new FieldDefinition(Multiplication.class, ref, label);
        link(in1, 0, mul);
        link(in2, 1, mul);

        return mul;
    }

    public static FieldDefinition mul(FieldObjectDefinition ref, String label, FieldOutputDefinition in1, FieldOutputDefinition in2, FieldInputDefinition... out) {
        FieldDefinition mul = mul(ref, label, in1, in2);
        linkAll(mul, out);
        return mul;
    }

    public static FieldDefinition func(FieldObjectDefinition ref, String label, Double tolerance, FieldOutputDefinition in, DoubleUnaryOperator f) {
        if(in == null)
            return null;

        FieldDefinition func = new FieldFunctionDefinition(ref, label, tolerance, f);
        link(in, 0, func);

        return func;
    }

    public static FieldDefinition func(FieldObjectDefinition ref, String label, Double tolerance, FieldOutputDefinition in, DoubleUnaryOperator f, FieldInputDefinition... out) {
        if(in == null)
            return null;

        FieldDefinition func = func(ref, label, tolerance, in, f);
        linkAll(func, out);
        return func;
    }

    public static FieldDefinition func(FieldObjectDefinition ref, String label, FieldOutputDefinition in1, FieldOutputDefinition in2, DoubleBinaryOperator f) {
        if(in1 == null || in2 == null)
            return null;

        FieldDefinition func = new BiFunctionFieldDefinition(ref, label, f);
        link(in1, 0, func);
        link(in2, 1, func);

        return func;
    }

    public static FieldDefinition func(FieldObjectDefinition ref, String label, FieldOutput in1, FieldOutput in2, DoubleBinaryOperator f, FieldInputDefinition... out) {
        FieldDefinition func = func(ref, label, in1, in2, f);
        linkAll(func, out);
        return func;
    }

    public static FieldDefinition threshold(FieldObjectDefinition ref, String label, double threshold, ThresholdOperator.Type type, FieldOutputDefinition in) {
        if(in == null)
            return null;

        FieldDefinition op = new ThresholdOperatorFieldDefinition(ref, label, threshold, type);
        link(in, 0, op);
        return op;
    }

    public static FieldDefinition threshold(FieldObjectDefinition ref, String label, double threshold, ThresholdOperator.Type type, boolean isFinal, FieldOutputDefinition in) {
        if(in == null)
            return null;

        FieldDefinition op = new ThresholdOperatorFieldDefinition(ref, label, threshold, type, isFinal);
        link(in, 0, op);
        return op;
    }

    public static FieldDefinition invert(FieldObjectDefinition ref, String label, FieldOutputDefinition in) {
        if(in == null)
            return null;

        FieldDefinition f = new FieldDefinition(InvertFunction.class, ref, label);
        link(in, 0, f);
        return f;
    }

    public static FieldDefinition scale(FieldObjectDefinition ref, String label, double scale, FieldOutputDefinition in) {
        if(in == null)
            return null;

        FieldDefinition f = new FieldDefinition(ScaleFunction.class, ref, label, scale);
        link(in, 0, f);
        return f;
    }

    public static FieldDefinition scale(FieldObjectDefinition ref, String label, double scale, FieldOutputDefinition in, FieldInputDefinition... out) {
        FieldDefinition f = scale(ref, label, scale, in);
        linkAll(f, out);
        return f;
    }
}
