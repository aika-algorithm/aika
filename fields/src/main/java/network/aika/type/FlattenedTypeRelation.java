package network.aika.type;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.direction.Direction;
import network.aika.fields.field.Field;
import network.aika.fields.link.FieldLinkDefinition;

import java.util.List;

public class FlattenedTypeRelation<
        T extends Type<T, O>,
        O extends Obj<T, O>,
        RT extends Type<RT, RO>,
        RO extends Obj<RT, RO>
        > {

    FieldLinkDefinition<T, O, RT, RO>[][] fieldLinks;

    public FlattenedTypeRelation(List<? extends FieldLinkDefinition<T, O, RT, RO>> fieldLinks) {

    }

    public void followLinks(Field<T, O> field, RO relatedObj, Direction direction) {
        for (FieldLinkDefinition<T, O, RT, RO> fl : fieldLinks[field.getFieldDefinition().getFieldId()]) {
            direction.transmit(field, fl, relatedObj);
        }
    }
}
