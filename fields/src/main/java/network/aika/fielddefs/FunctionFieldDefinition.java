package network.aika.fielddefs;

import network.aika.fielddefs.inputs.FixedFieldInputsDefinition;
import network.aika.fields.Field;
import network.aika.fields.Obj;

import java.util.function.BiFunction;

public class FunctionFieldDefinition<T extends Type<T, O>, O extends Obj<T, O>> extends FieldDefinition<T, O> {


    public FunctionFieldDefinition(Class<? extends Field> clazz, T object, FieldTag fieldTag) {
        super(clazz, new FixedFieldInputsDefinition<>(), object, fieldTag);
    }

    public FunctionFieldDefinition(Class<? extends Field> clazz, T object, FieldTag fieldTag, double tolerance) {
        super(clazz, new FixedFieldInputsDefinition<>(), object, fieldTag, tolerance);
    }

    public FunctionFieldDefinition<T, O> in(Integer arg, BiFunction<T, ObjectPath, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        ((FixedFieldInputsDefinition<T, O>)getInputs()).in(arg, pathProvider, propagateUpdates);

        return this;
    }

    public FunctionFieldDefinition<T, O> in(Integer arg, BiFunction<T, ObjectPath, FieldOutputDefinition> pathProvider) {
        return in(arg, pathProvider, true);
    }

    public FunctionFieldDefinition<T, O> in(Integer arg, FieldOutputDefinition localField) {
        return in(arg, (o, p) -> localField, true);
    }
}
