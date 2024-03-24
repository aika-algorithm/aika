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
package network.aika;



import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.suspension.LabelKey;
import network.aika.suspension.SuspensionCallback;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.synapses.types.InputObjectSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.text.TextReference;
import network.aika.utils.Writable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import static network.aika.elements.neurons.RefType.NEURON_EXTERNAL;
import static network.aika.elements.neurons.RefType.OTHER;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public class SuspensionTest {

    @Test
    public void testSuspendInputNeuron() {
        Model m = new Model(new DummySuspensionCallback());
        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.1)
                .setTrainingEnabled(false);
        m.setConfig(c);

        NeuronProvider inStrong = new PatternNeuron(m, NEURON_EXTERNAL).setLabel("IN Strong").getProvider();
        NeuronProvider inWeak = new PatternNeuron(m, NEURON_EXTERNAL).setLabel("IN Weak").getProvider();
        NeuronProvider out = new BindingNeuron(m, NEURON_EXTERNAL).setLabel("OUT").getProvider();
        out.getNeuron().setBias(1.0);

        Synapse sStrong = new InputObjectSynapse()
                .setWeight(10.0)
                .link(inStrong.getNeuron(), out.getNeuron())
                .adjustBias();

        Synapse sWeak = new InputObjectSynapse()
                .setWeight(0.5)
                .link(inWeak.getNeuron(), out.getNeuron())
                .adjustBias();

        Assertions.assertEquals(INPUT, sStrong.getStoredAt());
        Assertions.assertEquals(OUTPUT, sWeak.getStoredAt());

        inStrong.save();
        inWeak.save();
        out.save();

        // Reactivate
        inStrong = m.lookupNeuronProvider(inStrong.getId(), OTHER);

        Document doc = new Document(m, "test");
        doc.addToken(
                inStrong.getNeuron(),
                new TextReference(
                        new Range(0, 1),
                        new Range(0, 4)
                ),
                5.0
        );
    }


    public static class DummySuspensionCallback implements SuspensionCallback {
        public AtomicInteger currentId = new AtomicInteger(0);

        Map<Long, byte[]> storage = new TreeMap<>();
        private Map<LabelKey, Long> labels = new TreeMap<>();


        @Override
        public void prepareNewModel() {
        }

        @Override
        public void open() {
        }

        @Override
        public void close() {
        }

        @Override
        public long createId() {
            return currentId.addAndGet(1);
        }

        @Override
        public long getCurrentId() {
            return currentId.get();
        }

        @Override
        public void store(Long id, String label, Writable customData, byte[] data) {
            storage.put(id, data);
        }

        @Override
        public void remove(Long id) {
            storage.remove(id);
        }

        @Override
        public byte[] retrieve(Long id) {
            return storage.get(id);
        }

        @Override
        public Collection<Long> getAllIds() {
            return storage.keySet();
        }

        @Override
        public Long getIdByLabel(String label, Long templateId) {
            return labels.get(new LabelKey(label, templateId));
        }

        @Override
        public void putLabel(String label, Long templateId, Long id) {
            labels.put(new LabelKey(label, templateId), id);
        }

        @Override
        public void removeLabel(String label, Long templateId) {
            labels.remove(new LabelKey(label, templateId));
        }

        @Override
        public void loadIndex(Model m) {
        }

        @Override
        public void saveIndex(Model m) {
        }
    }
}
