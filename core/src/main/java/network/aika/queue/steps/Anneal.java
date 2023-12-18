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
import network.aika.ActivationFunction;
import network.aika.elements.activations.Activation;
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

    private double nextStep;

    public static void add(BindingActivation act, double nv) {
        add(new Anneal(act, nv));
    }

    public Anneal(BindingActivation act, double nv) {
        super(act);

        nextStep = nv;
    }

    @Override
    public boolean incrementRound() {
        return true;
    }

    @Override
    public void process() {
        BindingActivation act = getElement();
        Document doc = act.getDocument();

        nextStep -= doc.getConfig().getAnnealStepSize();

        act.getFeedbackTrigger().setValue(ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT.f(nextStep));

        if (nextStep > 0.0)
            Anneal.add(this);
    }

    @Override
    public Phase getPhase() {
        return ANNEAL;
    }

    @Override
    public String toString() {
        Activation act = getElement();
        return getElement() +
                " NextStep:" + doubleToString(nextStep, "#.######");
    }
}
