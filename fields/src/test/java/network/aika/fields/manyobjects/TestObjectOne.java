package network.aika.fields.manyobjects;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;


public class TestObjectOne extends ObjImpl {

    TestObjectMany relatedTestObject;

    public TestObjectOne(TestTypeOne type) {
        super(type);
    }

    public TestObjectMany getRelatedTestObject() {
        return relatedTestObject;
    }

    public static void linkObjects(TestObjectMany objA, TestObjectOne objB) {
        objA.relatedTestObjects.add(objB);
        objB.relatedTestObject = objA;
    }
}
