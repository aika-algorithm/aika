package network.aika.fields.direction;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.defs.FieldLinkDefinitionInputSide;
import network.aika.fields.defs.FieldLinkDefinitionOutputSide;
import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.type.FlattenedType;
import network.aika.type.FlattenedTypeRelation;
import network.aika.type.Obj;
import network.aika.type.Type;

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
    public <
            T extends Type<T, O>,
            O extends Obj<T, O>
            >
    Stream<FieldLinkDefinitionInputSide<T, O, ?, ?>> getFieldLinkDefinitions(FieldDefinition<T, O> fd) {
        return fd.getOutputs();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
            >
    FlattenedTypeRelation<T, O, RT, RO>[][] getFlattenedTypeRelations(FlattenedType<T, O> flattenedType) {
        return (FlattenedTypeRelation<T, O, RT, RO>[][]) flattenedType.getOutputs();
    }

    @Override
    public <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
    >
    void transmit(Field<T, O> originField, FieldLinkDefinition<T, O, RT, RO> fl, RO relatedObject) {
        FieldLinkDefinitionOutputSide<RT, RO, T, O> flo = ((FieldLinkDefinitionInputSide<T, O, RT, RO>)fl).getOutputSide();
        fl.getRelatedFD().transmit(relatedObject.getOrCreateField(fl.getRelatedFD()), flo, originField.getUpdate());
    }
}
