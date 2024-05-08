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
package network.aika.meta.sequences;

import network.aika.Model;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.PatternCategoryNeuron;
import network.aika.meta.Dictionary;
import network.aika.meta.entities.EntityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.neurons.RefType.TEMPLATE_MODEL;
import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class PhraseModel extends SequenceModel {

    private static final Logger log = LoggerFactory.getLogger(PhraseModel.class);

    EntityModel entityModel;

    PatternCategoryNeuron upperCaseN;

    BindingNeuron entityBN;


    public PhraseModel(Model m) {
        super(m);
    }

    public void initModelDependencies(Dictionary dict, EntityModel entityModel) {
        this.dictionary = dict;
        this.entityModel = entityModel;
    }

    public BindingNeuron getEntityBN() {
        return entityBN;
    }

    @Override
    public void setInstantiable(boolean instantiable) {
        super.setInstantiable(instantiable);

        entityBN.setInstantiable(instantiable);
        entityBN.setInputSynapsesInstantiable(instantiable, instantiable);
    }

    @Override
    public String getPatternType() {
        return "Phrase";
    }

    @Override
    public void initStaticNeurons() {
        super.initStaticNeurons();

        upperCaseN = new PatternCategoryNeuron(model, TEMPLATE_MODEL)
                .setLabel("Upper Case")
                .setPersistent(true);

        entityBN = addBindingNeuron(model, "Entity (Phrase)", 2.5)
                .setTypeDescription("Abstract Entity -> Phrase BN");

        entityBN.makeAbstract(false)
                .setWeight(getDefaultInputCategorySynapseWeight(entityBN.getType()))
                .adjustBias();

        setInstantiable(true);
    }

    @Override
    public void initOuterSynapses() {
        addOuterPositiveFeedbackLoop(
                sequencePatternN,
                entityBN,
                entityModel.getEntityPattern(),
                2.5,
                true
        ).setOptional(true);
    }

    @Override
    protected void initTemplateBindingNeurons() {
        subPhraseBN = createSubPhraseBindingNeuron()
                .setTypeDescription("Abstract Sub-Phrase BN");

        primaryBN = createPrimaryBindingNeuron()
                .setTypeDescription("Abstract Primary Phrase BN");


        expandContinueBindingNeurons(
                1,
                primaryBN,
                5,
                1
        );

        expandContinueBindingNeurons(
                1,
                primaryBN,
                5,
                -1
        );
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeLong(upperCaseN.getId());
        out.writeLong(entityBN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        upperCaseN = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
        entityBN = m.lookupNeuronProvider(in.readLong(), TEMPLATE_MODEL).getNeuron();
    }
}
