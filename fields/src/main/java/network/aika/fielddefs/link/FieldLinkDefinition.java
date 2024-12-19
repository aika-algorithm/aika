package network.aika.fielddefs.link;

import network.aika.fielddefs.FieldTag;
import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;
import network.aika.fields.link.FieldLink;


public abstract class FieldLinkDefinition {

    private FieldLinkTypeDefinition typeDefinition;
    private FieldTag targetFieldDocu;

    public FieldLinkDefinition(FieldLinkTypeDefinition typeDefinition, FieldTag targetFieldDocu) {
        this.typeDefinition = typeDefinition;
        this.targetFieldDocu = targetFieldDocu;
    }

    public FieldLinkTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    public FieldLink instantiate(FieldOutput input, FieldInput output) {
        return typeDefinition.instantiate(input, output);
    }

    @Override
    public String toString() {
        return "" + typeDefinition + " -> " + targetFieldDocu;
    }
}
