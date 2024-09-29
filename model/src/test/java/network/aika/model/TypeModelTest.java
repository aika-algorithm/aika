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
package network.aika.model;

import network.aika.Config;
import network.aika.Document;
import network.aika.Model;
import network.aika.Range;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;

import static network.aika.elements.neurons.RefType.NEURON_EXTERNAL;

/**
 *
 * @author Lukas Molzberger
 */
public class TypeModelTest {


    @Test
    public void testTypeModel() {
        Model m = new Model();
        m.setConfig(new Config());

        TypeModel typeModel = new TypeModel(new Config());

        System.out.println(typeModel.dumpModel());

        System.out.println("Begin test\n");

        Neuron inputNeuron = typeModel
                .getPattern()
                .getNeuron()
                .instantiate(m, NEURON_EXTERNAL)
                .setLabel("IN");

        Neuron outputNeuron = typeModel
                .getBinding()
                .getNeuron()
                .instantiate(m, NEURON_EXTERNAL)
                .setLabel("OUT")
                .setBias(1.0);

        Synapse synapse = typeModel
                .getBinding()
                .getInputObjectSynapse()
                .instantiate(inputNeuron, outputNeuron)
                .setWeight(10.0)
                .setPropagable(true);
/*
        System.out.println("Dump model:");
        System.out.println(inputNeuron.dumpObject(0));
        System.out.println(outputNeuron.dumpObject(0));
        System.out.println(synapse.dumpObject(0));
        System.out.println();
*/
//        typeModel.typesAsJSON();

        Document doc = new Document(m, "test");
        Activation act = doc.addToken(
                inputNeuron,
                new TextReference(
                        new Range(0, 1),
                        new Range(0, 4)
                ),
                5.0
        );

        doc.process();

        System.out.println("Dump results:");
        System.out.println(doc.dumpActivations());

        doc.disconnect();
    }
}
