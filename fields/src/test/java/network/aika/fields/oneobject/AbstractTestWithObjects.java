package network.aika.fields.oneobject;

import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import network.aika.type.relations.RelationTypeOne;


public abstract class AbstractTestWithObjects {

    public static RelationTypeOne<TestType, TestObject, TestType, TestObject> TEST_RELATION_FROM = new RelationTypeOne<>(TestObject::getRelatedTestObject, "TEST_FROM");
    public static RelationTypeOne<TestType, TestObject, TestType, TestObject> TEST_RELATION_TO = new RelationTypeOne<>(TestObject::getRelatedTestObject, "TEST_TO");

    static {
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM);
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO);
    }

    protected TestType typeA;
    protected TestType typeB;

    public void init() {
        TypeRegistry registry = new TypeRegistryImpl();

        typeA = new TestType(registry, "A")
                .setClazz(TestObject.class);

        typeB = new TestType(registry, "B")
                .setClazz(TestObject.class);
    }
}
