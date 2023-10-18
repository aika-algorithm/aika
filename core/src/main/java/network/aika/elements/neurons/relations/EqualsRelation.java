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
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.enums.direction.Direction;
import network.aika.text.Document;
import network.aika.text.Range;

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
public class EqualsRelation extends Relation {

    boolean compareBegin;
    boolean compareEnd;

    EqualsRelation() {
    }

    public EqualsRelation(boolean compareBegin, boolean compareEnd) {
        this.compareBegin = compareBegin;
        this.compareEnd = compareEnd;

        assert compareBegin || compareEnd;
    }

    @Override
    public int getRelationType() {
        return 3;
    }

    public boolean isCompareBegin() {
        return compareBegin;
    }

    public boolean isCompareEnd() {
        return compareEnd;
    }

    @Override
    public Stream<ConjunctiveActivation> evaluateLatentRelation(ConjunctiveActivation fromAct, Direction dir) {
        Document doc = (Document) fromAct.getThought();
        Range r = fromAct.getTextReference().getTokenPosRange();

        return doc.getRelatedTokensByTokenPosition(compareBegin ? INPUT : OUTPUT, r)
                .filter(act -> fromAct != act)
                .filter(act ->
                        compare(r, act.getTextReference().getTokenPosRange(), dir)
                );
    }

    public boolean compare(Range ra, Range rb, Direction dir) {
        if(compareBegin) {
            if(ra.getBegin() != rb.getBegin())
                return false;
        }

        if(compareEnd) {
            if(ra.getEnd() != rb.getEnd())
                return false;
        }

        return true;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        out.writeBoolean(compareBegin);
        out.writeBoolean(compareEnd);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);
        compareBegin = in.readBoolean();
        compareEnd = in.readBoolean();
    }

    @Override
    public String toString() {
        return "EqualsRelation: " + " compareBegin:" + compareBegin + " compareEnd:" + compareEnd;
    }
}
