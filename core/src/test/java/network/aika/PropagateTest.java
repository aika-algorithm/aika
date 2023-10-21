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

import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.synapses.InputObjectSynapse;
import network.aika.Document;
import network.aika.text.Range;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Lukas Molzberger
 */
public class PropagateTest {

    private static final Logger log = LoggerFactory.getLogger(PropagateTest.class);

    @Test
    public void testPropagation() {
        Model m = new Model();
        m.setConfig(new Config());

        PatternNeuron in = new PatternNeuron(m).setLabel("IN");
        BindingNeuron out = new BindingNeuron(m).setLabel("OUT");

        new InputObjectSynapse()
                .setWeight(10.0)
                .link(in, out);

        out.setBias(1.0);

        Document doc = new Document(m, "test");
        doc.addToken(
                in,
                new TextReference(
                        new Range(0, 1),
                        new Range(0, 4)
                ),
                5.0
        );
        log.info("" + doc);

        doc.disconnect();
    }
}
