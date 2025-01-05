package network.aika.fields.softmax;

import network.aika.fields.SoftmaxFields;
import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static network.aika.fields.SoftmaxFields.softmax;

public class SoftmaxTest {

    protected SoftmaxInputType inputType;
    protected SoftmaxNormType normType;
    protected SoftmaxOutputType outputType;

    double[] inputValues = new double[] {2.3, 4.1, 8.4, 1.2, 6.9};

    @BeforeEach
    public void init() {
        TypeRegistry registry = new TypeRegistryImpl();

        inputType = new SoftmaxInputType(registry, "Input")
                .setClazz(SoftmaxInputObj.class);

        normType = new SoftmaxNormType(registry, "Norm")
                .setClazz(SoftmaxNormObj.class);

        outputType = new SoftmaxOutputType(registry, "Output")
                .setClazz(SoftmaxOutputObj.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    public void testSoftmax(int setInputValuesPos) {
        SoftmaxFields<SoftmaxInputType, SoftmaxInputObj,
                SoftmaxNormType, SoftmaxNormObj,
                SoftmaxOutputType, SoftmaxOutputObj> softmaxFields =
                softmax(
                        inputType,
                        normType,
                        outputType,
                        NORM_TO_INPUT,
                        OUTPUT_TO_NORM,
                        CORRESPONDING_INPUT_LINK,
                        "test softmax"
                );

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

        SoftmaxNormObj.linkObjectsAndInitFields(inputsObjs, normObj);

        if(setInputValuesPos == 2)
            setInputValues(inputsObjs, softmaxFields);

        SoftmaxOutputObj.linkObjectsAndInitFields(normObj, outputsObjs);

        if(setInputValuesPos == 3)
            setInputValues(inputsObjs, softmaxFields);

        double[] outputValues = new double[inputValues.length];
        for(int i = 0; i < outputValues.length; i++)
            outputValues[i] = outputsObjs[i].getField(softmaxFields.getOutputs()).getValue();

        Assertions.assertArrayEquals(
                new double[] {0.10043668122270742, 0.17903930131004367, 0.36681222707423583, 0.052401746724890834, 0.3013100436681223},
                outputValues,
                0.0001
        );
    }

    private void setInputValues(SoftmaxInputObj[] inputsObjs, SoftmaxFields<SoftmaxInputType, SoftmaxInputObj, SoftmaxNormType, SoftmaxNormObj, SoftmaxOutputType, SoftmaxOutputObj> softmaxField) {
        for (int i = 0; i < inputValues.length; i++)
            inputsObjs[i].setFieldValue(softmaxField.getInputs(), inputValues[i]);
    }
}
