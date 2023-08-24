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
import network.aika.text.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;

import static network.aika.enums.Scope.INPUT;
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

        TokenNeuron in = new TokenNeuron().init(m, "IN");
        OuterInhibitoryNeuron inhib = new OuterInhibitoryNeuron().init(m, "I");

        BindingNeuron na = addBindingNeuronOuter(m,  "A", 1.0, in, inhib);
        BindingNeuron nb = addBindingNeuronOuter(m, "B", 1.5, in, inhib);
        BindingNeuron nc = addBindingNeuronOuter(m, "C", 1.2, in, inhib);

        Document doc = new Document(m, "test");
        doc.addToken(in, 0, 0, 4, 5.0);

        doc.postProcessing();
        doc.updateModel();

        log.info("" + doc);

        SortedSet<BindingActivation> nbActs = nb.getActivations(doc);
        Activation nbAct = nbActs.stream().findFirst().orElse(null);

        assertTrue(nbAct.getValue().getValue() > 0.38);

        doc.disconnect();
    }

    private static BindingNeuron addBindingNeuronOuter(Model m, String label, double bias, TokenNeuron in, OuterInhibitoryNeuron inhib) {
        BindingNeuron bn = new BindingNeuron().init(m, label);

        new InputObjectSynapse()
                .setWeight(10.0)
                .init(in, bn)
                .adjustBias();
        new OuterNegativeFeedbackSynapse()
                .setWeight(-20.0)
                .init(inhib, bn);

        new OuterInhibitorySynapse()
                .setWeight(1.0)
                .init(bn, inhib);

        bn.setBias(bias);

        return bn;
    }
}
