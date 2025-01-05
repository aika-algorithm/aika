package network.aika.fields.softmax;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;

import java.util.List;


public class SoftmaxInputType extends Type<SoftmaxInputType, SoftmaxInputObj> {

    public static RelationOne<SoftmaxInputType, SoftmaxInputObj, SoftmaxNormType, SoftmaxNormObj> INPUT_TO_NORM = new RelationOne<>(SoftmaxInputObj::getNormObject, 0, "INPUT_TO_NORM");
    public static RelationMany<SoftmaxNormType, SoftmaxNormObj, SoftmaxInputType, SoftmaxInputObj> NORM_TO_INPUT = new RelationMany<>(SoftmaxNormObj::getInputs, 1, "NORM_TO_INPUT");

    static {
        INPUT_TO_NORM.setReversed(NORM_TO_INPUT);
        NORM_TO_INPUT.setReversed(INPUT_TO_NORM);
    }

    public SoftmaxInputType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation<SoftmaxInputType, SoftmaxInputObj, ?, ?>[] getRelations() {
        return new Relation[] {INPUT_TO_NORM, NORM_TO_INPUT};
    }

    public SoftmaxInputObj instantiate(int bsId) {
        return instantiate(
                List.of(SoftmaxInputType.class, Integer.class),
                List.of(this, bsId)
        );
    }
}
