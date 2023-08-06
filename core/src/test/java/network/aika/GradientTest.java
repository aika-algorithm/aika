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

import network.aika.elements.neurons.PatternNeuron;
import network.aika.text.Document;
import org.junit.jupiter.api.Test;

import java.util.Arrays;


/**
 *
 * @author Lukas Molzberger
 */
public class GradientTest {

    @Test
    public void gradientAndInduction2() {
        Model m = new Model();

        m.setN(912);

        Document doc = new Document(m, "A B ");
        doc.setConfig(
                new Config()
                .setAlpha(0.99)
                .setLearnRate(0.1)
                .setTrainingEnabled(true)
        );
/*
        Neuron nA = m.getNeuron("A");
        nA.setFrequency(53.0);
        nA.getSampleSpace().setN(299);
        nA.getSampleSpace().setOffset(899l);


        Neuron nB = m.getNeuron("B");
        nB.setFrequency(10.0);
        nB.getSampleSpace().setN(121);
        nB.getSampleSpace().setOffset(739l);
*/

        processDoc(m, doc);

        doc.postProcessing();
        doc.disconnect();
    }


    @Test
    public void gradientAndInduction3() {
        Model m = new Model();

        m.setN(912);

        Document doc = new Document(m, "A B C ");
        doc.setConfig(
                new Config()
                        .setAlpha(0.99)
                        .setLearnRate(0.1)
                        .setTrainingEnabled(true)
        );

        processDoc(m, doc);

        PatternNeuron nA = m.getNeuronByLabel("A");
        setStatistic(nA, 53.0,299,899l);

        PatternNeuron nB = m.getNeuronByLabel("B");
        setStatistic(nB, 10.0, 121, 739l);

        PatternNeuron nC = m.getNeuronByLabel("C");
        setStatistic(nC, 30.0, 234, 867l);

        doc.postProcessing();
        doc.updateModel();
        doc.disconnect();
    }


    @Test
    public void gradientAndInduction2With2Docs() {
        Model m = new Model();

        m.setN(912);
     //   t.BINDING_TEMPLATE.getBias().receiveUpdate(-0.32);

        Document doc1 = new Document(m, "A B ");
        doc1.setConfig(
                new Config()
                        .setAlpha(0.99)
                        .setLearnRate(0.1)
                        .setTrainingEnabled(true)
        );
        processDoc(m, doc1);

        PatternNeuron nA = m.getNeuronByLabel("A");
        setStatistic(nA, 53.0, 299, 899l);

        PatternNeuron nB = m.getNeuronByLabel("B");
        setStatistic(nB, 10.0, 121, 739l);

        doc1.postProcessing();
        doc1.updateModel();
        doc1.disconnect();

        Document doc2 = new Document(m, "A C ");
        doc2.setConfig(
                new Config()
                        .setAlpha(0.99)
                        .setLearnRate(0.1)
                        .setTrainingEnabled(true)
        );
        processDoc(m, doc2);

        PatternNeuron nC = m.getNeuronByLabel("C");
        setStatistic(nC, 30.0, 234, 867l);

        doc2.postProcessing();
        doc2.updateModel();
        doc2.disconnect();

        System.out.println();
    }

    private void processDoc(Model m, Document doc) {
        //TODO
    }

    public static void setStatistic(PatternNeuron n, double frequency, int N, long lastPosition) {
        n.setFrequency(frequency);
        n.getSampleSpace().setN(N);
        n.getSampleSpace().setLastPosition(lastPosition);
    }
}
