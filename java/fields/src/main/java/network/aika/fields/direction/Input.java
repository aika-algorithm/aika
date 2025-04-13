package network.aika.fields.direction;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.defs.FieldLinkDefinitionOutputSide;
import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.type.FlattenedType;
import network.aika.type.Obj;
import network.aika.type.Type;
import network.aika.type.relations.Relation;

import java.util.stream.Stream;

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
    public Stream<FieldLinkDefinitionOutputSide> getFieldLinkDefinitions(FieldDefinition fd) {
        return fd.getInputs();
    }

    public FlattenedType getFlattenedType(Type type) {
        return type.getFlattenedTypeInputSide();
    }

    @Override
    public void transmit(Field originField, FieldLinkDefinition fl, Obj relatedObject) {
        double inputFieldValue = relatedObject.getFieldValue(fl.getRelatedFD());
        fl.getOriginFD().transmit(originField, (FieldLinkDefinitionOutputSide) fl, inputFieldValue);
    }
}
