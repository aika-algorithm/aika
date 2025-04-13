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

import static network.aika.fields.IdentityFunction.identity;
import static network.aika.fields.InputField.inputField;
import static network.aika.fields.Multiplication.mul;
import static network.aika.fields.oneobject.TestObject.linkObjects;
import static network.aika.fields.oneobject.TestType.TEST_RELATION_FROM;


public class InputSideOverloadingTest {

    TypeRegistry registry;
    TestType inputParent;
    TestType inputChild;
    TestType output;

    @BeforeEach
    public void init() {
        registry = new TypeRegistryImpl();

        inputParent = new TestType(registry, "inputParent");
        inputChild = new TestType(registry, "inputChild");
        output = new TestType(registry, "output");
    }

    @Test
    public void testOverloadingGetInputField() {
        FieldDefinition inputFieldParent = inputField(inputParent, "inputParentField");
        FieldDefinition inputFieldChild = inputField(inputChild, "inputChildField")
                .setParent(inputFieldParent);


        FieldDefinition outputField = mul(output, "output")
                .in(TEST_RELATION_FROM, inputFieldParent, 0)
                .in(TEST_RELATION_FROM, inputFieldParent, 1);

        registry.flattenTypeHierarchy();

        // Object and Field initialization

        TestObject inputObj = inputChild.instantiate();
        TestObject outputObj = output.instantiate();

        inputObj.setFieldValue(inputFieldChild, 5.0);

        linkObjects(inputObj, outputObj);
        outputObj.initFields();

        Assertions.assertEquals(
                25.0,
                outputObj.getFieldOutput(outputField).getValue()
        );
    }


    @ParameterizedTest
    @CsvSource({"propagate", "init"})
    public void testOverloading(String transmitMethod) {
        FieldDefinition inputFieldParent = inputField(inputParent, "inputParentField");
        FieldDefinition inputFieldChild = inputField(inputChild, "inputChildField")
                .setParent(inputFieldParent);


        FieldDefinition outputField = identity(output, "output")
                .in(TEST_RELATION_FROM, inputFieldParent, 0);

        registry.flattenTypeHierarchy();

        // Object and Field initialization

        TestObject inputObj = inputChild.instantiate();
        TestObject outputObj = output.instantiate();

        if("propagate".equalsIgnoreCase(transmitMethod)) {
            linkObjects(inputObj, outputObj);
            outputObj.initFields();
        }

        inputObj.setFieldValue(inputFieldChild, 5.0);

        if("init".equalsIgnoreCase(transmitMethod)) {
            linkObjects(inputObj, outputObj);
            outputObj.initFields();
        }

        Assertions.assertEquals(
                5.0,
                outputObj.getFieldOutput(outputField).getValue()
        );
    }
}
