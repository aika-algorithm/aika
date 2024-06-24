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
import network.aika.utils.ApproximateComparisonValueUtil;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 *
 */
public abstract class AbstractMaxField<O extends FieldObject, F extends FieldLink> extends Field<O, F> {

    private F selectedInput;

    public static final Comparator<? extends FieldLink> INPUT_VALUE_COMPARATOR = Comparator.comparingInt(fl ->
            ApproximateComparisonValueUtil.convert(fl.getUpdatedInputValue())
    );

    public AbstractMaxField(O ref, String label, Double tolerance) {
        super(ref, label, tolerance);
    }

    protected void updateSelectedInput(F si, boolean state) {

    }

    public F getSelectedInput() {
        return selectedInput;
    }

    @Override
    public void receiveUpdate(F ufl, double u) {
        assert interceptor == null; // Not supported to avoid having to calculate the max twice.

        triggerUpdate(0.0);
    }

    protected boolean isNegativeInputAllowed() {
        return false;
    }

    public F getMaxInput() {
        return getCandidateInputs()
                .max(getComparator())
                .orElse(null);
    }

    public Comparator<F> getComparator() {
        return (Comparator<F>) INPUT_VALUE_COMPARATOR;
    }

    @Override
    public void triggerUpdate(double u) {
        F lastSelectedInput = selectedInput;

        selectedInput = getMaxInput();

        double maxInputValue = selectedInput != null ?
                selectedInput.getUpdatedInputValue() :
                0.0;

        double update = maxInputValue - value;

        if(lastSelectedInput != selectedInput) {
            updateSelectedInput(lastSelectedInput, false);
            updateSelectedInput(selectedInput, true);
        }

        super.triggerUpdate(update);
    }

    private synchronized Stream<F> getCandidateInputs() {
        Stream<F> inputs = getInputs().stream();
        return isNegativeInputAllowed() ?
                inputs :
                inputs.filter(fl -> fl.getUpdatedInputValue() > 0.0);
    }
}
