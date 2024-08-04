package network.aika.fielddefs;

import network.aika.fielddefs.inputs.FixedFieldInputsDefinition;
import network.aika.fields.Field;
import network.aika.fields.FieldObject;

import java.util.function.BiFunction;

public class FunctionFieldDefinition<D extends ObjectDefinition<D, O>, O extends FieldObject<D, O>> extends FieldDefinition<D, O> {


    public FunctionFieldDefinition(Class<? extends Field> clazz, D object, String label) {
        super(clazz, new FixedFieldInputsDefinition<>(), object, label);
    }

    public FunctionFieldDefinition(Class<? extends Field> clazz, D object, String name, double tolerance) {
        super(clazz, new FixedFieldInputsDefinition<>(), object, name, tolerance);
    }

    public FunctionFieldDefinition<D, O> in(Integer arg, BiFunction<D, ObjectPath, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        ((FixedFieldInputsDefinition<D, O>)getInputs()).in(arg, pathProvider, propagateUpdates);

        return this;
    }

    public FunctionFieldDefinition<D, O> in(Integer arg, BiFunction<D, ObjectPath, FieldOutputDefinition> pathProvider) {
        return in(arg, pathProvider, true);
    }

    public FunctionFieldDefinition<D, O> in(Integer arg, FieldOutputDefinition localField) {
        return in(arg, (o, p) -> localField, true);
    }
}
