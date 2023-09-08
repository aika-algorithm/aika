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
import network.aika.text.Slot;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Stream;

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.text.Slot.BEGIN;
import static network.aika.text.Slot.END;


/**
 *
 * @author Lukas Molzberger
 */
public class BeforeRelationNeuron extends LatentRelationNeuron {

    private Slot fromSlot;
    private Slot toSlot;

    private int beginOffset;
    private int endOffset;

    public static BeforeRelationNeuron createBeforeRelationNeuron(Model m, int rangeBegin, int rangeEnd, String l) {
        BeforeRelationNeuron n = new BeforeRelationNeuron(m);
        n.setLabel(l);

        n.beginOffset = rangeBegin;
        n.endOffset = rangeEnd;

        n.fromSlot = n.beginOffset < 0 ? BEGIN : END;
        n.toSlot = n.endOffset < 0 ? END : BEGIN;

        n.setAllowTraining(false);
        return n;
    }

    public BeforeRelationNeuron(Model m) {
        super(m);
    }

    @Override
    public Stream<PatternActivation> evaluateLatentRelation(PatternActivation fromAct, Direction dir) {
        Document doc = (Document) fromAct.getThought();

        Slot fromSlot = getFromSlot(dir);
        Range inputRange = fromAct.getTokenPosRange();
        Range targetRange = new Range(
                inputRange.getPosition(fromSlot) + getRelBegin(dir),
                inputRange.getPosition(fromSlot) + getRelEnd(dir)
        );

        return doc.getRelatedTokensByTokenPosition(getToSlot(dir), targetRange);
    }

    private Slot getFromSlot(Direction dir) {
        return dir == OUTPUT ?
                fromSlot :
                toSlot;
    }

    private Slot getToSlot(Direction dir) {
        return dir == OUTPUT ?
                toSlot :
                fromSlot;
    }

    private int getRelBegin(Direction dir) {
        return dir == INPUT ?
                -beginOffset :
                endOffset;
    }

    private int getRelEnd(Direction dir) {
        return dir == INPUT ?
                -endOffset :
                beginOffset;
    }

    public Direction getDirection() {
        return beginOffset > 0 ?
                INPUT :
                OUTPUT;
    }

    public int getBeginOffset() {
        return beginOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }


    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeInt(fromSlot.ordinal());
        out.writeInt(toSlot.ordinal());

        out.writeInt(beginOffset);
        out.writeInt(endOffset);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        fromSlot = Slot.values()[in.readInt()];
        toSlot = Slot.values()[in.readInt()];

        beginOffset = in.readInt();
        endOffset = in.readInt();
    }
}
