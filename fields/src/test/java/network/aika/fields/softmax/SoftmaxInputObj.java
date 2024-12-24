package network.aika.fields.softmax;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;


public class SoftmaxInputObj extends ObjImpl<SoftmaxInputType, SoftmaxInputObj, TypeRegistry> {

    SoftmaxNormObj normObject;
    Integer bsId;

    public SoftmaxInputObj(SoftmaxInputType type, Integer bsId) {
        super(type);
        this.bsId = bsId;
    }

    public SoftmaxNormObj getNormObject() {
        return normObject;
    }

    public void setNormObject(SoftmaxNormObj normObject) {
        this.normObject = normObject;
    }

    public SoftmaxOutputObj getCorrespondingOutputLink() {
        if(normObject == null)
            return null;

        return normObject.getOutput(bsId);
    }
}
