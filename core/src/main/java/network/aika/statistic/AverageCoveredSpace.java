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


import network.aika.Model;
import network.aika.text.Range;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.text.Range.length;
import static network.aika.utils.Utils.doubleToString;

/**
 *
 * @author Lukas Molzberger
 */
public class AverageCoveredSpace implements Writable {

    private long n;
    private long coveredSpace;

    public void count(Range r) {
        n++;
        coveredSpace += length(r);
    }

    public double getAvgCoveredSpace() {
        if(n == 0)
            return 0;

        return (double) coveredSpace / (double) n;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(n);
        out.writeLong(coveredSpace);
    }

    public static AverageCoveredSpace read(DataInput in, Model m) throws Exception {
        AverageCoveredSpace acs = new AverageCoveredSpace();
        acs.readFields(in, m);
        return acs;
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        n = in.readLong();
        coveredSpace = in.readLong();
    }

    @Override
    public String toString() {
        return "avg:" + doubleToString(getAvgCoveredSpace()) + " n:" + n;
    }
}
