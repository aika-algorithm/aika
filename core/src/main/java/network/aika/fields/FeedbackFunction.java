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

/**
 * @author Lukas Molzberger
 */
public class FeedbackFunction extends IdentityFunction {

    public FeedbackFunction(FieldObject ref, String label) {
        super(ref, label);
    }

    @Override
    protected int getNumberOfFunctionArguments() {
        return 2;
    }

    @Override
    protected void receiveUpdateInternal(FieldLink fl, boolean nextRound, double u) {
        if (u == 0.0)
            return;

        AbstractFieldLink oppositeFl = getInputLinkByArg(fl.getArgument() == 0 ? 1 : 0);
        switch (fl.getArgument()) {
            case 0:
                if(oppositeFl != null && oppositeFl.getUpdatedInputValue() > 0.5)
                    return;

                triggerUpdate(true, u);
                break;
            case 1:
                double inputValue = oppositeFl != null ? oppositeFl.getUpdatedInputValue() : 0.0;
                double update = fl.getUpdatedInputValue() > 0.5 ?
                        1.0 - inputValue :
                        inputValue - 1.0;

                triggerUpdate(false, update);
                assert value >= 0.0 && value <= 1.0;

                break;
            default:
        }
    }
}