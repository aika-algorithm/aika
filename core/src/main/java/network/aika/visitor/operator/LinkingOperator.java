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
package network.aika.visitor.operator;

import network.aika.Document;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;

import static network.aika.elements.synapses.Synapse.getLatentLink;

/**
 * @author Lukas Molzberger
 */
public abstract class LinkingOperator implements Operator {

    protected Activation sourceAct;

    protected Synapse targetSyn;

    public LinkingOperator(Activation sourceAct, Synapse targetSyn) {
        this.sourceAct = sourceAct;
        this.targetSyn = targetSyn;
    }

    @Override
    public Run getRun() {
        return Run.PRIMARY;
    }

    public abstract Link checkAndLink(Activation act);

    public Activation getSourceAct() {
        return sourceAct;
    }

    public Synapse getTargetSyn() {
        return targetSyn;
    }

    public static Link latentLink(Activation actA, Synapse synA, Activation actB, Synapse synB) {
        Link linkA = getLatentLink(synA, synB, actA, actB);
        if (linkA != null)
            return linkA;

        Document doc = actA.getDocument();
        Activation oAct = synA.getOutput().createActivation(doc);

        return synA.createAndInitLink(actA, oAct);
    }
}
