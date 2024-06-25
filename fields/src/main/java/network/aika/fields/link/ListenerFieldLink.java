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
package network.aika.fields.link;

import network.aika.fields.FieldOutput;
import network.aika.fields.UpdateListener;

/**
 * @author Lukas Molzberger
 */
public class ListenerFieldLink extends AbstractFieldLink {

    private String listenerName;

    private UpdateListener output;

    public ListenerFieldLink(FieldOutput input, String listenerName, UpdateListener output) {
        super(input, 0);
        this.listenerName = listenerName;
        this.output = output;
    }

    public void setInput(FieldOutput input) {
        this.input = input;
    }

    @Override
    protected void propagateUpdate(double u) {
        output.receiveUpdate(this, u);
    }

    @Override
    public void unlinkOutput() {
    }

    public String getListenerName() {
        return listenerName;
    }

    @Override
    public String toString() {
        return input + " --> listener: " + listenerName;
    }
}
