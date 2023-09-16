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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Stream;

import static network.aika.enums.direction.Direction.INPUT;


/**
 *
 * @author Lukas Molzberger
 */
public class ContainsRelationNeuron extends LatentRelationNeuron {

    private Direction relationDir;

    public ContainsRelationNeuron(Model m) {
        super(m);
    }

    public ContainsRelationNeuron(Model m, String l, Direction relDir) {
       super(m);

        setLabel(l);
        setAllowTraining(false);

        relationDir = relDir;
    }

    @Override
    public Stream<PatternActivation> evaluateLatentRelation(PatternActivation fromAct, Direction vDir) {
        Document doc = (Document) fromAct.getThought();
        Range r = fromAct.getGroundRef().getTokenPosRange();
        Direction dir = relationDir.combine(vDir);

        return (
                dir == Direction.OUTPUT ?
                        doc.getRelatedTokensByTokenPosition(INPUT, r) :
                        doc.getRelatedTokensByTokenPosition(INPUT, new Range(0, r.getBegin()))
        )
                .filter(act -> fromAct != act)
                .filter(act ->
                        contains(r, act.getGroundRef().getTokenPosRange(), dir)
                );
    }

    private boolean contains(Range a, Range b, Direction dir) {
        return dir == Direction.OUTPUT ?
                a.contains(b) :
                b.contains(a);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        relationDir.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        relationDir = Direction.read(in);
    }
}
