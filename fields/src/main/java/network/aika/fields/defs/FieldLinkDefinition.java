package network.aika.fields.defs;

import network.aika.fields.direction.Direction;
import network.aika.fields.field.Field;
import network.aika.type.Type;
import network.aika.type.relations.Relation;
import network.aika.type.Obj;
import network.aika.type.relations.RelationOne;


public abstract class FieldLinkDefinition<
        T extends Type<T, O>,
        O extends Obj<T, O>,
        RT extends Type<RT, RO>,
        RO extends Obj<RT, RO>
        > {

    private final FieldDefinition<T, O> originFD;
    private final FieldDefinition<RT, RO> relatedFD;
    private final Relation<T, O, RT, RO> relation;
    private final Direction direction;

    private final Integer argument;

    public static <
            IT extends Type<IT, IO>,
            IO extends Obj<IT, IO>,
            OT extends Type<OT, OO>,
            OO extends Obj<OT, OO>
            >
    void link(FieldDefinition<IT, IO> input, FieldDefinition<OT, OO> output, Relation<IT, IO, OT, OO> relation, Integer argument) {
        FieldLinkDefinitionOutputSide<OT, OO, IT, IO> flo = new FieldLinkDefinitionOutputSide<>(output, input, relation.getReverse(), Direction.OUTPUT, argument);
        FieldLinkDefinitionInputSide<IT, IO, OT, OO> fli = new FieldLinkDefinitionInputSide<>(input, output, relation, Direction.INPUT, argument);

        output.addInput(flo);
        input.addOutput(fli);

        flo.setInputSide(fli);
        fli.setOutputSide(flo);
    }

    public FieldLinkDefinition(
            FieldDefinition<T, O> originFD,
            FieldDefinition<RT, RO> relatedFD,
            Relation<T, O, RT, RO> relation,
            Direction direction,
            Integer argument
    ) {
        this.originFD = originFD;
        this.relatedFD = relatedFD;
        this.relation = relation;
        this.direction = direction;
        this.argument = argument;
    }

    public FieldLinkDefinition(
            FieldDefinition<T, O> originFD,
            FieldDefinition<RT, RO> relatedFD,
            Relation<T, O, RT, RO> relation,
            Direction direction
    ) {
        this(originFD, relatedFD, relation, direction, null);
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

    public int getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return originFD + " -- (" + relation + ") -> " + relatedFD;
    }
}
