package network.aika.fielddefs;

import network.aika.fielddefs.inputs.FieldInputsDefinition;
import network.aika.fields.Field;
import network.aika.fields.Obj;

public class MultiFieldDefinition<T extends Type<T, O>, O extends Obj<T, O>> implements FieldInputDefinition<T, O> {

    protected Class<? extends Field> clazz;

    protected FieldInputsDefinition inputs;

    protected T object;

    protected String label;

    private FieldOutputDefinition[] outputFieldDefinitions;

    public MultiFieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<T, O, ?> inputs, T object, String label, double tolerance) {
        this.clazz = clazz;
        this.inputs = inputs;
        this.object = object;
        this.label = label;

        inputs.setObject(object);
    }

    @Override
    public FieldInputsDefinition getInputs() {
        return inputs;
    }

    public FieldOutputDefinition[] getOutputFieldDefinitions() {
        return outputFieldDefinitions;
    }

    @Override
    public T getObject() {
        return object;
    }
}
