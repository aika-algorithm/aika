package network.aika.fielddefs;

import java.util.ArrayList;
import java.util.List;

public class FieldObjectDefinition {

    List<FieldDefinition> fieldDefs = new ArrayList<>();


    public void addFieldDefinition(FieldDefinition fieldDef) {
        fieldDef.setFieldId(fieldDefs.size());
        fieldDefs.add(fieldDef);
    }
}
