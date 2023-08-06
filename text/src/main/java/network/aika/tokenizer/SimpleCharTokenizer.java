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
package network.aika.tokenizer;


import network.aika.meta.AbstractTemplateModel;
import network.aika.meta.Dictionary;
import network.aika.parser.Context;
import network.aika.text.Document;

/**
 *
 * @author Lukas Molzberger
 */
public class SimpleCharTokenizer implements Tokenizer {

    private Dictionary dict;

    public SimpleCharTokenizer(Dictionary dict) {
        this.dict = dict;
    }

    @Override
    public void tokenize(Document doc, Context context, TokenConsumer tokenConsumer) {
        int i = 0;
        int pos = 0;

        for(char c: doc.getContent().toCharArray()) {
            int j = i + 1;

            tokenConsumer.processToken(
                    dict.lookupInputToken("" + c),
                    pos,
                    i,
                    j
            );

            pos++;

            i = j;
        }
    }
}
