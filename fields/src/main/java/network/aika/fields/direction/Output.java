package network.aika.fields.direction;

import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;
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
    public <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
    >
    void transmit(Field<T, O> originField, FieldLinkDefinition<T, O, RT, RO> fl, RO relatedObject) {
        fl.getRelatedFD().transmit(relatedObject.getOrCreateField(fl.getRelatedFD()), fl, originField.getUpdate());
    }
}
