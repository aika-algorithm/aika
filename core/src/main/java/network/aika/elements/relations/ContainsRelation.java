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
package network.aika.elements.relations;

import network.aika.Model;
import network.aika.elements.PreActivation;
import network.aika.elements.activations.Activation;
import network.aika.enums.direction.Direction;
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
public class ContainsRelation extends Relation {

    private Direction relationDir;

    ContainsRelation() {
    }

    public ContainsRelation(Direction relDir) {
        relationDir = relDir;
    }

    @Override
    public int getRelationType() {
        return 2;
    }

    @Override
    public Stream<Activation> evaluateLatentRelation(Activation fromAct, PreActivation<?> toPreAct, Direction vDir) {
        Range r = fromAct.getTextReference().getTokenPosRange();
        Direction dir = relationDir.combine(vDir);

        return (
                dir == Direction.OUTPUT ?
                        toPreAct.getRelatedTokensByTokenPosition(INPUT, r) :
                        toPreAct.getRelatedTokensByTokenPosition(INPUT, new Range(0, r.getBegin()))
        )
                .filter(act -> fromAct != act)
                .filter(act ->
                        contains(r, act.getTextReference().getTokenPosRange(), dir)
                ).toList().stream();
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
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);
        relationDir = Direction.read(in);
    }

    @Override
    public String toString() {
        return "ContainsRelation: " + " " + relationDir;
    }
}
