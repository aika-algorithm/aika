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


    public static void linkObjectsAndInitFields(SoftmaxInputObj objA, SoftmaxNormObj objB) {
        objA.normObject = objB;
        objB.inputs.add(objA);

        objB.initFields(objA);
    }

    public Stream<SoftmaxInputObj> getInputs() {
        return inputs.stream();
    }

    public Stream<SoftmaxOutputObj> getOutputs() {
        return outputs.stream();
    }
}
