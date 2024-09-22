package network.aika.fielddefs.link;

import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;
import network.aika.fields.link.FieldLink;


public abstract class FieldLinkDefinition {

    private FieldLinkTypeDefinition typeDefinition;

    public FieldLinkDefinition(FieldLinkTypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    public FieldLinkTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    public FieldLink instantiate(FieldOutput input, FieldInput output) {
        return typeDefinition.instantiate(input, output);
    }
}
