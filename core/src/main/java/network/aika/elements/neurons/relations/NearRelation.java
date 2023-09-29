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
public class NearRelation extends Relation {

    private int distance;

    public NearRelation() {
    }

    public NearRelation(Direction relDirection, int distance) {
        this.distance = distance;
    }

    @Override
    public int getRelationType() {
        return 4;
    }

    @Override
    public Stream<PatternActivation> evaluateLatentRelation(PatternActivation fromAct, Direction dir) {
        Document doc = (Document) fromAct.getThought();

        Range inputRange = fromAct.getGroundRef().getTokenPosRange();

        Range targetRange = new Range(
                inputRange.getBegin() - distance,
                inputRange.getEnd() + distance
        );

        return doc.getRelatedTokensByTokenPosition(INPUT, targetRange);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        out.writeInt(distance);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);
        distance = in.readInt();
    }

    @Override
    public String toString() {
        return "NearRelation: dist:" + distance;
    }
}
