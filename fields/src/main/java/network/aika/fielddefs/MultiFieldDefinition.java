package network.aika.fielddefs;

import network.aika.fielddefs.link.FieldInputsDefinition;
import network.aika.fields.Field;

public class MultiFieldDefinition<O extends ObjectDefinition<O>> {

    protected Class<? extends Field> clazz;

    protected FieldInputsDefinition inputs;

    private FieldDefinition<O>[] outputFieldDefinitions;

}
