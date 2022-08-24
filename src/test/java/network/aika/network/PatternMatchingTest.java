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
package network.aika.network;


import network.aika.ActivationFunction;
import network.aika.Document;
import network.aika.Model;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.INeuron;
import network.aika.neuron.relation.Relation;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static network.aika.neuron.INeuron.Type.EXCITATORY;
import static network.aika.neuron.INeuron.Type.INPUT;
import static network.aika.neuron.Synapse.OUTPUT;
import static network.aika.neuron.relation.Relation.*;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternMatchingTest {

    @Test
    public void testPatternMatching1() {
        Model m = new Model();

        Map<Character, Neuron> inputNeurons = new HashMap<>();

        // Create an input neuron and a recurrent neuron for every letter in this example.
        for(char c: new char[] {'a', 'b', 'c', 'd', 'e'}) {
            Neuron in = m.createNeuron(c + "", INPUT);

            inputNeurons.put(c, in);
        }

        // Create a pattern neuron with the relational neurons as input. The numbers that are
        // given in the inputs are the recurrent ids (relativeRid) which specify the relative position
        // of the inputs relative to each other. The following flag specifies whether this relativeRid
        // is relative or absolute.
        Neuron pattern = Neuron.init(
                m.createNeuron("BCD", EXCITATORY),
                1.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inputNeurons.get('b'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inputNeurons.get('c'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inputNeurons.get('d'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(2)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(OUTPUT)
                        .setRelation(END_EQUALS)
        );


        // Create a simple text document.
        Document doc = new Document(m, "a b c d e ");

        // Then add the characters
        int wordPos = 0;
        for(int i = 0; i < doc.length(); i++) {
            char c = doc.getContent().charAt(i);
            if(c != ' ') {
                inputNeurons.get(c).addInput(doc, i, i + 2);
            } else {
                wordPos++;
            }
        }

        // Computes the selected option
        doc.process();

        Assert.assertEquals(1, pattern.get().size(doc));


        System.out.println("Output activation:");
        INeuron n = pattern.get();
        for(Activation act: n.getActivations(doc, false).collect(Collectors.toList())) {
            System.out.println("Text Range: " + act.slotsToString());
            System.out.println("Neuron: " + act.getLabel());
            System.out.println();
        }

        System.out.println("All activations:");
        System.out.println(doc.activationsToString());
        System.out.println();
    }


    @Test
    public void testPatternMatching2() {
        Model m = new Model();

        Map<Character, Neuron> inputNeurons = new HashMap<>();

        // Create an input neuron and a recurrent neuron for every letter in this example.
        for(char c: new char[] {'a', 'b', 'c', 'd', 'e', 'f'}) {
            Neuron in = m.createNeuron(c + "", INPUT);

            inputNeurons.put(c, in);
        }

        // Create a pattern neuron with the relational neurons as input. The numbers that are
        // given in the inputs are the recurrent ids (relativeRid) which specify the relative position
        // of the inputs relative to each other. The following flag specifies whether this relativeRid
        // is relative or absolute.
        Neuron pattern = Neuron.init(
                m.createNeuron("BCDE", EXCITATORY),
                5.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inputNeurons.get('b'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inputNeurons.get('c'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inputNeurons.get('d'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(3)
                        .setNeuron(inputNeurons.get('e'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(2)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(3)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(3)
                        .setTo(OUTPUT)
                        .setRelation(END_EQUALS)
        );


        // Create a simple text document.
        Document doc = new Document(m, "a b c d e ");

        // Then add the characters
        int wordPos = 0;
        for(int i = 0; i < doc.length(); i++) {
            char c = doc.getContent().charAt(i);
            if(c != ' ') {
                inputNeurons.get(c).addInput(doc, i, i + 2);
            } else {
                wordPos++;
            }
        }

        // Computes the best interpretation
        doc.process();

        Assert.assertEquals(1, pattern.getActivations(doc, false).collect(Collectors.toList()).size());


        System.out.println("Output activation:");
        INeuron n = pattern.get();
        for(Activation act: n.getActivations(doc, false).collect(Collectors.toList())) {
            System.out.println("Text Range: " + act.slotsToString());
            System.out.println("Neuron: " + act.getLabel());
            System.out.println();
        }

        System.out.println("All activations:");
        System.out.println(doc.activationsToString());
        System.out.println();
    }



    @Test
    public void testPatternMatching3() {
        Model m = new Model();

        Neuron inA = m.createNeuron("A", INPUT);
        Neuron inB = m.createNeuron("B", INPUT);
        Neuron inC = m.createNeuron("C", INPUT);


        Neuron pattern = Neuron.init(
                m.createNeuron("ABC", EXCITATORY),
                1.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inA)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inB)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inC)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(2)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(0)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(OUTPUT)
                        .setRelation(END_EQUALS)
        );


        Document doc = new Document(m, "X");

        inA.addInput(doc, 0, 1);
        inB.addInput(doc, 0, 1);
        inC.addInput(doc, 0, 1);

        // Computes the selected option
        doc.process();

        Assert.assertEquals(1, pattern.get().size(doc));


        System.out.println("Output activation:");
        INeuron n = pattern.get();
        for(Activation act: n.getActivations(doc, false).collect(Collectors.toList())) {
            System.out.println("Text Range: " + act.slotsToString());
            System.out.println("Neuron: " + act.getLabel());
            System.out.println();
        }

        System.out.println("All activations:");
        System.out.println(doc.activationsToString());
        System.out.println();
    }

    @Test
    public void testPatternMatching4() {
        Model m = new Model();

        Neuron inA = m.createNeuron("A", INPUT);
        Neuron inB = m.createNeuron("B", INPUT);
        Neuron inC = m.createNeuron("C", INPUT);
        Neuron inD = m.createNeuron("D", INPUT);


        Neuron pattern = Neuron.init(
                m.createNeuron("ABCD", EXCITATORY),
                1.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inA)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inB)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inC)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(3)
                        .setNeuron(inD)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(2)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(3)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(2)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(3)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(3)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(3)
                        .setTo(OUTPUT)
                        .setRelation(END_EQUALS)
        );


        Document doc = new Document(m, "X");

        inA.addInput(doc, 0, 1);
        inB.addInput(doc, 0, 1);
        inC.addInput(doc, 0, 1);
        inD.addInput(doc, 0, 1);

        // Computes the selected option
        doc.process();

        Assert.assertEquals(1, pattern.get().size(doc));


        System.out.println("Output activation:");
        INeuron n = pattern.get();
        for(Activation act: n.getActivations(doc, false).collect(Collectors.toList())) {
            System.out.println("Text Range: " + act.slotsToString());
            System.out.println("Neuron: " + act.getLabel());
            System.out.println();
        }

        System.out.println("All activations:");
        System.out.println(doc.activationsToString());
        System.out.println();
    }

    @Test
    public void testPatternMatching5() {
        Model m = new Model();

        Neuron word = m.createNeuron("Word", INPUT);
        Map<Character, Neuron> inputNeurons = new HashMap<>();

        // Create an input neuron and a recurrent neuron for every letter in this example.
        for(char c: new char[] {'a', 'b', 'c', 'd', 'e'}) {
            Neuron in = m.createNeuron(c + "", INPUT);

            inputNeurons.put(c, in);
        }

        // Create a pattern neuron with the relational neurons as input. The numbers that are
        // given in the inputs are the recurrent ids (relativeRid) which specify the relative position
        // of the inputs relative to each other. The following flag specifies whether this relativeRid
        // is relative or absolute.
        Neuron pattern = Neuron.init(
                m.createNeuron("BCD", EXCITATORY),
                1.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inputNeurons.get('b'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inputNeurons.get('c'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inputNeurons.get('d'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(3)
                        .setNeuron(word)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(2)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(OUTPUT)
                        .setRelation(END_EQUALS),
                new Relation.Builder()
                        .setFrom(3)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );


        // Create a simple text document.
        Document doc = new Document(m, "a b c d e ");

        word.addInput(doc, 2, 8);

        // Then add the characters
        int wordPos = 0;
        for(int i = 0; i < doc.length(); i++) {
            char c = doc.getContent().charAt(i);
            if(c != ' ') {
                inputNeurons.get(c).addInput(doc, i, i + 2);
            } else {
                wordPos++;
            }
        }

        // Computes the selected option
        doc.process();

        Assert.assertEquals(1, pattern.get().size(doc));


        System.out.println("Output activation:");
        INeuron n = pattern.get();
        for(Activation act: n.getActivations(doc, false).collect(Collectors.toList())) {
            System.out.println("Text Range: " + act.slotsToString());
            System.out.println("Neuron: " + act.getLabel());
            System.out.println();
        }

        System.out.println("All activations:");
        System.out.println(doc.activationsToString());
        System.out.println();
    }

    @Test
    public void testPatternMatching6() {
        Model m = new Model();

        Neuron word = m.createNeuron("Word", INPUT);
        Map<Character, Neuron> inputNeurons = new HashMap<>();

        // Create an input neuron and a recurrent neuron for every letter in this example.
        for(char c: new char[] {'a', 'b', 'c', 'd', 'e'}) {
            Neuron in = m.createNeuron(c + "", INPUT);

            inputNeurons.put(c, in);
        }

        // Create a pattern neuron with the relational neurons as input. The numbers that are
        // given in the inputs are the recurrent ids (relativeRid) which specify the relative position
        // of the inputs relative to each other. The following flag specifies whether this relativeRid
        // is relative or absolute.
        Neuron pattern = Neuron.init(
                m.createNeuron("BCD", EXCITATORY),
                5.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inputNeurons.get('b'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inputNeurons.get('c'))
                        .setWeight(2.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inputNeurons.get('d'))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(3)
                        .setNeuron(word)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(2)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(OUTPUT)
                        .setRelation(END_EQUALS),
                new Relation.Builder()
                        .setFrom(3)
                        .setTo(0)
                        .setRelation(BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(3)
                        .setTo(2)
                        .setRelation(END_EQUALS)
        );


        // Create a simple text document.
        Document doc = new Document(m, "a b c d e ");

        word.addInput(doc, 2, 8);

        // Then add the characters
        int wordPos = 0;
        for(int i = 0; i < doc.length(); i++) {
            char c = doc.getContent().charAt(i);
            if(c != ' ') {
                inputNeurons.get(c).addInput(doc, i, i + 2);
            } else {
                wordPos++;
            }
        }

        // Computes the selected option
        doc.process();

        Assert.assertEquals(1, pattern.get().size(doc));


        System.out.println("Output activation:");
        INeuron n = pattern.get();
        for(Activation act: n.getActivations(doc, false).collect(Collectors.toList())) {
            System.out.println("Text Range: " + act.slotsToString());
            System.out.println("Neuron: " + act.getLabel());
            System.out.println();
        }

        System.out.println("All activations:");
        System.out.println(doc.activationsToString());
        System.out.println();
    }

}
