package network.aika.fielddefs.link;

import network.aika.fielddefs.FieldOutputDefinition;
import network.aika.fielddefs.FixedFieldInputsDefinition;
import network.aika.fielddefs.ObjectPath;

public class FixedFieldLinkDefinition extends FieldLinkDefinition<FixedFieldLinkDefinition> {

    private Integer arg;

    public FixedFieldLinkDefinition(ObjectPath objectPath, FieldOutputDefinition in, Integer arg, FixedFieldInputsDefinition out, boolean propagateUpdates) {
        super(objectPath, in, out, propagateUpdates);

        this.arg = arg;
    }

    public Integer getArg() {
        return arg;
    }
}
