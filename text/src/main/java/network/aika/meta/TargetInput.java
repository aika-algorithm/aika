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
package network.aika.meta;

import network.aika.Model;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.neurons.TokenNeuron;
import network.aika.enums.Scope;
import network.aika.text.Document;

import static network.aika.meta.NetworkMotifs.addBindingNeuron;
import static network.aika.meta.NetworkMotifs.addPositiveFeedbackLoop;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class TargetInput {

    Model model;

    protected NeuronProvider targetInput;

    protected NeuronProvider targetInputCategory;

    protected double targetInputNetTarget = 5.0;


    public TargetInput(Model model) {
        this.model = model;
    }

    public NeuronProvider getTargetInput() {
        return targetInput;
    }

    public TokenNeuron addTarget(String target) {
        return model.lookupNeuronByLabel(target, l ->
                createTargetInput(target)
        );
    }

    public void addTarget(Document doc, String target) {
        TokenNeuron n = addTarget(target);
        doc.addToken(n, null, null, null, targetInputNetTarget);
    }

    protected TokenNeuron createTargetInput(String label) {
        TokenNeuron inputTokenN = targetInput.getNeuron();
        TokenNeuron n = inputTokenN.instantiateTemplate()
                .init(model, label);

        n.setTokenLabel(label);
        n.setAllowTraining(false);

        return n;
    }

    public void initTargetInput(String label) {
        targetInput = new TokenNeuron()
                        .init(model, label)
                .getProvider(true);

        targetInput.getNeuron()
                .setBias(targetInputNetTarget);

        targetInputCategory = makeAbstract((PatternNeuron) targetInput.getNeuron())
                .getProvider(true);
    }

    public BindingNeuron createTargetInputBindingNeuron(PatternNeuron pn, double patternNetTarget) {
        double netTarget = 2.5;

        BindingNeuron bn = addBindingNeuron(
                targetInput.getNeuron(),
                Scope.INPUT,
                "Abstract Target Input",
                10.0,
                targetInputNetTarget,
                netTarget
        );
        makeAbstract(bn);

        addPositiveFeedbackLoop(
                bn,
                pn,
                patternNetTarget,
                netTarget,
                2.5,
                0.0,
                false
        );

        return bn;
    }

    public TokenNeuron createTargetInputNeuron(String label) {
        TokenNeuron targetInputN = targetInput.getNeuron();
        TokenNeuron n = targetInputN.instantiateTemplate()
                .init(model, label);

        n.setTokenLabel(label);
        n.setAllowTraining(false);

        return n;
    }
}
