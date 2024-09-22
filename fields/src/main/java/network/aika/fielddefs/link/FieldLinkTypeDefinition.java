package network.aika.fielddefs.link;

import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;
import network.aika.fields.link.FieldLink;

public abstract class FieldLinkTypeDefinition {

    boolean propagateUpdates;

    public static FixedFieldLinkDefinition argLink(Integer arg) {
        return argLink(arg, true);
    }

    public static FixedFieldLinkDefinition argLink(Integer arg, boolean propagateUpdates) {
        return new FixedFieldLinkDefinition(arg, propagateUpdates);
    }

    public static VariableFieldLinkDefinition varLink(boolean propagateUpdates) {
        return new VariableFieldLinkDefinition(propagateUpdates);
    }

    public static VariableFieldLinkDefinition varLink() {
        return new VariableFieldLinkDefinition(true);
    }

    public FieldLinkTypeDefinition(boolean propagateUpdates) {
        this.propagateUpdates = propagateUpdates;
    }

    public boolean isPropagateUpdates() {
        return propagateUpdates;
    }

    public FieldLink instantiate(FieldOutput input, FieldInput output) {
        return new FieldLink(input, output);
    }
}
