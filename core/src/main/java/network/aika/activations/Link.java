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
package network.aika.activations;

import network.aika.Model;
import network.aika.Document;
import network.aika.Element;
import network.aika.ModelProvider;
import network.aika.typedefs.LinkDefinition;
import network.aika.neurons.Synapse;
import network.aika.type.ObjImpl;
import network.aika.queue.Queue;
import network.aika.queue.QueueProvider;
import network.aika.queue.Timestamp;

/**
 *
 * @author Lukas Molzberger
 */
public class Link extends ObjImpl<LinkDefinition, Link, Model> implements Element, ModelProvider, QueueProvider {

    protected Synapse synapse;

    protected Activation input;
    protected Activation output;

    public Link(LinkDefinition type, Synapse s, Activation input, Activation output) {
        super(type);
        this.synapse = s;
        this.input = input;
        initFields(null);

        this.output = output;
        output.initFields(this);

        input.addOutputLink(this);
        output.addInputLink(this);
    }

    @Override
    public Timestamp getFired() {
        return input != null && isCausal() ?
                input.getFired() :
                output.getFired();
    }

    @Override
    public Timestamp getCreated() {
        return input != null && isCausal() ? input.getCreated() : output.getCreated();
    }

    public Synapse getSynapse() {
        return synapse;
    }

    public void setSynapse(Synapse synapse) {
        this.synapse = synapse;
    }

    public Activation getInput() {
        return input;
    }

    public Activation getOutput() {
        return output;
    }

    public Link getCorrespondingInputLink() {
        throw new UnsupportedOperationException();
    }

    public Link getCorrespondingOutputLink() {
        throw new UnsupportedOperationException();
    }

    public boolean isCausal() {
        return input == null || isCausal(input, output);
    }

    public static boolean isCausal(Activation iAct, Activation oAct) {
        return iAct.getFired().compareTo(oAct.getFired()) < 0;
    }

    public Document getDocument() {
        return output.getDocument();
    }

    @Override
    public Queue getQueue() {
        return output.getDocument();
    }

    @Override
    public Model getModel() {
        return output.getModel();
    }

    protected String getInputKeyString() {
        return (input != null ? input.toKeyString() : "id:X n:[" + synapse.getInput() + "]");
    }

    protected String getOutputKeyString() {
        return (output != null ? output.toKeyString() : "id:X n:[" + synapse.getOutput() + "]");
    }

    @Override
    public String toString() {
        return type.getName() +
                " in:[" + getInputKeyString() + "] " +
                " ==> " +
                "out:[" + getOutputKeyString() + "]";
    }

    @Override
    public String toKeyString() {
        return getInputKeyString() +
                " ==> " +
                getOutputKeyString();
    }
}