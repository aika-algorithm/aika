package network.aika.fields.softmax;

import network.aika.fields.SoftmaxFields;
import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.manyobjects.TestObjectMany;
import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import network.aika.type.relations.RelationTypeMany;
import network.aika.type.relations.RelationTypeOne;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.fields.SoftmaxFields.softmax;
import static network.aika.fields.manyobjects.TestObjectMany.linkObjectsAndInitFields;

public class SoftmaxTest {

    public static RelationTypeOne<SoftmaxInputType, SoftmaxInputObj, SoftmaxNormType, SoftmaxNormObj> INPUT_TO_NORM = new RelationTypeOne<>(SoftmaxInputObj::getNormObject, "INPUT_TO_NORM");
    public static RelationTypeMany<SoftmaxNormType, SoftmaxNormObj, SoftmaxInputType, SoftmaxInputObj> NORM_TO_INPUT = new RelationTypeMany<>((o, td) -> o.getInputs(), "NORM_TO_INPUT");

    public static RelationTypeMany<SoftmaxNormType, SoftmaxNormObj, SoftmaxOutputType, SoftmaxOutputObj> NORM_TO_OUTPUT = new RelationTypeMany<>((o, td) -> o.getOutputs(), "NORM_TO_OUTPUT");
    public static RelationTypeOne<SoftmaxOutputType, SoftmaxOutputObj, SoftmaxNormType, SoftmaxNormObj> OUTPUT_TO_NORM = new RelationTypeOne<>(SoftmaxOutputObj::getNormObj, "OUTPUT_TO_NORM");

    public static RelationTypeOne<SoftmaxOutputType, SoftmaxOutputObj, SoftmaxInputType, SoftmaxInputObj> CORRESPONDING_INPUT_LINK = new RelationTypeOne<>(SoftmaxOutputObj::getCorrespondingInputLink, "CORRESPONDING_INPUT_LINK");
    public static RelationTypeOne<SoftmaxInputType, SoftmaxInputObj, SoftmaxOutputType, SoftmaxOutputObj> CORRESPONDING_OUTPUT_LINK = new RelationTypeOne<>(SoftmaxInputObj::getCorrespondingOutputLink, "CORRESPONDING_OUTPUT_LINK");

    static {
        INPUT_TO_NORM.setReversed(NORM_TO_INPUT);
        NORM_TO_INPUT.setReversed(INPUT_TO_NORM);

        NORM_TO_OUTPUT.setReversed(OUTPUT_TO_NORM);
        OUTPUT_TO_NORM.setReversed(NORM_TO_OUTPUT);

        CORRESPONDING_INPUT_LINK.setReversed(CORRESPONDING_OUTPUT_LINK);
        CORRESPONDING_OUTPUT_LINK.setReversed(CORRESPONDING_INPUT_LINK);
    }

    protected SoftmaxInputType inputType;
    protected SoftmaxNormType normType;
    protected SoftmaxOutputType outputType;


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

    @Test
    public void testSoftmax() {
        SoftmaxFields<SoftmaxInputType, SoftmaxInputObj,
                SoftmaxNormType, SoftmaxNormObj,
                SoftmaxOutputType, SoftmaxOutputObj> softmaxField =
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

        SoftmaxInputObj[] inputsObjs = new SoftmaxInputObj[3];
        for(int i = 0; i < inputsObjs.length; i++)
            inputsObjs[i] = inputType.instantiate();

        SoftmaxNormObj normObj = normType.instantiate();

        SoftmaxOutputObj[] outputsObjs = new SoftmaxOutputObj[3];
        for(int i = 0; i < outputsObjs.length; i++)
            outputsObjs[i] = outputType.instantiate();
        /*
        objA.setFieldValue(softmaxField.getInputs(), 5.0);

        TestObjectMany objB = new TestObjectMany(typeB);

        linkObjectsAndInitFields(objA, objB);

        Assertions.assertEquals(
                10.0,
                objB.getField(fieldC).getValue()
        );

         */
    }
}
