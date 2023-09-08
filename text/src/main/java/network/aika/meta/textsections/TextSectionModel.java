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
package network.aika.meta.textsections;

import network.aika.Model;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.BeforeRelationNeuron;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.meta.NetworkMotifs.*;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class TextSectionModel implements Writable {

    private static final Logger log = LoggerFactory.getLogger(TextSectionModel.class);

    protected static double NEG_MARGIN_TS_BEGIN = 1.1;
    protected static double NEG_MARGIN_TS_END = 1.1;

    protected static double NEG_MARGIN_TS = 1.1;

    public static final double INPUT_NET_TARGET = 5.0;


    protected PhraseModel phraseModel;


    protected Model model;

    protected NeuronProvider relationPT;
    protected NeuronProvider relationNT;

    protected NeuronProvider patternN;

    protected NeuronProvider beginBN;

    protected NeuronProvider beginInputPN;

    protected NeuronProvider endBN;

    protected NeuronProvider endInputPN;

    protected double bindingNetTarget = 2.5;

    protected double patternNetTarget = 0.7;


    public TextSectionModel(PhraseModel phraseModel) {
        this.phraseModel = phraseModel;
        model = phraseModel.getModel();
    }

    public void initStaticNeurons() {
        log.info("Text-Section");

        relationPT = BeforeRelationNeuron.createBeforeRelationNeuron(model, -300, -1, "Prev. Token Rel.: -300, -1")
                .getProvider(true);
        relationPT.getNeuron().setTargetNet(bindingNetTarget);

        relationNT = BeforeRelationNeuron.createBeforeRelationNeuron(model, 1, 300, "Next. Token Rel.: 1, 300")
                .getProvider(true);
        relationNT.getNeuron().setTargetNet(bindingNetTarget);

        patternN = PatternNeuron.create(model, "Abstract Text-Section", true);
        patternN.getNeuron().setTargetNet(patternNetTarget);

        beginInputPN = createTextSectionInput("Begin");
        beginBN = addBindingNeuron(beginInputPN.getNeuron(), "Abstract Text-Section-Begin", 10.0, bindingNetTarget)
                .getProvider(true);

        endInputPN = createTextSectionInput("End");
        endBN = addBindingNeuron(endInputPN.getNeuron(), "Abstract Text-Section-End", 10.0, bindingNetTarget)
                .getProvider(true);


        addRelation(
                beginBN.getNeuron(),
                endBN.getNeuron(),
                relationPT.getNeuron(),
                5.0,
                10.0,
                false
        );

        addPositiveFeedbackLoop(
                beginBN.getNeuron(),
                patternN.getNeuron(),
                2.5,
                0.0,
                false
        );

        addPositiveFeedbackLoop(
                endBN.getNeuron(),
                patternN.getNeuron(),
                2.5,
                0.0,
                false
        );
    }

    private NeuronProvider createTextSectionInput(String label) {
        PatternNeuron inputPN = new PatternNeuron(model)
                .setLabel(label + " Input");

        inputPN.setBias(INPUT_NET_TARGET);
        inputPN.setTargetNet(INPUT_NET_TARGET);

        makeAbstract(inputPN)
                .setWeight(1.0)
                .adjustBias();

        return inputPN.getProvider(true);
    }

    public PatternActivation addTextSection(Document doc, int begin, int end) {
        return new PatternActivation(doc.createActivationId(), doc, patternN.getNeuron());
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(relationPT.getId());
        out.writeLong(relationNT.getId());;
        out.writeLong(patternN.getId());;
        out.writeLong(beginBN.getId());;
        out.writeLong(endBN.getId());;
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        relationPT = m.lookupNeuronProvider(in.readLong());
        relationNT = m.lookupNeuronProvider(in.readLong());
        patternN = m.lookupNeuronProvider(in.readLong());
        beginBN = m.lookupNeuronProvider(in.readLong());
        endBN = m.lookupNeuronProvider(in.readLong());
    }
}
