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
import network.aika.Context;
import network.aika.parser.Parser;
import network.aika.Document;
import network.aika.tokenizer.WordTokenizer;
import network.aika.tokenizer.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.parser.ParserPhase.COUNTING;
import static network.aika.parser.ParserPhase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class AnnealingTest extends Parser {

    private Dictionary dict;
    private Tokenizer tokenizer;
    private SequenceModel templateModel;

    @BeforeEach
    public void init() {
        Model model = new Model();
        dict = new Dictionary(model);
        dict.initStaticNeurons();

        templateModel = new PhraseModel(model);
        templateModel.initModelDependencies(dict);
        templateModel.initStaticNeurons();

        model.setN(0);

        tokenizer = new WordTokenizer();
    }

    @Override
    protected void prepareInputs(Document doc) {
        tokenizer.tokenize(doc, (token, ref) ->
            doc.addToken(
                    dict.lookupInputToken(token),
                    ref
            )
        );
    }

    @Test
    public void testAnnealing() {
        process("a b", null, COUNTING, null);
        templateModel.initStaticNeurons();
        process("a b", null, TRAINING, null);
    }

    @Override
    protected SequenceModel getPhraseModel() {
        return templateModel;
    }

}
