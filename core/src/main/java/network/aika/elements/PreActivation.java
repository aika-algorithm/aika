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
package network.aika.elements;


import network.aika.Document;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.Neuron;
import network.aika.enums.direction.Direction;
import network.aika.Range;
import network.aika.text.TextReference;
import network.aika.text.TokenPositionKey;

import java.util.*;
import java.util.stream.Stream;

import static network.aika.elements.neurons.RefType.NEURON;
import static network.aika.text.TextReference.getTPBegin;
import static network.aika.text.TextReference.getTPEnd;

/**
 *
 * @author Lukas Molzberger
 */
public class PreActivation {

    private Document doc;

    private Neuron neuron;

    private SortedSet<Activation> activations = new TreeSet<>();

    private NavigableMap<TokenPositionKey, Activation> tokenPosBeginIndex = new TreeMap<>();

    private NavigableMap<TokenPositionKey, Activation> tokenPosEndIndex = new TreeMap<>();


    public PreActivation(Document doc, Neuron neuron) {
        this.doc = doc;
        this.neuron = neuron;

        neuron.getProvider().increaseRefCount(NEURON);

        doc.register(neuron, this);
    }

    public SortedSet<Activation> getActivations() {
        return activations;
    }

    public Neuron getNeuron() {
        return neuron;
    }

    public NavigableMap<TokenPositionKey, Activation> getTokenPosBeginIndex() {
        return tokenPosBeginIndex;
    }

    public NavigableMap<TokenPositionKey, Activation> getTokenPosEndIndex() {
        return tokenPosEndIndex;
    }

    public void addActivation(Activation act) {
        activations.add(act);
    }

    public void updateTextReference(Activation act, TextReference oldTextReference, TextReference newTextReference) {
        updateTextReference(tokenPosBeginIndex, act, getTPBegin(oldTextReference), getTPBegin(newTextReference));
        updateTextReference(tokenPosEndIndex, act, getTPEnd(oldTextReference), getTPEnd(newTextReference));
    }

    private void updateTextReference(NavigableMap<TokenPositionKey, Activation> index, Activation tokenAct, Long oldPos, Long newPos) {
        if(oldPos != null) {
            if (oldPos == newPos)
                return;

            index.remove(new TokenPositionKey(oldPos, tokenAct.getId()));
        }

        if(newPos != null)
            index.put(new TokenPositionKey(newPos, tokenAct.getId()), tokenAct);
    }

    public Stream<Activation> getRelatedTokensByTokenPosition(Direction slot, Range r) {
        return getPositionIndex(slot)
                .subMap(
                        new TokenPositionKey(r.getBegin(), Integer.MIN_VALUE),
                        new TokenPositionKey(r.getEnd(), Integer.MAX_VALUE)
                )
                .values()
                .stream();
    }

    private NavigableMap<TokenPositionKey, Activation> getPositionIndex(Direction slot) {
        return slot == Direction.OUTPUT ?
                tokenPosEndIndex :
                tokenPosBeginIndex;
    }

    public Document getDocument() {
        return doc;
    }

    public void disconnect() {
        neuron.getProvider().decreaseRefCount(NEURON);
    }
}
