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
import network.aika.elements.activations.StateType;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.model.TypeModel;
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
        TypeModel tm = m.getTypeModel();
        Neuron bsN = tm.getPatternDef().getNeuron().instantiate(m, NEURON_EXTERNAL);
        Neuron in = tm.getBindingDef().getNeuron().instantiate(m, NEURON_EXTERNAL);
        Neuron on = tm.getPatternDef().getNeuron().instantiate(m, NEURON_EXTERNAL);
        Synapse s = tm.getPatternDef().getSynapse().instantiate()
                .setWeight(1.0)
                .link(in, on);

        Document doc = new Document(m, "Bla");

        Activation bs = tm.getPatternDef().getActivation().instantiate(0, doc, bsN);

        Activation iAct = tm.getBindingDef().getActivation().instantiate(1, doc, in);
        Activation oAct = tm.getPatternDef().getActivation().instantiate(2, doc, on);

        iAct.getBindingSignalSlot(Scope.INPUT).updateBindingSignal(bs, true);

        Link l = s.createLink(iAct, oAct);

        l.getInputValue().setValue(1.0);
        oAct.setNet(StateType.PRE_FEEDBACK, 1.0);

        l.getLinkUpdateStep(Direction.INPUT).process();
        l.getLinkUpdateStep(Direction.OUTPUT).process();

        Assertions.assertTrue(l.isInputSideActive());
        Assertions.assertTrue(l.isOutputSideActive());

        Assertions.assertTrue(oAct.getBindingSignalSlot(Scope.INPUT).isSet(bs));
    }

    @Test
    public void testPropagateBindingSignalOnBSArrived() {
        Model m = new Model();
        TypeModel tm = m.getTypeModel();

        Neuron bsN = tm.getPatternDef().getNeuron().instantiate(m, NEURON_EXTERNAL);
        Neuron in = tm.getBindingDef().getNeuron().instantiate(m, NEURON_EXTERNAL);
        Neuron on = tm.getPatternDef().getNeuron().instantiate(m, NEURON_EXTERNAL);
        Synapse s = tm.getPatternDef().getSynapse().instantiate()
                .setWeight(1.0)
                .link(in, on);

        Document doc = new Document(m, "Bla");

        Activation bs = tm.getPatternDef().getActivation().instantiate(0, doc, bsN);

        Activation iAct = tm.getBindingDef().getActivation().instantiate(1, doc, in);
        Activation oAct = tm.getPatternDef().getActivation().instantiate(2, doc, on);

        Link l = s.createLink(iAct, oAct);

        l.getInputValue().setValue(1.0);
        oAct.setNet(StateType.PRE_FEEDBACK, 1.0);

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
        TypeModel tm = m.getTypeModel();

        Neuron in = tm.getPatternDef().getNeuron().instantiate(m, NEURON_EXTERNAL);
        Neuron on = tm.getBindingDef().getNeuron().instantiate(m, NEURON_EXTERNAL);
        Synapse s = tm.getBindingDef().getInnerPositiveFeedbackSynapse().instantiate()
                .setWeight(1.0)
                .link(in, on);

        Document doc = new Document(m, "Bla");

        Activation iAct = tm.getPatternDef().getActivation().instantiate(0, doc, in);
        Activation bs = iAct;
        Activation oAct = tm.getBindingDef().getActivation().instantiate(1, doc, on);

        Link l = s.createLink(iAct, oAct);

        l.getInputValue().setValue(1.0);
        l.getLinkUpdateStep(Direction.INPUT).process();
        l.getLinkUpdateStep(Direction.OUTPUT).process();

        Assertions.assertTrue(l.isInputSideActive());
        Assertions.assertTrue(l.isOutputSideActive());

        Assertions.assertEquals(bs, oAct.getBindingSignal(Scope.SAME));
    }
}
