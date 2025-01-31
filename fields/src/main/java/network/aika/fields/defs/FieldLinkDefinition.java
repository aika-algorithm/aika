package network.aika.fields.defs;

import network.aika.fields.direction.Direction;
import network.aika.fields.field.Field;
import network.aika.type.Type;
import network.aika.type.relations.Relation;
import network.aika.type.Obj;
import network.aika.type.relations.RelationOne;


public abstract class FieldLinkDefinition {

    private final FieldDefinition originFD;
    private final FieldDefinition relatedFD;
    private final Relation relation;
    private final Direction direction;

    private final Integer argument;

    public static void link(FieldDefinition input, FieldDefinition output, Relation relation, Integer argument) {
        FieldLinkDefinitionOutputSide flo = new FieldLinkDefinitionOutputSide(output, input, relation.getReverse(), Direction.OUTPUT, argument);
        FieldLinkDefinitionInputSide fli = new FieldLinkDefinitionInputSide(input, output, relation, Direction.INPUT, argument);

        output.addInput(flo);
        input.addOutput(fli);

        flo.setInputSide(fli);
        fli.setOutputSide(flo);
    }

    public FieldLinkDefinition(
            FieldDefinition originFD,
            FieldDefinition relatedFD,
            Relation relation,
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
            FieldDefinition originFD,
            FieldDefinition relatedFD,
            Relation relation,
            Direction direction
    ) {
        this(originFD, relatedFD, relation, direction, null);
    }

    public FieldDefinition getOriginFD() {
        return originFD;
    }

    public FieldDefinition getRelatedFD() {
        return relatedFD;
    }

    public Relation getRelation() {
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
