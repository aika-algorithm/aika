package network.aika.fields.softmax;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;


public class SoftmaxInputObj extends ObjImpl<SoftmaxInputType, SoftmaxInputObj, TypeRegistry> {

    SoftmaxNormObj normObject;

    public SoftmaxInputObj(SoftmaxInputType type) {
        super(type);
    }


    public SoftmaxNormObj getNormObject() {
        return normObject;
    }

    public void setNormObject(SoftmaxNormObj normObject) {
        this.normObject = normObject;
    }
}
