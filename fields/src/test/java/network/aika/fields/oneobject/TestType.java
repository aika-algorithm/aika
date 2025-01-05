package network.aika.fields.oneobject;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;
import network.aika.type.relations.RelationSelf;

import java.util.List;


public class TestType extends Type<TestType, TestObject> {

    public static final RelationSelf<TestType, TestObject> SELF = new RelationSelf<>(0, "TEST_SELF");

    public static final RelationOne<TestType, TestObject, TestType, TestObject> TEST_RELATION_FROM = new RelationOne<>(TestObject::getRelatedTestObject, 1, "TEST_FROM");
    public static final RelationOne<TestType, TestObject, TestType, TestObject> TEST_RELATION_TO = new RelationOne<>(TestObject::getRelatedTestObject, 2, "TEST_TO");

    static {
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM);
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO);
    }

    public TestType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation<TestType, TestObject, ?, ?>[] getRelationTypes() {
        return new Relation[] {SELF, TEST_RELATION_FROM, TEST_RELATION_TO};
    }

    public TestObject instantiate() {
        return instantiate(
                List.of(TestType.class),
                List.of(this)
        );
    }
}
