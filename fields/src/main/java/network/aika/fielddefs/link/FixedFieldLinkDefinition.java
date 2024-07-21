package network.aika.fielddefs.link;

import network.aika.fielddefs.FieldOutputDefinition;
import network.aika.fielddefs.ObjectPath;
import network.aika.fielddefs.inputs.FixedFieldInputsDefinition;

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
