package network.aika.fields.manyobjects;

import network.aika.fields.defs.FieldDefinition;
import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.fields.InputField.inputField;
import static network.aika.fields.SumField.sum;
import static network.aika.fields.manyobjects.TestObjectMany.linkObjects;
import static network.aika.fields.manyobjects.TestTypeMany.TEST_RELATION_FROM;


public class OneToManyRelationTest {

    protected TypeRegistry registry;
    protected TestTypeOne typeA;
    protected TestTypeMany typeB;


    @BeforeEach
    public void init() {
        registry = new TypeRegistryImpl();

        typeA = new TestTypeOne(registry, "A");

        typeB = new TestTypeMany(registry, "B");
    }

    @Test
    public void testInitFields() {

        FieldDefinition fieldA = inputField(typeA, "a");
        FieldDefinition fieldB = inputField(typeA, "b");

        FieldDefinition fieldC = sum(typeB, "b")
                .in(TEST_RELATION_FROM, fieldA)
                .in(TEST_RELATION_FROM, fieldB);

        registry.flattenTypeHierarchy();

        // Object and Field initialization

        TestObjectOne objA = new TestObjectOne(typeA);
        objA.setFieldValue(fieldA, 5.0);
        objA.setFieldValue(fieldB, 5.0);

        TestObjectMany objB = new TestObjectMany(typeB);

        linkObjects(objA, objB);
        objB.initFields();

        Assertions.assertEquals(
                10.0,
                objB.getFieldOutput(fieldC).getValue()
        );
    }
}
