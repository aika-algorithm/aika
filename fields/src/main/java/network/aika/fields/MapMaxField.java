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

import network.aika.fields.link.ArgumentFieldLink;
import network.aika.fields.link.FieldLink;

import java.util.*;

/**
 * @author Lukas Molzberger
 *
 */
public class MapMaxField<K, V> extends AbstractMaxField<ArgumentFieldLink<K, V>> {

    private Map<K, ArgumentFieldLink<K, V>> inputs;

    public MapMaxField(FieldObject ref, String label, Double tolerance) {
        super(ref, label, tolerance);
    }

    @Override
    protected void initIO() {
        super.initIO();

        inputs = new TreeMap<>();
    }

    @Override
    public int size() {
        return inputs.size();
    }

    @Override
    public void addInput(ArgumentFieldLink<K, V> fl) {
        inputs.put(fl.getKey(), fl);
    }

    @Override
    public void removeInput(ArgumentFieldLink<K, V> fl) {
        inputs.remove(fl.getKey());
    }

    @Override
    public Collection<ArgumentFieldLink<K, V>> getInputs() {
        return inputs.values();
    }
}