package network.aika.fielddefs;

import network.aika.fields.Field;

import java.util.function.BiFunction;

public class FunctionFieldDefinition<O extends ObjectDefinition<O>> extends FieldDefinition<O> {


    public FunctionFieldDefinition(Class<? extends Field> clazz, O object, String label) {
        super(clazz, object, label);
    }

    public FunctionFieldDefinition(Class<? extends Field> clazz, O object, String name, double tolerance) {
        super(clazz, object, name, tolerance);
    }

    public FunctionFieldDefinition<O> in(Integer arg, BiFunction<O, ObjectPath, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        ((FixedFieldInputsDefinition<O>)getInputs()).in(arg, pathProvider, propagateUpdates);

        return this;
    }

    public FunctionFieldDefinition<O> in(Integer arg, BiFunction<O, ObjectPath, FieldOutputDefinition> pathProvider) {
        return in(arg, pathProvider, true);
    }
}
