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

import network.aika.elements.Timestamp;
import network.aika.elements.activations.StateType;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.types.InnerPositiveFeedbackLink;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.slots.SynapseInputSlot;
import network.aika.elements.synapses.types.InnerPositiveFeedbackSynapse;
import network.aika.fields.Field;
import network.aika.fields.SumField;
import network.aika.fields.link.ArgumentFieldLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static network.aika.elements.neurons.RefType.NEURON_EXTERNAL;
import static network.aika.fields.link.ArgumentFieldLink.linkAndConnect;

/**
 * @author Lukas Molzberger
 *
 */
public class ConjunctiveSynapseSlotTest {

    @Test
    public void testSynapseInputSlot() {
        Model m = new Model();
        BindingNeuron on = new BindingNeuron(m, NEURON_EXTERNAL);
        PatternNeuron in = new PatternNeuron(m, NEURON_EXTERNAL);
        InnerPositiveFeedbackSynapse s = new InnerPositiveFeedbackSynapse();
        s.setInput(in);
        s.setOutput(on);

        Document doc = new Document(m, "Bla");

        BindingActivation oAct1 = new BindingActivation(0, doc, on);
        BindingActivation oAct2 = new BindingActivation(1, doc, on);
        oAct2.getState(StateType.PRE_FEEDBACK).setFired(new Timestamp(2));
        oAct1.getState(StateType.PRE_FEEDBACK).setFired(Timestamp.NOT_SET);

        PatternActivation iAct = new PatternActivation(2, doc, in);

        SynapseInputSlot slot = new SynapseInputSlot(iAct, s);

        InnerPositiveFeedbackLink l1 = new InnerPositiveFeedbackLink(s, iAct, oAct1);
        InnerPositiveFeedbackLink l2 = new InnerPositiveFeedbackLink(s, iAct, oAct2);

        Field outputNet = new SumField(iAct, "outputNet", 0.0);
        ArgumentFieldLink fl1 = linkAndConnect(outputNet, l1, slot);
        ArgumentFieldLink fl2 = linkAndConnect(outputNet, l2, slot);

        int r = slot.getComparator().compare(fl1, fl2);
        Assertions.assertEquals(-1, r);

        Assertions.assertEquals(fl2, slot.getMaxInput());

        Assertions.assertTrue(slot.getSelectedLink().getOutput() == oAct2);
    }
}
