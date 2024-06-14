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
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.types.PatternCategorySynapse;
import network.aika.enums.sign.Sign;
import network.aika.Document;
import network.aika.text.TextReference;
import network.aika.Range;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import static network.aika.elements.neurons.RefType.TEMPLATE_MODEL;

/**
 *
 * @author Lukas Molzberger
 */
public class Dictionary implements Writable {

    private static final Logger log = LoggerFactory.getLogger(Dictionary.class);

    public static final double INPUT_TOKEN_NET_TARGET = 2.0;

    protected Model model;

    protected PatternNeuron inputToken;


    public Dictionary(Model m) {
        this.model = m;
    }

    public PatternNeuron getInputToken() {
        return inputToken;
    }

    public void initStaticNeurons() {
        inputToken = new PatternNeuron(model, TEMPLATE_MODEL)
                .setLabel("Input Token")
                .setBias(INPUT_TOKEN_NET_TARGET)
                .setTargetNet(INPUT_TOKEN_NET_TARGET)
                .setPersistent(true);

        inputToken.makeAbstract()
                .setWeight(3.0)
                .setPropagable(true)
                .adjustBias();

        log.info("Input Token: netTarget:" + INPUT_TOKEN_NET_TARGET);
    }

    public void initInputTokenWeights() {
        CategoryNeuron itCat = inputToken.getCategoryInputSynapse().getInput();

        model.getNeuronsByType(PatternNeuron.class)
                .map(n -> n.getOutputSynapseByType(PatternCategorySynapse.class))
                .filter(Objects::nonNull)
                .filter(s -> s.getOutput() == itCat)
                .forEach(this::mapSurprisalToWeight);
    }

    private void mapSurprisalToWeight(PatternCategorySynapse s) {
        PatternNeuron tn = s.getInput();
        if(tn.getStatistic().getSampleSpace().getN() == 0)
            return;

        Range r = new Range(0, tn.getLabel().length());
        r = r.getAbsoluteRange(model.getN());

        double surprisal = s.getInput().getStatistic().getSurprisal(Sign.POS, r, false);

        double weight = 1.0 + (-0.1 * surprisal);
        assert weight >= 1.0;

        s.setWeight(weight);

        if(log.isDebugEnabled())
            log.debug("Set category synapse weight for token: " + s.getInput().getLabel() + " (weight: " + weight + " surprisal: " + surprisal + ")");
    }

    public PatternNeuron getTokenNeuron(String label) {
        return model.getNeuronByLabel(label, inputToken);
    }

    public PatternNeuron lookupInputToken(String label) {
        return model.lookupNeuronByLabel(label, inputToken);
    }

    public void addToken(Document doc, String token, TextReference ref) {
        PatternNeuron n = lookupInputToken(token);
        addToken(doc, n, ref);
    }

    public static PatternActivation addToken(Document doc, PatternNeuron n, TextReference ref) {
        return doc.addToken(
                n,
                ref
        );
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(inputToken.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        inputToken = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
    }
}
