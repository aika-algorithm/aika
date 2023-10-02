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
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Lukas Molzberger
 */
public class InitParams implements Writable {

    double targetNet;
    Double instanceTargetNet;

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(targetNet);
        out.writeBoolean(instanceTargetNet != null);
        if(instanceTargetNet != null)
            out.writeDouble(instanceTargetNet);
    }

    public static InitParams read(DataInput in, Model m) throws Exception {
        InitParams ip = new InitParams();
        ip.readFields(in, m);
        return ip;
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        targetNet = in.readDouble();
        if(in.readBoolean())
            instanceTargetNet = in.readDouble();
    }
}
