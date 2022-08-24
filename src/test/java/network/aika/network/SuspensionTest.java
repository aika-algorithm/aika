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


import network.aika.Document;
import network.aika.Model;
import network.aika.Provider;
import network.aika.SuspensionHook;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.relation.Relation;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static network.aika.neuron.INeuron.Type.EXCITATORY;
import static network.aika.neuron.INeuron.Type.INPUT;
import static network.aika.neuron.Synapse.OUTPUT;
import static network.aika.neuron.relation.Relation.*;

/**
 *
 * @author Lukas Molzberger
 */
public class SuspensionTest {


    @Test
    public void testSuspendInputNeuron() {
        Model m = new Model(new DummySuspensionHook(), 1);

        Neuron n = m.createNeuron("A", INPUT);
        n.get().getInputNode().suspend(Provider.SuspensionMode.SAVE);
        n.suspend(Provider.SuspensionMode.SAVE);

        int id = n.getId();


        // Reactivate
        n = m.lookupNeuron(id);

        Document doc = new Document(m, "Bla");
        n.addInput(doc, 0, 1);
    }


    @Test
    public void testSuspendAndNeuron() {
        Model m = new Model(new DummySuspensionHook(), 1);

        Neuron inA = m.createNeuron("A", INPUT);
        Neuron inB = m.createNeuron("B", INPUT);

        int idA = inA.getId();
        int idB = inB.getId();

        Neuron nC = Neuron.init(m.createNeuron("C", EXCITATORY),
                5.0,
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
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(BEGIN_EQUALS),
                new Relation.Builder()
                        .setFrom(1)
                        .setTo(OUTPUT)
                        .setRelation(END_EQUALS)
        );


        Neuron outD = Neuron.init(m.createNeuron("D", EXCITATORY),
                6.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(nC)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        m.suspendAll(Provider.SuspensionMode.SAVE);

        Assert.assertTrue(outD.isSuspended());

        // Reactivate

        Document doc = processTestDocument(m, idA, idB);

        Assert.assertFalse(outD.getActivations(doc, true).collect(Collectors.toList()).isEmpty());
    }




    public static class DummySuspensionHook implements SuspensionHook {
        public AtomicInteger currentId = new AtomicInteger(0);

        Map<Integer, byte[]> storage = new TreeMap<>();
        private Map<String, Integer> labels = new TreeMap<>();


        @Override
        public int getNewId() {
            return currentId.addAndGet(1);
        }

        @Override
        public void store(Integer id, byte[] data) {
            storage.put(id, data);
        }

        @Override
        public byte[] retrieve(Integer id) {
            return storage.get(id);
        }

        @Override
        public void remove(Integer id) {
            storage.remove(id);
        }

        @Override
        public Integer getIdByLabel(String label) {
            return labels.get(label);
        }

        @Override
        public void putLabel(String label, Integer id) {
            labels.put(label, id);
        }

        @Override
        public void removeLabel(String label) {
            labels.remove(label);
        }

        @Override
        public Iterable<Integer> getAllNodeIds() {
            return storage.keySet();
        }

        @Override
        public void loadIndex() {

        }

        @Override
        public void storeIndex() {

        }
    }




    @Test
    public void testSuspendNegativeNeuron() {
        Model m = new Model(new DummySuspensionHook(), 1);

        Neuron inA = m.createNeuron("A", INPUT);
        Neuron inB = m.createNeuron("B", INPUT);

        int idA = inA.getId();
        int idB = inB.getId();

        Neuron nC = Neuron.init(m.createNeuron("C", EXCITATORY),
                5.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(inA)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Synapse.Builder()
                        .setSynapseId(1)
                        .setNeuron(inB)
                        .setWeight(-100.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(EQUALS)
        );


        Neuron outD = Neuron.init(m.createNeuron("D", EXCITATORY),
                6.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(nC)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );

        m.suspendAll(Provider.SuspensionMode.SAVE);

        Assert.assertTrue(outD.isSuspended());

        // Reactivate

        Document doc = new Document(m, "Bla");

        inA = m.lookupNeuron(idA);
        inA.addInput(doc, 0, 1);

        inB = m.lookupNeuron(idB);
        Assert.assertEquals(2, nC.get().getInputSynapses().size());
    }


    @Test
    public void testSuspendAndNeuronWithDeletion() {
        Model m = new Model(new DummySuspensionHook(), 1);

        Neuron inA = m.createNeuron("A", INPUT);
        Neuron inB = m.createNeuron("B", INPUT);

        int idA = inA.getId();
        int idB = inB.getId();

        Neuron nC = initNeuronC(m, inA, inB);

        Neuron outD = Neuron.init(m.createNeuron("D", EXCITATORY),
                6.0,
                new Synapse.Builder()
                        .setSynapseId(0)
                        .setNeuron(nC)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new Relation.Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(EQUALS)
        );


        m.suspendAll(Provider.SuspensionMode.SAVE);

        Assert.assertTrue(outD.isSuspended());

        // Reactivate

        Document doc = processTestDocument(m, idA, idB);

        Assert.assertTrue(outD.getActivations(doc, true).collect(Collectors.toList()).isEmpty());

        Neuron nCNew = initNeuronC(m, inA, inB);

        Assert.assertNotEquals(nC.getId(), nCNew.getId());

        doc = processTestDocument(m, idA, idB);

        Assert.assertFalse(nCNew.getActivations(doc, true).collect(Collectors.toList()).isEmpty());
    }

    public Document processTestDocument(Model m, int idA, int idB) {
        Neuron inA;
        Neuron inB;
        Document doc = new Document(m, "Bla");

        inA = m.lookupNeuron(idA);
        inA.addInput(doc, 0, 1);

        inB = m.lookupNeuron(idB);
        inB.addInput(doc, 1, 2);

        doc.process();

        System.out.println(doc.activationsToString());
        return doc;
    }

    public Neuron initNeuronC(Model m, Neuron inA, Neuron inB) {
        Neuron nC = Neuron.init(m.createNeuron("C", EXCITATORY),
                5.0,
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
                new Builder()
                        .setFrom(0)
                        .setTo(1)
                        .setRelation(END_TO_BEGIN_EQUALS),
                new Builder()
                        .setFrom(0)
                        .setTo(OUTPUT)
                        .setRelation(BEGIN_EQUALS),
                new Builder()
                        .setFrom(1)
                        .setTo(OUTPUT)
                        .setRelation(END_EQUALS)
        );

        return nC;
    }
}
