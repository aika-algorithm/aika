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
package network.aika.activations;

import network.aika.Config;
import network.aika.Document;
import network.aika.Model;
import network.aika.activations.model.TestTypeModel;
import network.aika.fields.defs.FieldDefinition;
import network.aika.neurons.Neuron;
import network.aika.neurons.Synapse;
import network.aika.typedefs.ActivationDefinition;
import network.aika.typedefs.NeuronDefinition;
import network.aika.typedefs.SynapseDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static network.aika.activations.TestBSTypes.A;
import static network.aika.neurons.RefType.NEURON_EXTERNAL;

/**
 *
 * @author Lukas Molzberger
 */
public class MinimalNetworkTest {


    @Test
    public void minNetworkTest() {
        TestTypeModel typeModel = new TestTypeModel();
        FieldDefinition bias = typeModel.getNeuron().getBias();
        FieldDefinition weight = typeModel.getNeuron().getWeight();
        FieldDefinition net = typeModel.getNeuron().getNet();

        typeModel.flattenTypeHierarchy();

        Model m = new Model(typeModel)
                .setConfig(new Config());

        System.out.println("Begin test\n");

        Neuron inputNeuron = typeModel
                .getNeuron()
                .getNeuron()
                .instantiate(m);
/*
        Neuron outputNeuron = typeModel
                .getNeuron()
                .getNeuron()
                .instantiate(m)
                .setFieldValue(bias, 1.0);

        Synapse synapse = typeModel
                .getNeuron()
                .getSynapse()
                .instantiate(inputNeuron, outputNeuron)
                .setFieldValue(weight, 10.0)
                .setPropagable(m, true);


        Document doc = new Document(m, 4);
        Activation iAct = doc.addToken(inputNeuron, A, 0)
                .setFieldValue(net, 5.0);

        Assertions.assertEquals(5.0, iAct.getFieldValue(net));

        doc.process();

        System.out.println("Dump results:");

        Activation oAct = doc.getActivationByNeuron(outputNeuron);

        Assertions.assertEquals(10.0, oAct.getFieldValue(net));

        doc.disconnect();*/
    }
}
