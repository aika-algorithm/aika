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
package network.aika.elements.neurons.relations;

import network.aika.Thought;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.enums.direction.Direction;
import network.aika.fields.QueueSumField;
import network.aika.fields.MultiInputField;
import network.aika.elements.activations.LatentRelationActivation;
import network.aika.elements.activations.TokenActivation;


import java.util.stream.Stream;

import static network.aika.steps.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class LatentRelationNeuron extends BindingNeuron {


    public abstract Stream<TokenActivation> evaluateLatentRelation(TokenActivation fromOriginAct, Direction dir);

    @Override
    protected MultiInputField initBias() {
        return new QueueSumField(this, TRAINING, "bias", TOLERANCE);
    }

    @Override
    public LatentRelationActivation createActivation(Thought t) {
        return new LatentRelationActivation(t.createActivationId(), t, this);
    }
}