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
package network.aika.elements.neurons;

import network.aika.Model;
import network.aika.Thought;
import network.aika.elements.activations.TokenActivation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Lukas Molzberger
 */
public class TokenNeuron extends PatternNeuron {

    private String tokenLabel;


    @Override
    public TokenActivation createActivation(Thought t) {
        return new TokenActivation(t.createActivationId(), t, this);
    }

    public void setTokenLabel(String tokenLabel) {
        this.tokenLabel = tokenLabel;
    }

    public String getTokenLabel() {
        return tokenLabel;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeBoolean(tokenLabel != null);
        if(tokenLabel != null)
            out.writeUTF(tokenLabel);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        if(in.readBoolean())
            tokenLabel = in.readUTF();
    }
}
