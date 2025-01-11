package network.aika.fields.softmax;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;

import java.util.List;

import static network.aika.fields.softmax.SoftmaxNormType.NORM_TO_INPUT;
import static network.aika.fields.softmax.SoftmaxOutputType.CORRESPONDING_INPUT_LINK;


public class SoftmaxInputType extends Type<SoftmaxInputType, SoftmaxInputObj> {

    public static RelationOne<SoftmaxInputType, SoftmaxInputObj, SoftmaxNormType, SoftmaxNormObj> INPUT_TO_NORM = new RelationOne<>(SoftmaxInputObj::getNormObject, 0, "INPUT_TO_NORM");
    public static RelationOne<SoftmaxInputType, SoftmaxInputObj, SoftmaxOutputType, SoftmaxOutputObj> CORRESPONDING_OUTPUT_LINK = new RelationOne<>(SoftmaxInputObj::getCorrespondingOutputLink, 1, "CORRESPONDING_OUTPUT_LINK");

    static {
        INPUT_TO_NORM.setReversed(NORM_TO_INPUT);
        CORRESPONDING_OUTPUT_LINK.setReversed(CORRESPONDING_INPUT_LINK);
    }

    public SoftmaxInputType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    @Override
    public Relation<SoftmaxInputType, SoftmaxInputObj, ?, ?>[] getRelations() {
        return new Relation[] {INPUT_TO_NORM, CORRESPONDING_OUTPUT_LINK};
    }

    public SoftmaxInputObj instantiate(int bsId) {
        return instantiate(
                List.of(SoftmaxInputType.class, Integer.class),
                List.of(this, bsId)
        );
    }
}
