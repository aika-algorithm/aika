package network.aika.fields.direction;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.type.FlattenedType;
import network.aika.type.Obj;
import network.aika.type.Type;
import network.aika.type.relations.Relation;

import java.util.stream.Stream;


public interface Direction {

    Direction INPUT = new Input();
    Direction OUTPUT = new Output();

    int getDirectionId();

    Direction invert();

    Stream<? extends FieldLinkDefinition> getFieldLinkDefinitions(FieldDefinition fd);

    FlattenedType getFlattenedType(Type type);

   void transmit(Field originField, FieldLinkDefinition fl, Obj relatedObject);
}
