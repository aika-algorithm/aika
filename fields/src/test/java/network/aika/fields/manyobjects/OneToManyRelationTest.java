package network.aika.fields.manyobjects;

import network.aika.fields.defs.FieldDefinition;
import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import network.aika.type.relations.RelationTypeMany;
import network.aika.type.relations.RelationTypeOne;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static network.aika.fields.InputField.inputField;
import static network.aika.fields.SumField.sum;
import static network.aika.fields.manyobjects.TestObjectMany.linkObjectsAndInitFields;


public class OneToManyRelationTest {

    public static RelationTypeMany<TestTypeMany, TestObjectMany, TestTypeOne, TestObjectOne> TEST_RELATION_FROM = new RelationTypeMany<>((o, t) -> o.getRelatedTestObjects(), TestTypeMany.class, TestTypeOne.class, "TEST_FROM");
    public static RelationTypeOne<TestTypeOne, TestObjectOne, TestTypeMany, TestObjectMany> TEST_RELATION_TO = new RelationTypeOne<>(TestObjectOne::getRelatedTestObject, TestTypeOne.class, TestTypeMany.class, "TEST_TO");

    static {
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM);
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO);
    }

    protected TestTypeOne typeA;
    protected TestTypeMany typeB;


    @BeforeEach
    public void init() {
        TypeRegistry registry = new TypeRegistryImpl();

        typeA = new TestTypeOne(registry, "A")
                .setClazz(TestObjectOne.class);

        typeB = new TestTypeMany(registry, "B")
                .setClazz(TestObjectMany.class);
    }

    @Test
    public void testInitFields() {

        FieldDefinition<TestTypeOne, TestObjectOne> fieldA = inputField(typeA, "a");
        FieldDefinition<TestTypeOne, TestObjectOne> fieldB = inputField(typeA, "b");

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
}
