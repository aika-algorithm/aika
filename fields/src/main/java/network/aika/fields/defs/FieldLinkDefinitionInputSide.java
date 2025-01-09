package network.aika.fields.defs;

import network.aika.fields.direction.Direction;
import network.aika.type.Obj;
import network.aika.type.Type;
import network.aika.type.relations.Relation;


public class FieldLinkDefinitionInputSide<
        IT extends Type<IT, IO>,
        IO extends Obj<IT, IO>,
        OT extends Type<OT, OO>,
        OO extends Obj<OT, OO>
        > extends FieldLinkDefinition<IT, IO, OT, OO> {

    private FieldLinkDefinitionOutputSide<OT, OO, IT, IO> outputSide;

    public FieldLinkDefinitionInputSide(FieldDefinition<IT, IO> input, FieldDefinition<OT, OO> output, Relation<IT, IO, OT, OO> relation, Direction direction, Integer argument) {
        super(input, output, relation, direction, argument);
    }

    public FieldLinkDefinitionInputSide(FieldDefinition<IT, IO> input, FieldDefinition<OT, OO> output, Relation<IT, IO, OT, OO> relation, Direction direction) {
        super(input, output, relation, direction);
    }

    public FieldLinkDefinitionOutputSide<OT, OO, IT, IO> getOutputSide() {
        return outputSide;
    }

    public void setOutputSide(FieldLinkDefinitionOutputSide<OT, OO, IT, IO> outputSide) {
        this.outputSide = outputSide;
    }
}
