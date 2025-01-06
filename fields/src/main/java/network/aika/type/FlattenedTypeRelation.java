package network.aika.type;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.link.FieldLinkDefinition;

import java.util.List;
import java.util.function.BiConsumer;

public class FlattenedTypeRelation {

    FieldLinkDefinition[][] fieldLinks;

    public FlattenedTypeRelation(List<? extends FieldLinkDefinition> fieldLinks) {

    }

    public <RO> void followLinks(RO relatedObj, FieldDefinition fd, BiConsumer<FieldLinkDefinition, RO> perFieldLink) {
        for (FieldLinkDefinition fl : fieldLinks[fd.getFieldId()]) {
            perFieldLink.accept(fl, relatedObj);
        }
    }
}
