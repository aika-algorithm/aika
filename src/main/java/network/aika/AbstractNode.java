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
package network.aika;

import network.aika.lattice.Node;
import network.aika.neuron.Neuron;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class AbstractNode<P extends Provider<? extends AbstractNode>> implements Writable {

    public volatile int lastUsedDocumentId = 0;

    public volatile boolean modified;

    protected P provider;

    public P getProvider() {
        return provider;
    }

    public void setModified() {
        modified = true;
    }

    public abstract boolean isSuspendable();

    public void suspend() {}

    public void reactivate() {}

    public String getLabel() {
        return null;
    }

    @Override
    public void write(DataOutput out) throws IOException {
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
    }

    public static <P extends Provider> AbstractNode read(DataInput in, P p) throws IOException {
        AbstractNode n;
        if(in.readBoolean()) {
            n = p.getModel().readNeuron(in, (Neuron) p);
        } else {
            n = Node.readNode(in, p);
        }
        return n;
    }

    public abstract boolean isNeuron();
}
