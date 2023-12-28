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

/**
 * @author Lukas Molzberger
 *
 */
public class MaxField extends SumField {

    private FieldLink selectedInput;

    public MaxField(FieldObject ref, String label, Double tolerance) {
        super(ref, label, tolerance);
    }

    public FieldLink getSelectedInput() {
        return selectedInput;
    }

    @Override
    public void receiveUpdate(FieldLink fl, double u) {
        double update = getInputs().stream()
                .mapToDouble(AbstractFieldLink::getUpdatedInputValue)
                .max()
                .orElse(0.0) - value;

        if(selectedInput != null && Utils.belowTolerance(tolerance, update))
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

        selectedInput = getInputs().stream()
                .filter(this::isCandidate)
                .max(Comparator.comparingDouble(AbstractFieldLink::getUpdatedInputValue))
                .orElse(null);

        if(lastSelectedInput != selectedInput) {
            updateSelectedInput(lastSelectedInput, false);
            updateSelectedInput(selectedInput, true);
        }

        super.triggerUpdate(u);
    }

    protected void updateSelectedInput(FieldLink si, boolean state) {
    }

    protected boolean isCandidate(FieldLink fl) {
        return true;
    }
}
