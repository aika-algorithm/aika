package network.aika.fields.softmax;

import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;

import java.util.List;

import static network.aika.fields.softmax.SoftmaxInputType.INPUT_TO_NORM;
import static network.aika.fields.softmax.SoftmaxOutputType.OUTPUT_TO_NORM;


public class SoftmaxNormType extends Type<SoftmaxNormType, SoftmaxNormObj> {

    public static RelationMany<SoftmaxNormType, SoftmaxNormObj, SoftmaxInputType, SoftmaxInputObj> NORM_TO_INPUT = new RelationMany<>(SoftmaxNormObj::getInputs, 1, "NORM_TO_INPUT");
    public static RelationMany<SoftmaxNormType, SoftmaxNormObj, SoftmaxOutputType, SoftmaxOutputObj> NORM_TO_OUTPUT = new RelationMany<>(SoftmaxNormObj::getOutputs, 0, "NORM_TO_OUTPUT");

    static {
        NORM_TO_INPUT.setReversed(INPUT_TO_NORM);
        NORM_TO_OUTPUT.setReversed(OUTPUT_TO_NORM);
    }

    public SoftmaxNormType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public SoftmaxNormObj instantiate() {
        return instantiate(
                List.of(SoftmaxNormType.class),
                List.of(this)
        );
    }

    @Override
    public Relation<SoftmaxNormType, SoftmaxNormObj, ?, ?>[] getRelations() {
        return new Relation[] {NORM_TO_INPUT, NORM_TO_OUTPUT};
    }
}
