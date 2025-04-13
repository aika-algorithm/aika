package network.aika.type;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.direction.Direction;
import network.aika.fields.field.Field;
import network.aika.fields.defs.FieldLinkDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlattenedTypeRelation {

    FieldLinkDefinition[][] fieldLinks;

    public FlattenedTypeRelation(FlattenedType flattenedType, List<FieldLinkDefinition> fls) {
        Map<Integer, List<FieldLinkDefinition>> groupedByOriginFD =
                fls.stream()
                .collect(Collectors.groupingBy(fl ->
                        fl.getOriginFD().getId())
                );

        fieldLinks = new FieldLinkDefinition[flattenedType.getFieldsReverse().length][];
        for(short i = 0; i < fieldLinks.length; i++) {
            for(FieldDefinition fd : flattenedType.getFieldsReverse()[i]) {
                List<FieldLinkDefinition> list = groupedByOriginFD.get(fd.getId());
                if (list != null) {
                    fieldLinks[i] = list.toArray(new FieldLinkDefinition[0]);
                }
            }
        }
    }

    public void followLinks(Direction direction, Obj relatedObj, Field field) {
        FieldLinkDefinition[] fls = fieldLinks[field.getId()];
        if(fls != null) {
            for (FieldLinkDefinition fl : fls) {
                direction.transmit(
                        field,
                        fl,
                        relatedObj
                );
            }
        }
    }
}
