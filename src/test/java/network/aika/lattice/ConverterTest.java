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
package network.aika.lattice;

import network.aika.Document;
import network.aika.Model;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.lattice.Converter;
import network.aika.neuron.INeuron;
import network.aika.neuron.relation.Relation;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.neuron.INeuron.Type.*;
import static network.aika.neuron.Synapse.OUTPUT;
import static network.aika.neuron.relation.Relation.EQUALS;

/**
 *
 * @author Lukas Molzberger
 */
public class ConverterTest {


    @Test
    public void testConverter() {
        Model m = new Model();

        Neuron inA = m.createNeuron("A", INPUT);
        Neuron inB = m.createNeuron("B", INPUT);
        Neuron inC = m.createNeuron("C", INPUT);
        Neuron inD = m.createNeuron("D", INPUT);

        Neuron out = Neuron.init(m.createNeuron("ABCD", EXCITATORY),
                0.5,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inA)
                        .setWeight(4.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inB)
                        .setWeight(3.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inC)
                        .setWeight(2.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(3)
                        .setNeuron(inD)
                        .setWeight(1.0)
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
                        .setTo(3)
                        .setRelation(EQUALS),
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

        System.out.println(out.get().getInputNode().get().logicToString());
        Assert.assertEquals(1, out.get().getInputNode().get().andParents.size());

        out.get().setBias(1.5);

        out.get().commit(out.get().getInputSynapses());
        Converter.convert(new Document(m, ""), out.get(), out.get().getInputSynapses());

        System.out.println(out.get().getInputNode().get().logicToString());

        Assert.assertEquals(1, out.get().getInputNode().get().andParents.size());
    }


    @Test
    public void testConverter1() {
        Model m = new Model();

        Neuron inA = m.createNeuron("A", INPUT);
        Neuron inB = m.createNeuron("B", INPUT);
        Neuron inC = m.createNeuron("C", INPUT);
        Neuron inD = m.createNeuron("D", INPUT);

        Neuron out = Neuron.init(m.createNeuron("ABCD", INHIBITORY),
                -5.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inA)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inB)
                        .setWeight(1.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inC)
                        .setWeight(1.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(3)
                        .setNeuron(inD)
                        .setWeight(1.0)
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
                        .setTo(3)
                        .setRelation(EQUALS),
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

        System.out.println(out.get().getInputNode().get().logicToString());
        Assert.assertEquals(1, inA.get().getOutputNode().get().orChildren.size());
    }


    @Test
    public void testConverter2() {
        Model m = new Model();

        Neuron inA = m.createNeuron("A", INPUT);
        Neuron inB = m.createNeuron("B", INPUT);
        Neuron inC = m.createNeuron("C", INPUT);
        Neuron inD = m.createNeuron("D", INPUT);
        Neuron inE = m.createNeuron("E", INPUT);

        Neuron out = Neuron.init(m.createNeuron("ABCD", EXCITATORY),
                3.5,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inA)
                        .setWeight(5.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inB)
                        .setWeight(5.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inC)
                        .setWeight(2.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(3)
                        .setNeuron(inD)
                        .setWeight(2.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(4)
                        .setNeuron(inE)
                        .setWeight(0.5)
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
                        .setFrom(1)
                        .setTo(3)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(4)
                        .setRelation(EQUALS),
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
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(4)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        System.out.println(out.get().getInputNode().get().logicToString());

        Assert.assertEquals(2, out.get().getInputNode().get().andParents.size());


        out.getSynapseById(3).updateDelta(null, -1.5, 0.0);

        out.get().commit(out.get().getInputSynapses());
        Converter.convert( new Document(m, ""), out.get(), out.get().getInputSynapses());
        System.out.println(out.get().getInputNode().get().logicToString());
        Assert.assertEquals(1, out.get().getInputNode().get().andParents.size());
    }


    @Test
    public void testConverter3() {
        Model m = new Model();

        Neuron inA = m.createNeuron("A", INPUT);
        Neuron inB = m.createNeuron("B", INPUT);
        Neuron inC = m.createNeuron("C", INPUT);
        Neuron inD = m.createNeuron("D", INPUT);

        Neuron out = Neuron.init(m.createNeuron("ABCD", EXCITATORY),
                5.5,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inA)
                        .setWeight(50.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inB)
                        .setWeight(3.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inC)
                        .setWeight(2.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(3)
                        .setNeuron(inD)
                        .setWeight(1.0)
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

        System.out.println(out.get().getInputNode().get().logicToString());
        Assert.assertEquals(3, out.get().getInputNode().get().andParents.size());

    }


    @Test
    public void testDuplicates() {
        Model m = new Model();

        Neuron in = m.createNeuron("IN", INPUT);

        Neuron out = Neuron.init(m.createNeuron("OUT", EXCITATORY),
                5.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(in)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(in)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        Document doc = new Document(m, "IN");

        in.addInput(doc, 0, 2);

        Assert.assertFalse(out.getActivations(doc, false).collect(Collectors.toList()).isEmpty());
    }


    @Test
    public void testOnlyOptionalInputs() {
        Model model = new Model();

        Neuron inA = model.createNeuron("A", INPUT);
        Neuron inB = model.createNeuron("B", INPUT);
        Neuron inC = model.createNeuron("C", INPUT);

        Neuron testNeuron = model.createNeuron("Test", EXCITATORY);

        Neuron.init(testNeuron,
                2.1,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inA)
                        .setWeight(2.0),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inB)
                        .setWeight(2.0),
                new Synapse.Builder()
                        .setSynapseId(2)
                        .setNeuron(inC)
                        .setWeight(2.0),
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
                        .setRelation(EQUALS)
        );


        Document doc = new Document(model, "Bla");
        inB.addInput(doc, 0, 3);
        inC.addInput(doc, 0, 3);

        doc.process();

        Assert.assertNotNull(testNeuron.getActivation(doc, 0, 3, false));
    }
}
