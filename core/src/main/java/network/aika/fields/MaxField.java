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

import network.aika.fields.link.AbstractFieldLink;
import network.aika.fields.link.FieldLink;
import network.aika.utils.Utils;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 *
 */
public class MaxField extends SumField {

    private FieldLink selectedInput;

    private MaxFieldListener sectionChangeListener;

    private boolean allowNegativeInput;

    public MaxField(FieldObject ref, String label, boolean allowNegativeInput, Double tolerance) {
        super(ref, label, tolerance);

        this.allowNegativeInput = allowNegativeInput;
    }

    public MaxField(FieldObject ref, String label, boolean allowNegativeInput, Double tolerance, MaxFieldListener scl) {
        this(ref, label, allowNegativeInput, tolerance);

        this.sectionChangeListener = scl;
    }

    public FieldLink getSelectedInput() {
        return selectedInput;
    }

    @Override
    public void receiveUpdate(FieldLink ufl, double u) {
        double update = getCandidateInputs()
                .mapToDouble(AbstractFieldLink::getUpdatedInputValue)
                .max()
                .orElse(0.0) - value;

        if(!(allowNegativeInput && selectedInput == null) &&
                Utils.belowTolerance(tolerance, update))
            return;

        if(interceptor != null) {
            interceptor.receiveUpdate(update, true);
            return;
        }

        triggerUpdate(update);
    }

    @Override
    public void triggerUpdate(double u) {
        FieldLink lastSelectedInput = selectedInput;

        selectedInput = getCandidateInputs()
                .max(Comparator.comparingDouble(AbstractFieldLink::getUpdatedInputValue))
                .orElse(null);

        if(sectionChangeListener != null && lastSelectedInput != selectedInput) {
            sectionChangeListener.updateSelectedInput(lastSelectedInput, false);
            sectionChangeListener.updateSelectedInput(selectedInput, true);
        }

        super.triggerUpdate(u);
    }

    private Stream<FieldLink> getCandidateInputs() {
        Stream<FieldLink> inputs = getInputs().stream();
        return allowNegativeInput ?
                inputs :
                inputs.filter(fl -> fl.getUpdatedInputValue() > 0.0);
    }
}
