package network.aika.fielddefs;


import network.aika.fielddefs.inputs.FieldInputsDefinition;
import network.aika.fields.Obj;

public interface FieldInputDefinition<T extends Type<T, O>, O extends Obj<T, O>> {

    FieldInputsDefinition<T, O, ?> getInputs();

    T getObject();

}
