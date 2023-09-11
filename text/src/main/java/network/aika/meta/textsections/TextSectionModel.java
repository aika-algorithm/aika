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

    protected BeforeRelationNeuron relationPT;
    protected BeforeRelationNeuron relationNT;

    protected PatternNeuron patternN;

    protected BindingNeuron beginBN;

    protected PatternNeuron beginInputPN;

    protected BindingNeuron endBN;

    protected PatternNeuron endInputPN;

    protected double bindingNetTarget = 2.5;

    protected double patternNetTarget = 0.7;


    public TextSectionModel(PhraseModel phraseModel) {
        this.phraseModel = phraseModel;
        model = phraseModel.getModel();
    }

    public void initStaticNeurons() {
        log.info("Text-Section");

        relationPT = BeforeRelationNeuron.createBeforeRelationNeuron(model, -300, -1, "Prev. Token Rel.: -300, -1")
                .setTargetNet(bindingNetTarget)
                .setPersistent(true);

        relationNT = BeforeRelationNeuron.createBeforeRelationNeuron(model, 1, 300, "Next. Token Rel.: 1, 300")
                .setTargetNet(bindingNetTarget)
                .setPersistent(true);

        patternN = PatternNeuron.create(model, "Abstract Text-Section", true)
                .setTargetNet(patternNetTarget)
                .setPersistent(true);

        beginInputPN = createTextSectionInput("Begin");
        beginBN = addBindingNeuron(beginInputPN, "Abstract Text-Section-Begin", 10.0, bindingNetTarget);

        endInputPN = createTextSectionInput("End");
        endBN = addBindingNeuron(endInputPN, "Abstract Text-Section-End", 10.0, bindingNetTarget);


        addRelation(
                beginBN,
                endBN,
                relationPT,
                5.0,
                10.0,
                false
        );

        addPositiveFeedbackLoop(
                beginBN,
                patternN,
                2.5,
                0.0,
                false
        );

        addPositiveFeedbackLoop(
                endBN,
                patternN,
                2.5,
                0.0,
                false
        );
    }

    private PatternNeuron createTextSectionInput(String label) {
        PatternNeuron inputPN = new PatternNeuron(model)
                .setLabel(label + " Input")
                .setPersistent(true)
                .setBias(INPUT_NET_TARGET)
                .setTargetNet(INPUT_NET_TARGET);

        inputPN.makeAbstract()
                .setWeight(1.0)
                .adjustBias();

        return inputPN;
    }

    public PatternActivation addTextSection(Document doc, int begin, int end) {
        return new PatternActivation(doc.createActivationId(), doc, patternN);
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
        relationPT = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relationNT = m.lookupNeuronProvider(in.readLong()).getNeuron();
        patternN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        beginBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        endBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
