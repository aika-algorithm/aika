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
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.LatentRelationNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.relations.BeforeRelation;
import network.aika.meta.sequences.PhraseModel;
import network.aika.Document;
import network.aika.text.Range;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.PATTERN;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.meta.LabelUtil.*;
import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class TextSectionModel implements Writable {

    private static final Logger log = LoggerFactory.getLogger(TextSectionModel.class);

    protected static double NEG_MARGIN_TS = 1.1;

    public static final double INPUT_NET_TARGET = 5.0;

    public static final String TEXT_SECTION_LABEL = "Text-Section";


    protected PhraseModel phraseModel;


    protected Model model;


    protected LatentRelationNeuron beginEndRelation;

    protected LatentRelationNeuron relationPT;
    protected LatentRelationNeuron relationNT;

    protected PatternNeuron textSectionPatternN;

    protected BindingNeuron beginBN;

    protected PatternNeuron beginInputPN;

    protected BindingNeuron endBN;

    protected PatternNeuron endInputPN;

    protected double bindingNetTarget = 2.5;

    public TextSectionModel(PhraseModel phraseModel) {
        this.phraseModel = phraseModel;
        model = phraseModel.getModel();
    }

    public PatternNeuron getBeginInputPN() {
        return beginInputPN;
    }

    public PatternNeuron getEndInputPN() {
        return endInputPN;
    }

    public void initStaticNeurons() {
        log.info(TEXT_SECTION_LABEL);

        beginEndRelation = new LatentRelationNeuron(
                model,
                new BeforeRelation(
                OUTPUT,
                new Range(-300, 0)
                )
        )
                .setLabel("Begin-End Relation")
                .setBias(5.0)
                .setTargetNet(5.0)
                .setPersistent(true);

        relationPT = new LatentRelationNeuron(
                model,
                new BeforeRelation(
                        INPUT,
                        new Range(-300, 0)
                )
        )
                .setLabel("Prev. Token Rel.: -300, -0")
                .setBias(5.0)
                .setTargetNet(5.0)
                .setPersistent(true);

        relationNT = new LatentRelationNeuron(
                model,
                new BeforeRelation(
                        OUTPUT,
                        new Range(0, 300)
                )
        )
                .setLabel("Next. Token Rel.: 0, 300")
                .setBias(5.0)
                .setTargetNet(5.0)
                .setPersistent(true);

        textSectionPatternN = PatternNeuron.create(model, getAbstractLabel(PATTERN, TEXT_SECTION_LABEL))
                .setBias(0.7)
                .setTargetNet(0.7);

        textSectionPatternN.makeAbstract()
                .setWeight(getDefaultInputCategorySynapseWeight(textSectionPatternN.getType()))
                .adjustBias();

        beginInputPN = createTextSectionInput("Begin");
        beginBN = addBindingNeuron(beginInputPN, getAbstractLabel(BINDING, TEXT_SECTION_LABEL + "-Begin"), 10.0, bindingNetTarget);
        beginBN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        endInputPN = createTextSectionInput("End");
        endBN = addBindingNeuron(endInputPN, getAbstractLabel(BINDING, TEXT_SECTION_LABEL + "-End"), 10.0, bindingNetTarget);
        endBN.makeAbstract()
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addRelation(
                beginBN,
                endBN,
                beginEndRelation,
                5.0,
                10.0,
                false
        );

        addPositiveFeedbackLoop(
                beginBN,
                textSectionPatternN,
                2.5,
                0.0,
                false,
                false
        );

        addPositiveFeedbackLoop(
                endBN,
                textSectionPatternN,
                2.5,
                0.0,
                false,
                false
        );
    }

    protected PatternNeuron createTextSectionInput(String label) {
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
        return new PatternActivation(doc.createActivationId(), doc, textSectionPatternN);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(beginEndRelation.getId());
        out.writeLong(relationPT.getId());
        out.writeLong(relationNT.getId());
        out.writeLong(textSectionPatternN.getId());
        out.writeLong(beginInputPN.getId());
        out.writeLong(beginBN.getId());
        out.writeLong(endInputPN.getId());
        out.writeLong(endBN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        beginEndRelation = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relationPT = m.lookupNeuronProvider(in.readLong()).getNeuron();
        relationNT = m.lookupNeuronProvider(in.readLong()).getNeuron();
        textSectionPatternN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        beginInputPN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        beginBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        endInputPN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        endBN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
