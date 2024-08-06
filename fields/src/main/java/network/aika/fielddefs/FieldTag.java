package network.aika.fielddefs;

import java.util.Comparator;

public interface FieldTag {

    Comparator<FieldTag> FIELD_TAG_COMPARATOR = Comparator.comparing(FieldTag::getId);


    Integer getId();
}
