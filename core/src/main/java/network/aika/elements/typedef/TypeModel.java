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
package network.aika.elements.typedef;


import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.bsslots.BSSlotDefinition;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.SumField;

import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.NeuronType.BINDING;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_INPUT;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_SAME_FEEDBACK;
import static network.aika.fielddefs.Operators.func;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class TypeModel {

    public void initTypeModel() {
        StateTypeDefinition state = new StateTypeDefinition("state");

        FieldDefinition net = new FieldDefinition(SumField.class, state, "net");
/*
        net.addListener("onFired", (fl, u) ->
                updateFiredStep(fl)
        );
*/
        FieldDefinition value = func(
                this,
                "value = f(net)",
                TOLERANCE,
                net,
                x -> act.getActivationFunction().f(x)
        );
        value.setQueued(state, INFERENCE);


        NeuronTypeDefinition bindingNeuron = new NeuronTypeDefinition(
                "BindingNeuron",
                ConjunctiveNeuron.class
        )
                .setNeuronType(BINDING)
                .setActivationFunction(RECTIFIED_HYPERBOLIC_TANGENT)
                .setBindingSignalSlots(SINGLE_INPUT, SINGLE_SAME_FEEDBACK);

        ActivationTypeDefinition bindingActivation = new ActivationTypeDefinition(
                "BindingActivation",
                ConjunctiveActivation.class
        );
    }
}
