package network.aika.fielddefs;

import network.aika.fields.Field;

public class MultiFieldDefinition<O extends ObjectDefinition<O>> {

    protected Class<? extends Field> clazz;

    private FieldDefinition<O>[] fieldDefinitions;
}
