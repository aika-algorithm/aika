package network.aika.fields.softmax;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;


public class SoftmaxOutputObj extends ObjImpl<SoftmaxOutputType, SoftmaxOutputObj, TypeRegistry> {

    SoftmaxNormObj normObj;

    public SoftmaxOutputObj(SoftmaxOutputType type) {
        super(type);
    }

    public SoftmaxNormObj getNormObj() {
        return normObj;
    }

    public static void linkObjectsAndInitFields(SoftmaxNormObj objA, SoftmaxOutputObj objB) {
        objA.outputs.add(objB);
        objB.normObj = objA;

        objB.initFields(objA);
    }
}
