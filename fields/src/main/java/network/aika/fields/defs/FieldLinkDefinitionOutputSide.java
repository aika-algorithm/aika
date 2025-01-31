package network.aika.fields.defs;

import network.aika.fields.direction.Direction;
import network.aika.fields.field.Field;
import network.aika.type.Obj;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;

public class FieldLinkDefinitionOutputSide extends FieldLinkDefinition {

    private FieldLinkDefinitionInputSide inputSide;

    public FieldLinkDefinitionOutputSide(FieldDefinition output, FieldDefinition input, Relation relation, Direction direction, Integer argument) {
        super(output, input, relation, direction, argument);
    }

    public FieldLinkDefinitionOutputSide(FieldDefinition output, FieldDefinition input, Relation relation, Direction direction) {
        super(output, input, relation, direction);
    }

    public Field getInputField(Obj obj) {
        var rt = (RelationOne) getRelation();
        Obj inputObj = rt.followOne(obj);

        return inputObj.getFieldOutput(getRelatedFD());
    }

    public double getInputValue(Obj obj) {
        Field f = getInputField(obj);
        return f != null ?
                f.getValue() :
                0.0;
    }

    public double getUpdatedInputValue(Obj obj) {
        Field f = getInputField(obj);

        return f != null ?
                f.getUpdatedValue() :
                0.0;
    }

    public FieldLinkDefinitionInputSide getInputSide() {
        return inputSide;
    }

    public void setInputSide(FieldLinkDefinitionInputSide inputSide) {
        this.inputSide = inputSide;
    }
}
