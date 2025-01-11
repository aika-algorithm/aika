package network.aika.fields.softmax;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;

import java.util.List;

import static network.aika.fields.softmax.SoftmaxInputType.CORRESPONDING_OUTPUT_LINK;
import static network.aika.fields.softmax.SoftmaxNormType.NORM_TO_OUTPUT;


public class SoftmaxOutputType extends Type<SoftmaxOutputType, SoftmaxOutputObj> {

    public static RelationOne<SoftmaxOutputType, SoftmaxOutputObj, SoftmaxNormType, SoftmaxNormObj> OUTPUT_TO_NORM = new RelationOne<>(SoftmaxOutputObj::getNormObj, 1, "OUTPUT_TO_NORM");
    public static RelationOne<SoftmaxOutputType, SoftmaxOutputObj, SoftmaxInputType, SoftmaxInputObj> CORRESPONDING_INPUT_LINK = new RelationOne<>(SoftmaxOutputObj::getCorrespondingInputLink, 0, "CORRESPONDING_INPUT_LINK");

    static {
        OUTPUT_TO_NORM.setReversed(NORM_TO_OUTPUT);
        CORRESPONDING_INPUT_LINK.setReversed(CORRESPONDING_OUTPUT_LINK);
    }

    public SoftmaxOutputType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public SoftmaxOutputObj instantiate(int bsId) {
        return instantiate(
                List.of(SoftmaxOutputType.class, Integer.class),
                List.of(this, bsId)
        );
    }

    @Override
    public Relation<SoftmaxOutputType, SoftmaxOutputObj, ?, ?>[] getRelations() {
        return new Relation[] {OUTPUT_TO_NORM, CORRESPONDING_INPUT_LINK};
    }
}
