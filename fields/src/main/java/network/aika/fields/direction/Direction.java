package network.aika.fields.direction;

import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.type.FlattenedType;
import network.aika.type.FlattenedTypeRelation;
import network.aika.type.Obj;
import network.aika.type.Type;


public interface Direction {

    Direction INPUT = new Input();
    Direction OUTPUT = new Output();

    int getDirectionId();

    Direction invert();

    FlattenedTypeRelation[][] getFlattenedTypeRelations(FlattenedType flattenedType);

    <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
    >
    void transmit(Field<T, O> originField, FieldLinkDefinition<T, O, RT, RO> fl, RO relatedObject);
}
