package network.aika.fields.model.many;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;


public class TestObjectOne extends ObjImpl<TestTypeOne, TestObjectOne, TypeRegistry> {

    TestObjectMany relatedTestObject;

    public TestObjectOne(TestTypeOne type) {
        super(type);
    }

    public TestObjectMany getRelatedTestObject() {
        return relatedTestObject;
    }

    public static void linkObjectsAndInitFields(TestObjectMany objA, TestObjectOne objB) {
        objA.relatedTestObjects.add(objB);
        objB.relatedTestObject = objA;

        objB.initFields(objA);
    }
}
