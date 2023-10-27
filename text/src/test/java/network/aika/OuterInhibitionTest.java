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
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.neurons.*;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.inhibitoryloop.InhibitorySynapse;
import network.aika.elements.synapses.inhibitoryloop.NegativeFeedbackSynapse;
import network.aika.text.Range;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Lukas Molzberger
 */
public class OuterInhibitionTest {

    private static final Logger log = LoggerFactory.getLogger(OuterInhibitionTest.class);

    @Test
    public void testPropagationInput() {
        Model m = new Model();
        Config c = new Config()
                .setAlpha(0.99)
                .setLearnRate(0.01)
                .setTrainingEnabled(true);
        m.setConfig(c);

        PatternNeuron in = new PatternNeuron(m).setLabel("IN");
        InhibitoryNeuron inhib = new InhibitoryNeuron(m).setLabel("I");

        BindingNeuron na = addBindingNeuronOuter(m,  "A", 1.0, in, inhib);
        BindingNeuron nb = addBindingNeuronOuter(m, "B", 1.5, in, inhib);
        BindingNeuron nc = addBindingNeuronOuter(m, "C", 1.2, in, inhib);

        Document doc = new Document(m, "test");
        doc.addToken(in, new TextReference(new Range(0, 1), new Range(0, 4)), 5.0);

        doc.postProcessing();
        doc.updateModel();

        log.info("" + doc);

        SortedSet<BindingActivation> nbActs = nb.getActivations(doc);
        Activation nbAct = nbActs.stream().findFirst().orElse(null);

        assertTrue(nbAct.getValue().getValue() > 0.38);

        doc.disconnect();
    }

    private static BindingNeuron addBindingNeuronOuter(Model m, String label, double bias, PatternNeuron in, InhibitoryNeuron inhib) {
        BindingNeuron bn = new BindingNeuron(m).setLabel(label);

        new InputObjectSynapse()
                .setWeight(10.0)
                .link(in, bn)
                .adjustBias();
        new NegativeFeedbackSynapse()
                .setWeight(-20.0)
                .link(inhib, bn);

        new InhibitorySynapse()
                .setWeight(1.0)
                .link(bn, inhib);

        bn.setBias(bias);

        return bn;
    }
}
