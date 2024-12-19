package network.aika.fields.model;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;

public class TestObject extends ObjImpl<TestType, TestObject, TypeRegistry> {

    TestObject relatedTestObject;

    public TestObject(TestType type) {
        super(type);
    }

    public TestObject getRelatedTestObject() {
        return relatedTestObject;
    }

    public static void linkObjectsAndInitFields(TestObject objA, TestObject objB) {
        objA.relatedTestObject = objB;
        objB.relatedTestObject = objA;

        objB.initFields(objA);
    }
}
