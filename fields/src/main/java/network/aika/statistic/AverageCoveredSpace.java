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
package network.aika.statistic;


import network.aika.Range;
import network.aika.fields.Field;
import network.aika.fields.FieldObject;
import network.aika.fields.link.FieldLink;
import network.aika.utils.FieldWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import static network.aika.utils.ToleranceUtils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class AverageCoveredSpace<O extends FieldObject> extends Field<O, FieldLink> implements FieldWritable {

    private long n;
    private long coveredSpace;

    public void count(Range r) {
        n++;
        coveredSpace += Range.length(r);

        double uv = (double) coveredSpace / (double) n;

        triggerUpdate(uv - value);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(n);
        out.writeLong(coveredSpace);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        n = in.readLong();
        coveredSpace = in.readLong();
    }

    @Override
    public void addInput(FieldLink fl) {

    }

    @Override
    public void removeInput(FieldLink fl) {

    }

    @Override
    public Collection getInputs() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void receiveUpdate(FieldLink fl, double u) {

    }
}
