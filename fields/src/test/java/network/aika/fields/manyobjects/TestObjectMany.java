package network.aika.fields.manyobjects;

import network.aika.type.Obj;
import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TestObjectMany extends ObjImpl {

    List<Obj> relatedTestObjects = new ArrayList<>();

    public TestObjectMany(TestTypeMany type) {
        super(type);
    }

    public Stream<Obj> getRelatedTestObjects() {
        return relatedTestObjects.stream();
    }

    public static void linkObjects(TestObjectOne objA, TestObjectMany objB) {
        objA.relatedTestObject = objB;
        objB.relatedTestObjects.add(objA);
    }

    @Override
    public Stream<Obj> followManyRelation(Relation rel) {
        return relatedTestObjects.stream();
    }

    @Override
    public Obj followSingleRelation(Relation rel) {
        return null;
    }
}
