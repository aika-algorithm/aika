package network.aika.fields.manyobjects;

import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;

import java.util.List;

import static network.aika.fields.manyobjects.TestTypeOne.TEST_RELATION_TO;


public class TestTypeMany extends Type<TestTypeMany, TestObjectMany> {

    public static RelationMany<TestTypeMany, TestObjectMany, TestTypeOne, TestObjectOne> TEST_RELATION_FROM = new RelationMany<>(TestObjectMany::getRelatedTestObjects, 0, "TEST_FROM");

    static {
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO);
    }

    public TestTypeMany(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation<TestTypeMany, TestObjectMany, ?, ?>[] getRelationTypes() {
        return new Relation[] {TEST_RELATION_FROM};
    }

    public TestObjectMany instantiate() {
        return instantiate(
                List.of(TestTypeMany.class),
                List.of(this)
        );
    }
}
