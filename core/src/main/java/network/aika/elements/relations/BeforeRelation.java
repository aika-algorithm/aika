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
import network.aika.text.TextReference;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Stream;

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;


/**
 *
 * @author Lukas Molzberger
 */
public class BeforeRelation extends Relation {

    private Direction relDirection;

    private Range offsetRange;

    BeforeRelation() {
    }

    public BeforeRelation(Direction relDirection, Range offsetRange) {
        this.offsetRange = offsetRange;
        this.relDirection = relDirection;
    }

    @Override
    public int getRelationType() {
        return 1;
    }

    @Override
    public Stream<Activation> evaluateLatentRelation(TextReference ref, Activation fromAct, PreActivation<?> toPreAct, Direction dir) {
        Direction toSlot = dir.combine(relDirection);
        Direction fromSlot = toSlot.invert();

        Range inputRange = ref.getTokenPosRange();
        long fromPos = inputRange.getPosition(fromSlot);

        Range targetRange = new Range(
                fromPos + getRelBegin(dir),
                fromPos + getRelEnd(dir)
        );

        return toPreAct.getRelatedTokensByTokenPosition(toSlot, targetRange).toList().stream();
    }

    private long getRelBegin(Direction dir) {
        return dir == INPUT ?
                -offsetRange.getEnd() :
                offsetRange.getBegin();
    }

    private long getRelEnd(Direction dir) {
        return dir == INPUT ?
                -offsetRange.getBegin() :
                offsetRange.getEnd();
    }

    public Range getOffsetRange() {
        return offsetRange;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        out.writeBoolean(relDirection == INPUT);
        offsetRange.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);
        relDirection = in.readBoolean() ? INPUT : OUTPUT;
        offsetRange = Range.read(in, m);
    }

    @Override
    public String toString() {
        return "BeforeRelation: " + offsetRange + " " + relDirection;
    }
}
