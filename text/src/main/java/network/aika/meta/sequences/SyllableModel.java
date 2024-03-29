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
package network.aika.meta.sequences;

import network.aika.Model;
import network.aika.meta.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Lukas Molzberger
 */
public class SyllableModel extends SequenceModel {

    private static final Logger log = LoggerFactory.getLogger(SyllableModel.class);

    public SyllableModel(Model m) {
        super(m);
    }

    @Override
    public String getPatternType() {
        return "Syllable";
    }

    @Override
    protected void initTemplateBindingNeurons() {
        primaryBN = createPrimaryBindingNeuron();

        expandContinueBindingNeurons(
                2,
                primaryBN,
                4,
                1
        );
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);
    }
}