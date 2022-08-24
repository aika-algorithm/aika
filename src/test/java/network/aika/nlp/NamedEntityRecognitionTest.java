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
package network.aika.nlp;


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
import java.util.stream.Collectors;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.neuron.INeuron.Type.*;
import static network.aika.neuron.Synapse.OUTPUT;
import static network.aika.neuron.relation.Relation.*;

/**
 *
 * @author Lukas Molzberger
 */
public class NamedEntityRecognitionTest {

    // This test demonstrates the recognition of the words 'jackson cook' as forename and surname
    // even though each individual word would have been recognized as city or profession
    // respectively.
    @Test
    public void testNamedEntityRecognition() {
        Model m = new Model(null, 1); // number of threads

        Neuron forenameCategory = m.createNeuron("C-forename", INHIBITORY, LIMITED_RECTIFIED_LINEAR_UNIT);
        Neuron surnameCategory = m.createNeuron("C-surname", INHIBITORY, LIMITED_RECTIFIED_LINEAR_UNIT);
        Neuron inhibitingN = m.createNeuron("INHIB", INHIBITORY, LIMITED_RECTIFIED_LINEAR_UNIT);


        // The word input neurons which do not yet possess a relational id.
        HashMap<String, Neuron> inputNeurons = new HashMap<>();

        String[] words = new String[] {
                "mr.", "jackson", "cook", "was", "born", "in", "new", "york"
        };
        for(String word: words) {
            Neuron in = m.createNeuron("W-" + word, INPUT);

            inputNeurons.put(word, in);
        }

        // The entity neurons represent the concrete meanings of the input words.
        // The helper function 'initAndNeuron' computes the required bias for a
        // conjunction of the inputs.
        Neuron cookSurnameEntity = Neuron.init(
                m.createNeuron("E-cook (surname)", EXCITATORY),
                6.0, // adjusts the bias
                new Synapse.Builder() // Requires the word to be recognized
                        .setSynapseId(0)
                        .setNeuron(inputNeurons.get("cook"))
                        .setWeight(10.0)
                        // This input requires the input activation to have an
                        // activation value of at least 0.9
                        .setRecurrent(false),
                new Synapse.Builder() // The previous word needs to be a forename
                        .setSynapseId(1)
                        .setNeuron(forenameCategory)
                        .setWeight(10.0)
                        .setRecurrent(true), // this input is a positive feedback loop

                // This neuron may be suppressed by the E-cook (profession) neuron, but there is no
                // self suppression taking place even though 'E-cook (surname)' is also contained
                // in the inhibitingN.
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inhibitingN)
                        .setWeight(-100.0)
                        .setRecurrent(true), // this input is a negative feedback loop
                new Relation.Builder()  // references the previous word
                        .setFrom(1)
                        .setTo(0)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(0)
                        .setRelation(OVERLAPS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        Neuron cookProfessionEntity = Neuron.init(
                m.createNeuron("E-cook (profession)", EXCITATORY),
                5.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inputNeurons.get("cook"))
                        .setWeight(10.0),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inhibitingN)
                        .setWeight(-100.0)
                        .setRecurrent(true),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(0)
                        .setRelation(OVERLAPS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        Neuron jacksonForenameEntity = Neuron.init(
                m.createNeuron("E-jackson (forename)", EXCITATORY),
                6.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inputNeurons.get("jackson"))
                        .setWeight(10.0),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(surnameCategory)
                        .setWeight(10.0)
                        .setRecurrent(true),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inhibitingN)
                        .setWeight(-100.0)
                        .setRecurrent(true),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(0)
                        .setRelation(BEGIN_TO_END_EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(0)
                        .setRelation(OVERLAPS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        Neuron jacksonCityEntity = Neuron.init(
                m.createNeuron("E-jackson (city)", EXCITATORY),
                5.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inputNeurons.get("jackson"))
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inhibitingN)
                        .setWeight(-100.0)
                        .setRecurrent(true),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(0)
                        .setRelation(OVERLAPS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        Neuron.init(
                forenameCategory,
                0.0,
                new Synapse.Builder() // In this example there is only one forename considered.
                        .setSynapseId(0)
                        .setNeuron(jacksonForenameEntity)
                        .setWeight(1.0),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );
        Neuron.init(
                surnameCategory,
                0.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(cookSurnameEntity)
                        .setWeight(1.0),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        Neuron.init(
                inhibitingN,
                0.0,
                new Synapse.Builder().setNeuron(cookProfessionEntity)
                        .setSynapseId(0)
                        .setWeight(1.0),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(cookSurnameEntity)
                        .setWeight(1.0),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(jacksonCityEntity)
                        .setWeight(1.0),
                new Synapse.Builder()
                        .setSynapseId(3)
                        .setNeuron(jacksonForenameEntity)
                        .setWeight(1.0),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(2)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(3)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );


        // Now that the model is complete, start processing an actual text.
        Document doc = new Document(m, "mr. jackson cook was born in new york ");

        int i = 0;
        for(String w: doc.getContent().split(" ")) {
            int j = i + w.length();

            // Feed the individual words as inputs into the network.
            inputNeurons.get(w).addInput(doc, i, j + 1);
            i = j + 1;
        }

        // Search for the best interpretation of this text.
        doc.process();

        System.out.println(doc.activationsToString());
        System.out.println();

        System.out.println("Activations of the Surname Category:");
        for(Activation act: surnameCategory.getActivations(doc, true).collect(Collectors.toList())) {
            System.out.print(act.slotsToString() + " ");
            System.out.print(act.getLabel() + " ");
            System.out.print(act.getValue());
        }

        Assert.assertFalse(jacksonForenameEntity.getActivations(doc, true).collect(Collectors.toList()).isEmpty());
        Assert.assertFalse(cookSurnameEntity.getActivations(doc, true).collect(Collectors.toList()).isEmpty());
    }

}
