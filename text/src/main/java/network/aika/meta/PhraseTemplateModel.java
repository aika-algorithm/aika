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
import network.aika.elements.neurons.*;
import network.aika.meta.textsections.TypedTextSectionModel;
import network.aika.meta.topics.TopicModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Lukas Molzberger
 */
public class PhraseTemplateModel extends AbstractTemplateModel {

    private static final Logger log = LoggerFactory.getLogger(PhraseTemplateModel.class);

    TypedTextSectionModel textSectionModel;
    TopicModel topicModel;

    NeuronProvider upperCaseN;


    public PhraseTemplateModel(Model m) {
        super(m);

        textSectionModel = new TypedTextSectionModel(this);
        topicModel = new TopicModel(this);
    }

    @Override
    public void initStaticNeurons() {
        super.initStaticNeurons();

        upperCaseN = new PatternCategoryNeuron()
                .init(model, "Upper Case")
                .getProvider(true);
    }

    @Override
    public String getPatternType() {
        return "Phrase";
    }

    public TypedTextSectionModel getTextSectionModel() {
        return textSectionModel;
    }

    public TopicModel getTopicModel() {
        return topicModel;
    }

    @Override
    protected void initTemplateBindingNeurons() {
        primaryBN = createStrongBindingNeuron(
                patternNetTarget,
                false,
                0,
                null,
                null
        ).getProvider(true);

        expandContinueBindingNeurons(
                patternNetTarget,
                1,
                primaryBN.getNeuron(),
                5,
                1
        );

        expandContinueBindingNeurons(
                patternNetTarget,
                1,
                primaryBN.getNeuron(),
                5,
                -1
        );

        textSectionModel.initTextSectionTemplates();
        topicModel.initTopicTemplates();
    }
}
