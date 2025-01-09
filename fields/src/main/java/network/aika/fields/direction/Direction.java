package network.aika.fields.direction;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.type.FlattenedType;
import network.aika.type.FlattenedTypeRelation;
import network.aika.type.Obj;
import network.aika.type.Type;

import java.util.stream.Stream;


public interface Direction {

    Direction INPUT = new Input();
    Direction OUTPUT = new Output();

    int getDirectionId();

    Direction invert();

    <
            T extends Type<T, O>,
            O extends Obj<T, O>
            >
    Stream<? extends FieldLinkDefinition<T, O, ?, ?>> getFieldLinkDefinitions(FieldDefinition<T, O> fd);

    <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
    >
    FlattenedTypeRelation<T, O, RT, RO>[][] getFlattenedTypeRelations(FlattenedType<T, O> flattenedType);

    <
            T extends Type<T, O>,
            O extends Obj<T, O>,
            RT extends Type<RT, RO>,
            RO extends Obj<RT, RO>
    >
    void transmit(Field<T, O> originField, FieldLinkDefinition<T, O, RT, RO> fl, RO relatedObject);
}
