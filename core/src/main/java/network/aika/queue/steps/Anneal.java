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
package network.aika.queue.steps;

import network.aika.Document;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;

import static network.aika.queue.Phase.*;
import static network.aika.utils.Utils.doubleToString;


/**
 *
 * @author Lukas Molzberger
 */
public class Anneal extends ElementStep<BindingActivation> {


    public static void add(BindingActivation act) {
        double v = act.getFeedbackTrigger().getValue();
        if (v <= 1.0)
            add(new Anneal(act));
    }

    public Anneal(BindingActivation act) {
        super(act);
    }

    @Override
    public boolean incrementRound() {
        return true;
    }

    @Override
    public void process() {
        BindingActivation act = getElement();
        Document doc = act.getDocument();

        double v = act.getFeedbackTrigger().getValue();
        if(v >= 1.0)
            return;

        act.getFeedbackTrigger().setValue(
                Math.min(
                        v + doc.getConfig().getAnnealStepSize(),
                        1.0
                )
        );

        Anneal.add(act);
    }

    @Override
    public Phase getPhase() {
        return ANNEAL;
    }

    @Override
    public String toString() {
        return getElement() +
                " LastValue:" + doubleToString(getElement().getFeedbackTrigger().getValue(), "#.######");
    }
}
