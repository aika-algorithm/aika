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
package network.aika.fields.softmax;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class SoftmaxNormObj extends ObjImpl<SoftmaxNormType, SoftmaxNormObj, TypeRegistry> {

    List<SoftmaxInputObj> inputs = new ArrayList<>();
    List<SoftmaxOutputObj> outputs = new ArrayList<>();

    public SoftmaxNormObj(SoftmaxNormType type) {
        super(type);
    }


    public static void linkObjects(SoftmaxInputObj[] inputsObjs, SoftmaxNormObj normObj) {
        for (int i = 0; i < inputsObjs.length; i++) {
            SoftmaxInputObj inputObj = inputsObjs[i];
            inputObj.normObject = normObj;
            normObj.inputs.add(inputObj);
        }
    }

    public SoftmaxInputObj getInput(int bsId) {
        return inputs.get(bsId);
    }

    public Stream<SoftmaxInputObj> getInputs() {
        return inputs.stream();
    }

    public SoftmaxOutputObj getOutput(int bsId) {
        return outputs.get(bsId);
    }

    public Stream<SoftmaxOutputObj> getOutputs() {
        return outputs.stream();
    }
}
