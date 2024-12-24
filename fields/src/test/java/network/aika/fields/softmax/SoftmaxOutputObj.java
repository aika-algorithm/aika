package network.aika.fields.softmax;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;


public class SoftmaxOutputObj extends ObjImpl<SoftmaxOutputType, SoftmaxOutputObj, TypeRegistry> {

    SoftmaxNormObj normObj;
    Integer bsId;

    public SoftmaxOutputObj(SoftmaxOutputType type, Integer bsId) {
        super(type);
        this.bsId = bsId;
    }

    public SoftmaxNormObj getNormObj() {
        return normObj;
    }

    public static void linkObjectsAndInitFields(SoftmaxNormObj normObj, SoftmaxOutputObj[] outputObjs) {
        for(int i = 0; i < outputObjs.length; i++) {
            SoftmaxOutputObj outputObj = outputObjs[i];
            normObj.outputs.add(outputObj);
            outputObj.normObj = normObj;

            outputObj.initFields(normObj);
        }
    }

    public SoftmaxInputObj getCorrespondingInputLink() {
        if(normObj == null)
            return null;

        return normObj.getInput(bsId);
    }
}
