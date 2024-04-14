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
package network.aika.elements.activations.types;

import network.aika.Document;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.types.LatentRelationNeuron;


/**
 * @author Lukas Molzberger
 */
public class LatentRelationActivation extends Activation {

    private Activation fromAct;
    private Activation toAct;

    public LatentRelationActivation(int id, Document doc, LatentRelationNeuron n) {
        super(id, doc, n);
    }

    public Activation getFromAct() {
        return fromAct;
    }

    public void setFromAct(Activation fromAct) {
        this.fromAct = fromAct;
    }

    public Activation getToAct() {
        return toAct;
    }

    public void setToAct(Activation toAct) {
        this.toAct = toAct;
    }
}
