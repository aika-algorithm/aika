package network.aika.fields.defs;

import network.aika.fields.direction.Direction;
import network.aika.fields.field.Field;
import network.aika.type.Obj;
import network.aika.type.Type;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;

public class FieldLinkDefinitionOutputSide<
        OT extends Type<OT, OO>,
        OO extends Obj<OT, OO>,
        IT extends Type<IT, IO>,
        IO extends Obj<IT, IO>
        > extends FieldLinkDefinition<OT, OO, IT, IO> {

    private FieldLinkDefinitionInputSide<IT, IO, OT, OO> inputSide;

    public FieldLinkDefinitionOutputSide(FieldDefinition<OT, OO> output, FieldDefinition<IT, IO> input, Relation<OT, OO, IT, IO> relation, Direction direction, Integer argument) {
        super(output, input, relation, direction, argument);
    }

    public FieldLinkDefinitionOutputSide(FieldDefinition<OT, OO> output, FieldDefinition<IT, IO> input, Relation<OT, OO, IT, IO> relation, Direction direction) {
        super(output, input, relation, direction);
    }

    public Field<IT, IO> getInputField(OO obj) {
        var rt = (RelationOne<OT, OO, IT, IO>) getRelation();
        IO inputObj = rt.followOne(obj);

        return inputObj.getFieldOutput(getRelatedFD());
    }

    public double getInputValue(OO obj) {
        Field<IT, IO> f = getInputField(obj);
        return f != null ?
                f.getValue() :
                0.0;
    }

    public double getUpdatedInputValue(OO obj) {
        Field<IT, IO> f = getInputField(obj);

        return f != null ?
                f.getUpdatedValue() :
                0.0;
    }

    public FieldLinkDefinitionInputSide<IT, IO, OT, OO> getInputSide() {
        return inputSide;
    }

    public void setInputSide(FieldLinkDefinitionInputSide<IT, IO, OT, OO> inputSide) {
        this.inputSide = inputSide;
    }
}
