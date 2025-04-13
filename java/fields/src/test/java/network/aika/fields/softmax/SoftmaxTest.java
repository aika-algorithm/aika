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

import network.aika.fields.SoftmaxFields;
import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static network.aika.fields.SoftmaxFields.softmax;
import static network.aika.fields.softmax.SoftmaxNormType.NORM_TO_INPUT;
import static network.aika.fields.softmax.SoftmaxOutputType.CORRESPONDING_INPUT_LINK;
import static network.aika.fields.softmax.SoftmaxOutputType.OUTPUT_TO_NORM;

/**
 *
 * @author Lukas Molzberger
 */
public class SoftmaxTest {

    protected TypeRegistry registry;
    protected SoftmaxInputType inputType;
    protected SoftmaxNormType normType;
    protected SoftmaxOutputType outputType;

    double[] inputValues = new double[] {2.3, 4.1, 8.4, 1.2, 6.9};

    @BeforeEach
    public void init() {
        registry = new TypeRegistryImpl();

        inputType = new SoftmaxInputType(registry, "Input");
        normType = new SoftmaxNormType(registry, "Norm");
        outputType = new SoftmaxOutputType(registry, "Output");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    public void testSoftmax(int setInputValuesPos) {
        SoftmaxFields softmaxFields =
                softmax(
                        inputType,
                        normType,
                        outputType,
                        NORM_TO_INPUT,
                        OUTPUT_TO_NORM,
                        CORRESPONDING_INPUT_LINK,
                        "test softmax"
                );
        registry.flattenTypeHierarchy();

        // Object and Field initialization
        SoftmaxInputObj[] inputsObjs = new SoftmaxInputObj[inputValues.length];
        for(int i = 0; i < inputsObjs.length; i++)
            inputsObjs[i] = inputType.instantiate(i);

        if(setInputValuesPos == 0)
            setInputValues(inputsObjs, softmaxFields);

        SoftmaxNormObj normObj = normType.instantiate();

        SoftmaxOutputObj[] outputsObjs = new SoftmaxOutputObj[inputValues.length];
        for(int i = 0; i < outputsObjs.length; i++)
            outputsObjs[i] = outputType.instantiate(i);

        if(setInputValuesPos == 1)
            setInputValues(inputsObjs, softmaxFields);

        SoftmaxNormObj.linkObjects(inputsObjs, normObj);
        normObj.initFields();

        if(setInputValuesPos == 2)
            setInputValues(inputsObjs, softmaxFields);

        SoftmaxOutputObj.linkObjects(normObj, outputsObjs);
        Arrays.stream(outputsObjs).forEach(ObjImpl::initFields);

        if(setInputValuesPos == 3)
            setInputValues(inputsObjs, softmaxFields);

        double normValue = normObj.getFieldValue(softmaxFields.getNorm());
        Assertions.assertEquals(22.9, normValue);

        double[] outputValues = new double[inputValues.length];
        for(int i = 0; i < outputValues.length; i++)
            outputValues[i] = outputsObjs[i].getFieldOutput(softmaxFields.getOutputs()).getValue();

        Assertions.assertArrayEquals(
                new double[] {0.10043668122270742, 0.17903930131004367, 0.36681222707423583, 0.052401746724890834, 0.3013100436681223},
                outputValues,
                0.0001
        );
    }

    private void setInputValues(SoftmaxInputObj[] inputsObjs, SoftmaxFields softmaxField) {
        for (int i = 0; i < inputValues.length; i++)
            inputsObjs[i].setFieldValue(softmaxField.getInputs(), inputValues[i]);
    }
}
