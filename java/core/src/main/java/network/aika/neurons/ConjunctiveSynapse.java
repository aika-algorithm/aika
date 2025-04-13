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
package network.aika.neurons;

import network.aika.type.TypeRegistry;
import network.aika.typedefs.SynapseDefinition;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Lukas Molzberger
 */
public class ConjunctiveSynapse extends Synapse {

    public ConjunctiveSynapse(SynapseDefinition type) {
        super(type);
    }

    public ConjunctiveSynapse(SynapseDefinition type, Neuron input, Neuron output) {
        super(type, input, output);
    }


    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeBoolean(propagable);
    }

    @Override
    public void readFields(DataInput in, TypeRegistry tr) throws IOException {
        super.readFields(in, tr);

        propagable = in.readBoolean();
    }
}
