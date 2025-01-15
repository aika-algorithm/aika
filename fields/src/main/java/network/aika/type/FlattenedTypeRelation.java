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

    public FlattenedTypeRelation(TypeRegistry registry, List<FieldLinkDefinition<T, O, ?, ?>> fieldLinks) {
        Map<Integer, List<FieldLinkDefinition<T, O, ?, ?>>> groupedByRelatedFD =
                fieldLinks.stream()
                .collect(Collectors.groupingBy(fl ->
                        fl.getOriginFD().getFieldId())
                );

        this.fieldLinks = new FieldLinkDefinition[registry.getNumberOfFieldDefinitions()][];
        groupedByRelatedFD.forEach((id, list) ->
                        this.fieldLinks[id] = list.toArray(new FieldLinkDefinition[0])
                );
    }

    public void followLinks(Direction direction, RO relatedObj, Field<T, O> field) {
        FieldLinkDefinition<T, O, RT, RO>[] fls = fieldLinks[field.getFieldDefinition().getFieldId()];
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
