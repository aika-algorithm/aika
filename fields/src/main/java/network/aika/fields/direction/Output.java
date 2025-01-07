package network.aika.fields.direction;

import network.aika.fields.field.Field;
import network.aika.fields.link.FieldLinkDefinition;
import network.aika.type.FlattenedType;
import network.aika.type.FlattenedTypeRelation;
import network.aika.type.Obj;
import network.aika.type.Type;


public class Output implements Direction {

    @Override
    public int getDirectionId() {
        return 1;
    }

    @Override
    public Direction invert() {
        return Direction.INPUT;
    }

    @Override
    public FlattenedTypeRelation[][] getFlattenedTypeRelations(FlattenedType flattenedType) {
        return flattenedType.getOutputs();
    }

    @Override
    public <RT extends Type<RT, RO>, RO extends Obj<RT, RO>> void transmit(Field originField, FieldLinkDefinition fl, RO relatedObject) {
        fl.getOutput().receiveUpdate(relatedObject.getOrCreateField(fl.getOutput()), fl, originField.getUpdate());
    }
}
