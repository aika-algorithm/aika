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

import network.aika.elements.activations.StateType;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.types.InnerPositiveFeedbackLink;
import network.aika.elements.links.types.PatternLink;
import network.aika.elements.neurons.RefType;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.types.InnerPositiveFeedbackSynapse;
import network.aika.elements.synapses.types.PatternSynapse;
import network.aika.enums.Scope;
import network.aika.enums.direction.Direction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static network.aika.elements.neurons.RefType.NEURON_EXTERNAL;


/**
 * @author Lukas Molzberger
 *
 */
public class PropagateBindingSignalTest {

    @Test
    public void testPropagateBindingSignalOnLinkStateChange() {
        Model m = new Model();
        PatternNeuron bsN = new PatternNeuron(m, NEURON_EXTERNAL);
        BindingNeuron in = new BindingNeuron(m, NEURON_EXTERNAL);
        PatternNeuron on = new PatternNeuron(m, NEURON_EXTERNAL);
        PatternSynapse s = new PatternSynapse()
                .setWeight(1.0)
                .link(in, on);

        Document doc = new Document(m, "Bla");

        PatternActivation bs = new PatternActivation(0, doc, bsN);

        BindingActivation iAct = new BindingActivation(1, doc, in);
        PatternActivation oAct = new PatternActivation(2, doc, on);

        iAct.getBindingSignalSlot(Scope.INPUT).updateBindingSignal(bs, true);

        PatternLink l = s.createLink(iAct, oAct);

        l.getInputValue().setValue(1.0);
        oAct.getState(StateType.PRE_FEEDBACK).setNet(1.0);

        l.getLinkUpdateStep(Direction.INPUT).process();
        l.getLinkUpdateStep(Direction.OUTPUT).process();

        Assertions.assertTrue(l.isInputSideActive());
        Assertions.assertTrue(l.isOutputSideActive());

        Assertions.assertTrue(oAct.getBindingSignalSlot(Scope.INPUT).isSet(bs));
    }

    @Test
    public void testPropagateBindingSignalOnBSArrived() {
        Model m = new Model();
        PatternNeuron bsN = new PatternNeuron(m, NEURON_EXTERNAL);
        BindingNeuron in = new BindingNeuron(m, NEURON_EXTERNAL);
        PatternNeuron on = new PatternNeuron(m, NEURON_EXTERNAL);
        PatternSynapse s = new PatternSynapse()
                .setWeight(1.0)
                .link(in, on);

        Document doc = new Document(m, "Bla");

        PatternActivation bs = new PatternActivation(0, doc, bsN);

        BindingActivation iAct = new BindingActivation(1, doc, in);
        PatternActivation oAct = new PatternActivation(2, doc, on);

        PatternLink l = s.createLink(iAct, oAct);

        l.getInputValue().setValue(1.0);
        oAct.getState(StateType.PRE_FEEDBACK).setNet(1.0);

        l.getLinkUpdateStep(Direction.INPUT).process();
        l.getLinkUpdateStep(Direction.OUTPUT).process();

        Assertions.assertTrue(l.isInputSideActive());
        Assertions.assertTrue(l.isOutputSideActive());

        Assertions.assertFalse(oAct.getBindingSignalSlot(Scope.INPUT).isSet(bs));

        iAct.getBindingSignalSlot(Scope.INPUT).updateBindingSignal(bs, true);

        Assertions.assertTrue(oAct.getBindingSignalSlot(Scope.INPUT).isSet(bs));
    }

    @Test
    public void testPropagateBindingSignalOnLinkCreation() {
        Model m = new Model();
        PatternNeuron in = new PatternNeuron(m, NEURON_EXTERNAL);
        BindingNeuron on = new BindingNeuron(m, NEURON_EXTERNAL);
        InnerPositiveFeedbackSynapse s = new InnerPositiveFeedbackSynapse()
                .setWeight(1.0)
                .link(in, on);

        Document doc = new Document(m, "Bla");

        PatternActivation iAct = new PatternActivation(0, doc, in);
        PatternActivation bs = iAct;
        BindingActivation oAct = new BindingActivation(1, doc, on);

        InnerPositiveFeedbackLink l = s.createLink(iAct, oAct);

        l.getInputValue().setValue(1.0);
        l.getLinkUpdateStep(Direction.INPUT).process();
        l.getLinkUpdateStep(Direction.OUTPUT).process();

        Assertions.assertTrue(l.isInputSideActive());
        Assertions.assertTrue(l.isOutputSideActive());

        Assertions.assertEquals(bs, oAct.getBindingSignal(Scope.SAME));
    }
}
