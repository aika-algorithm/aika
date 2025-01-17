package network.aika.type;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.direction.Direction;
import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlattenedTypeRelation<
        T extends Type<T, O>,
        O extends Obj<T, O>,
        RT extends Type<RT, RO>,
        RO extends Obj<RT, RO>
        > {

    FieldLinkDefinition<T, O, RT, RO>[][] fieldLinks;

    public FlattenedTypeRelation(FlattenedType<T, O, RT, RO> flattenedType, List<FieldLinkDefinition<T, O, ?, ?>> fls) {
        Map<Integer, List<FieldLinkDefinition<T, O, ?, ?>>> groupedByOriginFD =
                fls.stream()
                .collect(Collectors.groupingBy(fl ->
                        fl.getOriginFD().getId())
                );

        fieldLinks = new FieldLinkDefinition[flattenedType.getFieldsReverse().length][];
        for(short i = 0; i < fieldLinks.length; i++) {
            for(FieldDefinition<T, O> fd : flattenedType.getFieldsReverse()[i]) {
                List<FieldLinkDefinition<T, O, ?, ?>> list = groupedByOriginFD.get(fd.getId());
                if (list != null) {
                    fieldLinks[i] = list.toArray(new FieldLinkDefinition[0]);
                }
            }
        }
    }


    public void followLinks(Direction direction, RO relatedObj, Field<T, O> field) {
        FieldLinkDefinition<T, O, RT, RO>[] fls = fieldLinks[field.getId()];
        if(fls != null) {
            for (FieldLinkDefinition<T, O, RT, RO> fl : fls) {
                direction.transmit(
                        field,
                        fl,
                        relatedObj
                );
            }
        }
    }
}
