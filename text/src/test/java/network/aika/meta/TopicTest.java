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
import network.aika.meta.sequences.SequenceModel;
import network.aika.meta.sequences.PhraseModel;
import network.aika.parser.Context;
import network.aika.parser.TrainingParser;
import network.aika.Document;
import network.aika.tokenizer.Tokenizer;
import network.aika.tokenizer.WordTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.parser.ParserPhase.COUNTING;
import static network.aika.parser.ParserPhase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class TopicTest extends TrainingParser {

    private Dictionary dict;
    private Tokenizer tokenizer;
    private SequenceModel templateModel;

    @BeforeEach
    public void init() {
        Model model = new Model();
        dict = new Dictionary(model);
        dict.initStaticNeurons();

        templateModel = new PhraseModel(model, dict);
        templateModel.initStaticNeurons();

        model.setN(0);

        tokenizer = new WordTokenizer();
    }

    @Override
    protected void prepareInputs(Document doc, Context context) {
        tokenizer.tokenize(doc, context, (token, ref) ->
                doc.addToken(
                        dict.lookupInputToken(token),
                        ref
                )
        );
    }

    @Test
    public void testTopics() {
        process("a b", null, COUNTING);
        templateModel.initStaticNeurons();
        process("a b", null, TRAINING);
    }

    @Override
    protected SequenceModel getPhraseModel() {
        return templateModel;
    }
}
