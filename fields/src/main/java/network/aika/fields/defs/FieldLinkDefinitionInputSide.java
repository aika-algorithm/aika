package network.aika.fields.defs;

import network.aika.fields.direction.Direction;
import network.aika.type.Obj;
import network.aika.type.Type;
import network.aika.type.relations.Relation;


public class FieldLinkDefinitionInputSide extends FieldLinkDefinition {

    private FieldLinkDefinitionOutputSide outputSide;

    public FieldLinkDefinitionInputSide(FieldDefinition input, FieldDefinition output, Relation relation, Direction direction, Integer argument) {
        super(input, output, relation, direction, argument);
    }

    public FieldLinkDefinitionInputSide(FieldDefinition input, FieldDefinition output, Relation relation, Direction direction) {
        super(input, output, relation, direction);
    }

    public FieldLinkDefinitionOutputSide getOutputSide() {
        return outputSide;
    }

    public void setOutputSide(FieldLinkDefinitionOutputSide outputSide) {
        this.outputSide = outputSide;
    }
}
