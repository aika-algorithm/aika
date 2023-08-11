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
import network.aika.elements.activations.TokenActivation;
import network.aika.enums.direction.Direction;
import network.aika.text.Document;
import network.aika.text.Range;
import network.aika.text.Slot;

import java.util.stream.Stream;

import static network.aika.text.Slot.BEGIN;
import static network.aika.text.Slot.END;


/**
 *
 * @author Lukas Molzberger
 */
public class ContainsRelationNeuron extends LatentRelationNeuron {


    private Direction relationDir;


    public static ContainsRelationNeuron lookupRelation(Model m, Direction relDir) {
        return m.lookupNeuronByLabel("Contains Rel.: ", l ->
                createContainsRelationNeuron(m, l, relDir)
        );
    }

    private static ContainsRelationNeuron createContainsRelationNeuron(Model m, String l, Direction relDir) {
        ContainsRelationNeuron n = new ContainsRelationNeuron();
        n.addProvider(m);
        n.setLabel(l);
        n.setAllowTraining(false);

        n.relationDir = relDir;

        return n;
    }

    @Override
    public Stream<TokenActivation> evaluateLatentRelation(TokenActivation fromAct, Direction vDir) {
        Document doc = (Document) fromAct.getThought();
        Range r = fromAct.getTokenPosRange();
        Direction dir = relationDir.combine(vDir);

        return (
                dir == Direction.OUTPUT ?
                        doc.getRelatedTokensByTokenPosition(BEGIN, r) :
                        doc.getRelatedTokensByTokenPosition(BEGIN, new Range(0, r.getBegin()))
        )
                .filter(act -> fromAct != act)
                .filter(act ->
                        contains(r, act.getTokenPosRange(), dir)
                );
    }

    private boolean contains(Range a, Range b, Direction dir) {
        return dir == Direction.OUTPUT ?
                a.contains(b) :
                b.contains(a);
    }

    @Override
    public Direction getDirection() {
        return Direction.INPUT;
    }
}
