
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


import network.aika.Document;
import network.aika.Context;
import network.aika.text.Range;
import network.aika.text.TextReference;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Lukas Molzberger
 */
public class WordTokenizer implements Tokenizer {

    public static final String DEFAULT_SEPARATOR_CHARS = " \n\r?!:;,.()[]-_\"/\\";

    private Set<Character> separatorCharSet = new HashSet<>();

    public WordTokenizer() {
        this(DEFAULT_SEPARATOR_CHARS);
    }

    public WordTokenizer(String separatorChars) {
        setSeparators(separatorChars);
    }

    public void setSeparators(String separatorChars) {
        char[] sc = separatorChars.toCharArray();
        for(int i = 0; i < separatorChars.length(); i++) {
            separatorCharSet.add(sc[i]);
        }
    }

    @Override
    public void tokenize(Document doc, TokenConsumer tc) {
        int pos = 0;
        int begin = 0;

        boolean lastWithinToken = false;
        String content = doc.getContent();
        int l = content.length();
        for(int i = 0; i <= l; i++) {
            char c = i < l ? content.charAt(i) : ' ';

            boolean withinToken = !separatorCharSet.contains(c);

            if(!lastWithinToken && withinToken)
                begin = i;
            else if(lastWithinToken && !withinToken) {
                processTokenIntern(tc, content, begin, i, pos);
                pos++;
            } else if(lastWithinToken && forcedSplit(i, doc.getContext())) {
                processTokenIntern(tc, content, begin, i, pos);
                begin = i;
                pos++;
            }

            lastWithinToken = withinToken;
        }
    }

    protected boolean forcedSplit(int i, Context context) {
        return false;
    }

    private void processTokenIntern(TokenConsumer tc, String content, int begin, int end, int pos) {
        String token = content.substring(begin, end);
        tc.processToken(
                token,
                new TextReference(
                        new Range(pos, pos + 1),
                        new Range(begin, end)
                )
        );
    }
}
