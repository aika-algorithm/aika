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
import network.aika.elements.activations.Activation;
import network.aika.elements.synapses.Synapse;
import network.aika.parser.TrainingParser;
import network.aika.tokenizer.SimpleWordTokenizer;
import network.aika.tokenizer.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.parser.ParserPhase.COUNTING;
import static network.aika.parser.ParserPhase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class TopicTest extends TrainingParser {

    private AbstractTemplateModel templateModel;
    private Tokenizer tokenizer;

    @BeforeEach
    public void init() {
        Model model = new Model();

        templateModel = new PhraseTemplateModel(model);
        templateModel.initStaticNeurons();

        model.setN(0);

        tokenizer = new SimpleWordTokenizer(templateModel);
    }

    @Test
    public void testTopics() {
        process("a b", null, COUNTING);
        templateModel.initTemplates();
        process("a b", null, TRAINING);
    }

    @Override
    protected AbstractTemplateModel getTemplateModel() {
        return templateModel;
    }

    @Override
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

}
