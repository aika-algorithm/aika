package network.aika.fields.direction;

import network.aika.fields.field.Field;
import network.aika.fields.link.FieldLinkDefinition;
import network.aika.type.FlattenedType;
import network.aika.type.FlattenedTypeRelation;
import network.aika.type.Obj;
import network.aika.type.Type;

public class Input implements Direction {

    @Override
    public int getDirectionId() {
        return 0;
    }

    @Override
    public Direction invert() {
        return Direction.OUTPUT;
    }

    @Override
    public FlattenedTypeRelation[][] getFlattenedTypeRelations(FlattenedType flattenedType) {
        return flattenedType.getInputs();
    }

    @Override
    public <RT extends Type<RT, RO>, RO extends Obj<RT, RO>> void transmit(Field originField, FieldLinkDefinition fl, RO relatedObject) {
        double inputFieldValue = relatedObject.getFieldValue(fl.getInput());
        fl.getOutput().receiveUpdate(originField, fl, inputFieldValue);
    }
}
