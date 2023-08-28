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

import network.aika.Model;
import network.aika.elements.activations.PatternActivation;
import network.aika.enums.direction.Direction;
import network.aika.text.Document;
import network.aika.text.Range;

import java.util.stream.Stream;

import static network.aika.text.Slot.BEGIN;


/**
 *
 * @author Lukas Molzberger
 */
public class EqualsRelationNeuron extends LatentRelationNeuron {

    public static EqualsRelationNeuron lookupRelation(Model m) {
        return m.lookupNeuronByLabel("Equals Rel.: ", l ->
                createEqualsRelationNeuron(m, l)
        );
    }

    private static EqualsRelationNeuron createEqualsRelationNeuron(Model m, String l) {
        EqualsRelationNeuron n = new EqualsRelationNeuron();
        n.addProvider(m);
        n.setLabel(l);
        n.setAllowTraining(false);

        return n;
    }

    @Override
    public Stream<PatternActivation> evaluateLatentRelation(PatternActivation fromAct, Direction vDir) {
        Document doc = (Document) fromAct.getThought();
        Range r = fromAct.getTokenPosRange();

        return doc.getRelatedTokensByTokenPosition(BEGIN, r)
                .filter(act -> fromAct != act)
                .filter(act ->
                        r.equals(act.getTokenPosRange())
                );
    }

    @Override
    public Direction getDirection() {
        return null;
    }
}
