package network.aika.fields.hierarchy;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.oneobject.TestObject;
import network.aika.fields.oneobject.TestType;
import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static network.aika.fields.Addition.add;
import static network.aika.fields.ExponentialFunction.exp;
import static network.aika.fields.InputField.inputField;
import static network.aika.fields.oneobject.TestObject.linkObjects;
import static network.aika.fields.oneobject.TestType.SELF;
import static network.aika.fields.oneobject.TestType.TEST_RELATION_FROM;


public class OutputSideOverloadingTest {

    TypeRegistry registry;
    TestType input;
    TestType outputParent;
    TestType outputChild;

    @BeforeEach
    public void init() {
        registry = new TypeRegistryImpl();

        input = new TestType(registry, "input");

        outputParent = new TestType(registry, "outputParent");

        outputChild = (TestType) new TestType(registry, "outputChild")
                .addParent(outputParent);
    }

    @Test
    public void testOverloadingGetInputField() {
        FieldDefinition inputField = inputField(input, "inputField");

        FieldDefinition outputParentField = add(outputParent, "outputParentField")
                .in(TEST_RELATION_FROM, inputField, 0)
                .in(TEST_RELATION_FROM, inputField, 1);

        FieldDefinition outputChildField = exp(outputChild, "outputChildField")
                .in(SELF, outputParentField, 0);


        registry.flattenTypeHierarchy();

        // Object and Field initialization

        TestObject inputObj = input.instantiate();
        TestObject outputObj = outputChild.instantiate();

        inputObj.setFieldValue(inputField, 2.5);

        linkObjects(inputObj, outputObj);
        outputObj.initFields();

        Assertions.assertEquals(
                148.4131591025766,
                outputObj.getFieldOutput(outputChildField).getValue(),
                0.00001
        );
    }


    @ParameterizedTest
    @CsvSource({"propagate", "init"})
    public void testOverloading(String transmitMethod) {
        FieldDefinition inputField = inputField(input, "inputField");

        FieldDefinition outputParentField = add(outputParent, "outputParentField")
                .in(TEST_RELATION_FROM, inputField, 0)
                .in(TEST_RELATION_FROM, inputField, 1);

        FieldDefinition outputChildField = exp(outputChild, "outputChildField")
                .in(SELF, outputParentField, 0);

        registry.flattenTypeHierarchy();

        // Object and Field initialization

        TestObject inputObj = input.instantiate();
        TestObject outputObj = outputChild.instantiate();

        if("propagate".equalsIgnoreCase(transmitMethod)) {
            linkObjects(inputObj, outputObj);
            outputObj.initFields();
        }

        inputObj.setFieldValue(inputField, 2.5);

        if("init".equalsIgnoreCase(transmitMethod)) {
            linkObjects(inputObj, outputObj);
            outputObj.initFields();
        }

        Assertions.assertEquals(
                148.4131591025766,
                outputObj.getFieldOutput(outputChildField).getValue(),
                0.0001
        );
    }
}
