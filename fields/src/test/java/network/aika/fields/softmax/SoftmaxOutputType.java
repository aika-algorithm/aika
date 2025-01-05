package network.aika.fields.softmax;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;

import java.util.List;


public class SoftmaxOutputType extends Type<SoftmaxOutputType, SoftmaxOutputObj> {

    public static RelationOne<SoftmaxOutputType, SoftmaxOutputObj, SoftmaxInputType, SoftmaxInputObj> CORRESPONDING_INPUT_LINK = new RelationOne<>(SoftmaxOutputObj::getCorrespondingInputLink, 0, "CORRESPONDING_INPUT_LINK");
    public static RelationOne<SoftmaxInputType, SoftmaxInputObj, SoftmaxOutputType, SoftmaxOutputObj> CORRESPONDING_OUTPUT_LINK = new RelationOne<>(SoftmaxInputObj::getCorrespondingOutputLink, 1, "CORRESPONDING_OUTPUT_LINK");

    static {
        CORRESPONDING_INPUT_LINK.setReversed(CORRESPONDING_OUTPUT_LINK);
        CORRESPONDING_OUTPUT_LINK.setReversed(CORRESPONDING_INPUT_LINK);
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
    public Relation<SoftmaxOutputType, SoftmaxOutputObj, ?, ?>[] getRelationTypes() {
        return new Relation[] {CORRESPONDING_INPUT_LINK, CORRESPONDING_OUTPUT_LINK};
    }
}
