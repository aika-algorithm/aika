package network.aika.type;

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

    @SuppressWarnings("unchecked")
    public FlattenedTypeRelation(TypeRegistry registry, List<? extends FieldLinkDefinition<T, O, ?, ?>> fieldLinks) {
        Map<Integer, List<FieldLinkDefinition<T, O, RT, RO>>> groupedByRelatedFD =
                ((List<? extends FieldLinkDefinition<T, O, RT, RO>>)fieldLinks).stream() // I just hate generics!!!!
                .collect(Collectors.groupingBy(fl ->
                        fl.getRelatedFD().getFieldId())
                );

        this.fieldLinks = new FieldLinkDefinition[registry.getNumberOfFields()][];
        groupedByRelatedFD.forEach((id, list) ->
                        this.fieldLinks[id] = list.toArray(new FieldLinkDefinition[0])
                );
    }

    public void followLinks(Field<T, O> field, RO relatedObj, Direction direction) {
        for (FieldLinkDefinition<T, O, RT, RO> fl : fieldLinks[field.getFieldDefinition().getFieldId()]) {
            direction.transmit(field, fl, relatedObj);
        }
    }
}
