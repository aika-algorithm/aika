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
package network.aika.text;

import network.aika.Model;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.text.Range.getBegin;
import static network.aika.text.Range.getEnd;

/**
 *
 * @author Lukas Molzberger
 */
public class TextReference implements Writable {

    private Range tokenPosRange;
    private Range charRange;

    public TextReference(int charBegin, int charEnd) {
        this.tokenPosRange = null;
        this.charRange = new Range(charBegin, charEnd);
    }

    public TextReference(int pos, int charBegin, int charEnd) {
        this.tokenPosRange = new Range(pos, pos + 1);
        this.charRange = new Range(charBegin, charEnd);
    }

    public TextReference(Range tokenPosRange, Range charRange) {
        this.tokenPosRange = tokenPosRange;
        this.charRange = charRange;
    }

    private TextReference() {
    }

    public Range getTokenPosRange() {
        return tokenPosRange;
    }

    public Range getCharRange() {
        return charRange;
    }

    public static Long getTPBegin(TextReference gr) {
        return gr != null ? getBegin(gr.tokenPosRange) : null;
    }

    public static Long getTPEnd(TextReference gr) {
        return gr != null ? getEnd(gr.tokenPosRange) : null;
    }

    public boolean equals(TextReference gr) {
        return tokenPosRange.equals(gr.tokenPosRange) &&
                charRange.equals(gr.charRange);
    }

    public static TextReference join(TextReference a, TextReference b) {
        if(a == null)
            return b;
        if(b == null)
            return a;

        return new TextReference(
                Range.join(a.tokenPosRange, b.tokenPosRange),
                Range.join(a.charRange, b.charRange)
        );
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(tokenPosRange != null);
        if(tokenPosRange != null)
            tokenPosRange.write(out);

        out.writeBoolean(charRange != null);
        if(charRange != null)
            charRange.write(out);
    }

    public static TextReference read(DataInput in, Model m) throws Exception {
        TextReference gr = new TextReference();
        gr.readFields(in, m);
        return gr;
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        if(in.readBoolean())
            tokenPosRange = Range.read(in, m);

        if(in.readBoolean())
            charRange = Range.read(in, m);
    }

    public String toString() {
        return "TPR:" + tokenPosRange + " CR:" + charRange;
    }
}
