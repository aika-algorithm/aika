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
    public <
            T extends Type<T, O>,
            O extends Obj<T, O>
            >
    Stream<FieldLinkDefinitionOutputSide<T, O, ?, ?>> getFieldLinkDefinitions(FieldDefinition<T, O> fd) {
        return fd.getInputs();
    }

    public <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
            >
    FlattenedType<T, O, RT, RO> getFlattenedType(Type<T, O> type) {
        return (FlattenedType<T, O, RT, RO>) type.getFlattenedTypeInputSide();
    }

    @Override
    public <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
    >
    void transmit(Field<T, O> originField, FieldLinkDefinition<T, O, RT, RO> fl, RO relatedObject) {
        double inputFieldValue = relatedObject.getFieldValue(fl.getRelatedFD());
        fl.getOriginFD().transmit(originField, (FieldLinkDefinitionOutputSide<T, O, RT, RO>) fl, inputFieldValue);
    }
}
