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
package network.aika.fields;

import network.aika.fields.link.FieldLink;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukas Molzberger
 */
public class SumField extends Field {

    private List<FieldLink> inputs;

    public SumField(FieldObject reference, String label, Double tolerance) {
        super(reference, label, tolerance);
    }

    @Override
    protected synchronized void initIO() {
        super.initIO();

        inputs = new ArrayList<>();
    }

    @Override
    public synchronized int size() {
        return inputs.size();
    }

    @Override
    public synchronized void addInput(FieldLink l) {
        inputs.add(l);
    }

    @Override
    public synchronized void removeInput(FieldLink l) {
        inputs.remove(l);
    }

    @Override
    public List<FieldLink> getInputs() {
        return inputs;
    }
}
