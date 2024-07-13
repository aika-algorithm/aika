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

import java.util.Arrays;
import java.util.List;

/**
 * @author Lukas Molzberger
 */
public class FixedInputs implements Inputs<FixedFieldLink> {

    private FixedFieldLink[] inputs;

    public FixedInputs(int numArgs) {
        this.inputs = new FixedFieldLink[numArgs];
    }

    @Override
    public int size() {
        return inputs.length;
    }

    @Override
    public void addInput(FixedFieldLink l) {
        inputs[l.getArgument()] = l;
    }

    @Override
    public void removeInput(FixedFieldLink l) {
        inputs[l.getArgument()] = null;
    }

    @Override
    public List<FixedFieldLink> getInputs() {
        return Arrays.asList(inputs);
    }

    public double getInputValueByArg(int arg) {
        return getInputLinkByArg(arg).getInputValue();
    }

    public FieldLink getInputLinkByArg(int arg) {
        return inputs[arg];
    }

}
