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

import network.aika.fields.defs.FieldLinkDefinitionOutputSide;
import network.aika.type.Obj;
import network.aika.type.Type;


/**
 * @author Lukas Molzberger
 */
public class ThresholdOperator extends AbstractFunctionDefinition {

    public static ThresholdOperator threshold(Type ref, String name, double threshold, Comparison type) {
        return new ThresholdOperator(ref, name, threshold, type);
    }

    public static ThresholdOperator threshold(Type ref, String name, double threshold, Comparison type, boolean isFinal) {
        return new ThresholdOperator(ref, name, threshold, type, isFinal);
    }

    private double threshold;
    private ThresholdOperator.Comparison comparison;
    private boolean isFinal;

    public ThresholdOperator(Type ref, String name, double threshold, Comparison type) {
        super(ref, name, 1);
        this.threshold = threshold;
        this.comparison = type;
    }

    public ThresholdOperator(Type ref, String name, double threshold, Comparison type, boolean isFinal) {
        super(ref, name, 1);
        this.threshold = threshold;
        this.comparison = type;
        this.isFinal = isFinal;
    }

    public enum Comparison {
        ABOVE,
        BELOW,
        BELOW_OR_EQUAL,
        ABOVE_ABS
    }

    @Override
    protected double computeUpdate(Obj obj, FieldLinkDefinitionOutputSide fl, double u) {
        double value = obj.getOrCreateFieldInput(this).getValue();
        if(isFinal && value > 0.5)
            return 0.0;

        return threshold(fl.getUpdatedInputValue(obj)) - value;
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
