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
package network.aika.text;

import network.aika.Model;
import network.aika.Thought;
import network.aika.elements.Timestamp;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.TokenActivation;
import network.aika.elements.neurons.TokenNeuron;

import java.util.*;
import java.util.stream.Stream;

import static network.aika.elements.Timestamp.MIN;
import static network.aika.elements.Timestamp.NOT_SET;
import static network.aika.text.Slot.END;


/**
 * The {@code Document} class represents a single document which may be either used for processing a text or as
 * training input. A document consists of the raw text, the interpretations and the activations.
 *
 * @author Lukas Molzberger
 */
public class Document extends Thought {

    private final StringBuilder content;

    private NavigableMap<TokenPositionKey, TokenActivation> tokenPosBeginIndex = new TreeMap<>();

    private NavigableMap<TokenPositionKey, TokenActivation> tokenPosEndIndex = new TreeMap<>();

    public Document(Model model, String content) {
        super(model);
        this.content = new StringBuilder();
        if(content != null) {
            this.content.append(content);
        }
    }

    public void registerTokenActivation(TokenActivation tokenAct) {
        if(tokenAct.getTokenPosRange() != null) {
            tokenPosBeginIndex.put(new TokenPositionKey(tokenAct.getTokenPosRange().getBegin(), tokenAct.getId()), tokenAct);
            tokenPosEndIndex.put(new TokenPositionKey(tokenAct.getTokenPosRange().getBegin(), tokenAct.getId()), tokenAct);
        }
    }

    public Stream<TokenActivation> getRelatedTokensByTokenPosition(Slot slot, Range r) {
        return getPositionIndex(slot)
                .subMap(
                        new TokenPositionKey(r.getBegin(), Integer.MIN_VALUE),
                        new TokenPositionKey(r.getEnd(), Integer.MAX_VALUE)
                )
                .values()
                .stream();
    }

    private NavigableMap<TokenPositionKey, TokenActivation> getPositionIndex(Slot slot) {
        NavigableMap<TokenPositionKey, TokenActivation> tokenPosIndex = slot == END ?
                tokenPosEndIndex :
                tokenPosBeginIndex;
        return tokenPosIndex;
    }

    public void append(String txt) {
        content.append(txt);
    }

    public char charAt(int i) {
        return content.charAt(i);
    }

    public String getContent() {
        return content.toString();
    }

    public int length() {
        return content.length();
    }

    public String getTextSegment(Range range) {
        if(range == null)
            return "";

        Range r = range.limit(new Range(0, length()));
        return content.substring((int) r.getBegin(), (int) r.getEnd());
    }

    public static String getText(Activation<?> act) {
        return ((Document)act.getThought()).getTextSegment(act.getCharRange());
    }

    public TokenActivation addToken(TokenNeuron n, Integer pos, int begin, int end) {
        return new TokenActivation(
                createActivationId(),
                pos != null ? new Range(pos, pos) : null,
                new Range(begin, end),
                this, n
        );
    }

    @Override
    public Timestamp getCreated() {
        return MIN;
    }

    @Override
    public Timestamp getFired() {
        return NOT_SET;
    }

    @Override
    public Thought getThought() {
        return this;
    }

    public String docToString() {
        StringBuilder sb = new StringBuilder(content);
        sb.append("\n");
        sb.append(super.activationsToString());
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" Content: ");
        sb.append(
                content.substring(0, Math.min(content.length(), 100))
                        .replaceAll("[\\n\\r\\s]+", " ")
        );
        return sb.toString();
    }
}
