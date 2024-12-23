package network.aika.fields.softmax;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.manyobjects.TestObjectMany;
import network.aika.fields.manyobjects.TestObjectOne;
import network.aika.fields.manyobjects.TestTypeMany;
import network.aika.fields.manyobjects.TestTypeOne;
import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import network.aika.type.relations.RelationTypeOne;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.fields.SoftmaxField.softmax;
import static network.aika.fields.SumField.sum;
import static network.aika.fields.manyobjects.TestObjectMany.linkObjectsAndInitFields;

public class SoftmaxTest {
/*
    public static RelationTypeOne<TestTypeOne, TestObjectOne, TestTypeMany, TestObjectMany> ONE_TO_MANY = new RelationTypeOne<>(Link::getInput, "LINK-INPUT");

    public static RelationTypeOne<TestTypeOne, TestObjectOne, TestTypeMany, TestObjectMany> OUTPUT = new RelationTypeOne<>(Link::getOutput, "LINK-OUTPUT");

    public static RelationTypeOne<TestTypeOne, TestObjectOne, TestTypeOne, TestObjectOne> CORRESPONDING_INPUT_LINK = new RelationTypeOne<>(Link::getCorrespondingInputLink, "LINK-CORRESPONDING-INPUT-LINK");
    public static RelationTypeOne<TestTypeOne, TestObjectOne, TestTypeOne, TestObjectOne> CORRESPONDING_OUTPUT_LINK = new RelationTypeOne<>(Link::getCorrespondingOutputLink, "LINK-CORRESPONDING-INPUT-LINK");
*/
    static {

//        CORRESPONDING_INPUT_LINK.setReversed(CORRESPONDING_OUTPUT_LINK);
//        CORRESPONDING_OUTPUT_LINK.setReversed(CORRESPONDING_INPUT_LINK);
    }

    protected TestTypeOne typeA;
    protected TestTypeMany typeB;
    protected TestTypeOne typeC;

/*
    @BeforeEach
    public void init() {
        TypeRegistry registry = new TypeRegistryImpl();

        inputType = new TestTypeOne(registry, "Input")
                .setClazz(TestObjectOne.class);

        normType = new TestTypeMany(registry, "Norm")
                .setClazz(TestObjectMany.class);

        outputType = new TestTypeOne(registry, "Output")
                .setClazz(TestObjectOne.class);
    }

    @Test
    public void testSoftmax() {
        FieldDefinition<TestTypeOne, TestObjectOne> softmaxField = softmax(
                inputType,
                normType,
                outputType,
                TEST_INPUT_RELATION_FROM,
                TEST_OUTPUT_RELATION_FROM,
                CORRESPONDING_INPUT_LINK,
                "test softmax"
        );

        FieldDefinition<TestTypeMany, TestObjectMany> fieldC = sum(typeB, "b")
                .in(TEST_RELATION_FROM, fieldA)
                .in(TEST_RELATION_FROM, fieldB);


        // Object and Field initialization

        TestObjectOne objA = new TestObjectOne(typeA);
        objA.setFieldValue(fieldA, 5.0);
        objA.setFieldValue(fieldB, 5.0);

        TestObjectMany objB = new TestObjectMany(typeB);

        linkObjectsAndInitFields(objA, objB);

        Assertions.assertEquals(
                10.0,
                objB.getField(fieldC).getValue()
        );
    }

 */
}
