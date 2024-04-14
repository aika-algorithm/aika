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

import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.RefType;
import network.aika.elements.typedef.model.TypeModel;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.FieldObjectDefinition;
import network.aika.fields.SumField;
import org.junit.jupiter.api.Test;

import static network.aika.fielddefs.Operators.func;
import static network.aika.utils.ToleranceUtils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class NegativeFeedbackLoopInstantiationTest {

    @Test
    public void testInstantiation() {
        TypeModel typeModel = new TypeModel();

        Model m = new Model();

        Neuron bn1 = typeModel.getBindingDef().getBindingNeuron().instantiate(m, RefType.NEURON);


        Document doc = new Document(m, "Bla");
        Activation bAct1 = typeModel.getBindingDef().getBindingActivation().instantiate(1, doc, bn1);
    }
}
