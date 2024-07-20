package network.aika.fielddefs.link;

import network.aika.fielddefs.FieldOutputDefinition;
import network.aika.fielddefs.ObjectPath;

public class VariableFieldLinkDefinition extends FieldLinkDefinition<VariableFieldLinkDefinition> {

    public VariableFieldLinkDefinition(ObjectPath objectPath, FieldOutputDefinition in, FieldInputsDefinition<?, VariableFieldLinkDefinition> out, boolean propagateUpdates) {
        super(objectPath, in, out, propagateUpdates);
    }
}
