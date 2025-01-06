package network.aika.fields.manyobjects;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TestObjectMany extends ObjImpl<TestTypeMany, TestObjectMany, TypeRegistry> {

    List<TestObjectOne> relatedTestObjects = new ArrayList<>();

    public TestObjectMany(TestTypeMany type) {
        super(type);
    }

    public Stream<TestObjectOne> getRelatedTestObjects() {
        return relatedTestObjects.stream();
    }

    public static void linkObjects(TestObjectOne objA, TestObjectMany objB) {
        objA.relatedTestObject = objB;
        objB.relatedTestObjects.add(objA);
    }
}
