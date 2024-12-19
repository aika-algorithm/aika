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

import network.aika.fields.Obj;
import network.aika.fields.ThresholdOperator;

/**
 * @author Lukas Molzberger
 */
public class ThresholdOperatorFieldDefinition<T extends Type<T, O>, O extends Obj<T, O>> extends FieldDefinition<T, O> {

    double threshold;
    ThresholdOperator.Comparison comparison;

    boolean isFinal;

    public ThresholdOperatorFieldDefinition(T ref, FieldTag fieldTag, double threshold, ThresholdOperator.Comparison comparison) {
        super(ThresholdOperator.class, ref, fieldTag);

        this.threshold = threshold;
        this.comparison = comparison;
    }

    public ThresholdOperatorFieldDefinition(T ref, FieldTag fieldTag, double threshold, ThresholdOperator.Comparison comparison, boolean isFinal) {
        super(ThresholdOperator.class, ref, fieldTag);

        this.threshold = threshold;
        this.comparison = comparison;
        this.isFinal = isFinal;
    }

    public ThresholdOperatorFieldDefinition(T ref, FieldTag fieldTag, double threshold, ThresholdOperator.Comparison comparison, double tolerance) {
        super(ThresholdOperator.class, ref, fieldTag, tolerance);

        this.threshold = threshold;
        this.comparison = comparison;
    }

    @Override
    public ThresholdOperator instantiate(O reference) {
        ThresholdOperator to = (ThresholdOperator) super.instantiate(reference);
        to.setThreshold(threshold);
        to.setComparison(comparison);
        to.setFinal(isFinal);

        return to;
    }
}
