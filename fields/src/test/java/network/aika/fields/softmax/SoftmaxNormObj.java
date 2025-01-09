package network.aika.fields.softmax;

import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SoftmaxNormObj extends ObjImpl<SoftmaxNormType, SoftmaxNormObj, TypeRegistry> {

    List<SoftmaxInputObj> inputs = new ArrayList<>();
    List<SoftmaxOutputObj> outputs = new ArrayList<>();

    public SoftmaxNormObj(SoftmaxNormType type) {
        super(type);
    }


    public static void linkObjects(SoftmaxInputObj[] inputsObjs, SoftmaxNormObj normObj) {
        for (int i = 0; i < inputsObjs.length; i++) {
            SoftmaxInputObj inputObj = inputsObjs[i];
            inputObj.normObject = normObj;
            normObj.inputs.add(inputObj);
        }
    }

    public SoftmaxInputObj getInput(int bsId) {
        return inputs.get(bsId);
    }

    public Stream<SoftmaxInputObj> getInputs() {
        return inputs.stream();
    }

    public SoftmaxOutputObj getOutput(int bsId) {
        return outputs.get(bsId);
    }

    public Stream<SoftmaxOutputObj> getOutputs() {
        return outputs.stream();
    }
}
