package network.aika.fielddefs;

import network.aika.fielddefs.inputs.FieldInputsDefinition;
import network.aika.fielddefs.inputs.FixedFieldInputsDefinition;
import network.aika.fields.Field;
import network.aika.statistic.NeuronStatistic;

public class MultiFieldDefinition<O extends ObjectDefinition<O>> implements FieldInputDefinition<O> {

    protected Class<? extends Field> clazz;

    protected FieldInputsDefinition inputs;

    protected O object;

    protected String label;

    private FieldOutputDefinition[] outputFieldDefinitions;

    public MultiFieldDefinition(Class<? extends Field> clazz, FieldInputsDefinition<O, ?> inputs, O object, String label, double tolerance) {
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
    public O getObject() {
        return object;
    }
}
