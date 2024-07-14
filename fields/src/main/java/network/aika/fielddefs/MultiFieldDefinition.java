package network.aika.fielddefs;

import network.aika.fields.Field;

public class MultiFieldDefinition<O extends ObjectDefinition<O>> {

    protected Class<? extends Field> clazz;

    protected FieldInputsDefinition inputs;

    private FieldDefinition<O>[] outputFieldDefinitions;

}
