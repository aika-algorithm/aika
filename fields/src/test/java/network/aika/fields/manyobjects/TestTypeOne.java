package network.aika.fields.manyobjects;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;

import java.util.List;

import static network.aika.fields.manyobjects.TestTypeMany.TEST_RELATION_FROM;


public class TestTypeOne extends Type<TestTypeOne, TestObjectOne> {

    public static RelationOne<TestTypeOne, TestObjectOne, TestTypeMany, TestObjectMany> TEST_RELATION_TO = new RelationOne<>(TestObjectOne::getRelatedTestObject, 0, "TEST_TO");

    static {
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM);
    }

    public TestTypeOne(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation<TestTypeOne, TestObjectOne, ?, ?>[] getRelationTypes() {
        return new Relation[] {TEST_RELATION_TO};
    }

    public TestObjectOne instantiate() {
        return instantiate(
                List.of(TestTypeOne.class),
                List.of(this)
        );
    }
}
