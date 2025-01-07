package network.aika.fields.link;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.direction.Direction;
import network.aika.fields.field.Field;
import network.aika.type.Type;
import network.aika.type.relations.Relation;
import network.aika.type.Obj;


public class FieldLinkDefinition<
        T extends Type<T, O>,
        O extends Obj<T, O>,
        RT extends Type<RT, RO>,
        RO extends Obj<RT, RO>
        > {

    private final FieldDefinition<T, O> originFD;
    private final FieldDefinition<RT, RO> relatedFD;
    private final Relation<T, O, RT, RO> relation;
    private final Direction direction;

    public FieldLinkDefinition(
            FieldDefinition<T, O> originFD,
            FieldDefinition<RT, RO> relatedFD,
            Relation<T, O, RT, RO> relation,
            Direction direction
    ) {
        this.originFD = originFD;
        this.relatedFD = relatedFD;
        this.relation = relation;
        this.direction = direction;
    }

    public FieldDefinition<T, O> getOriginFD() {
        return originFD;
    }

    public FieldDefinition<RT, RO> getRelatedFD() {
        return relatedFD;
    }

    public Relation<T, O, RT, RO> getRelation() {
        return relation;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return originFD + " -- (" + relation + ") -> " + relatedFD;
    }
}
