package network.aika.fields.softmax;

import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;

import java.util.List;


public class SoftmaxNormType extends Type<SoftmaxNormType, SoftmaxNormObj> {

    public static RelationMany<SoftmaxNormType, SoftmaxNormObj, SoftmaxOutputType, SoftmaxOutputObj> NORM_TO_OUTPUT = new RelationMany<>(SoftmaxNormObj::getOutputs, 0, "NORM_TO_OUTPUT");
    public static RelationOne<SoftmaxOutputType, SoftmaxOutputObj, SoftmaxNormType, SoftmaxNormObj> OUTPUT_TO_NORM = new RelationOne<>(SoftmaxOutputObj::getNormObj, 1, "OUTPUT_TO_NORM");

    static {
        NORM_TO_OUTPUT.setReversed(OUTPUT_TO_NORM);
        OUTPUT_TO_NORM.setReversed(NORM_TO_OUTPUT);
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
    public Relation<SoftmaxNormType, SoftmaxNormObj, ?, ?>[] getRelationTypes() {
        return new Relation[] {NORM_TO_OUTPUT, OUTPUT_TO_NORM};
    }
}
