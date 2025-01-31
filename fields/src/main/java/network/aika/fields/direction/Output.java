package network.aika.fields.direction;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.defs.FieldLinkDefinitionInputSide;
import network.aika.fields.defs.FieldLinkDefinitionOutputSide;
import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.type.FlattenedType;
import network.aika.type.Obj;
import network.aika.type.Type;
import network.aika.type.relations.Relation;

import java.util.stream.Stream;


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
    public Stream<FieldLinkDefinitionInputSide> getFieldLinkDefinitions(FieldDefinition fd) {
        return fd.getOutputs();
    }

    public FlattenedType getFlattenedType(Type type) {
        return type.getFlattenedTypeOutputSide();
    }

    @Override
    public void transmit(Field originField, FieldLinkDefinition fl, Obj relatedObject) {
        FieldLinkDefinitionOutputSide flo = ((FieldLinkDefinitionInputSide)fl).getOutputSide();
        fl.getRelatedFD().transmit(
                relatedObject.getOrCreateFieldInput(fl.getRelatedFD()),
                flo,
                originField.getUpdate()
        );
    }
}
