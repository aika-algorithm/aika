package network.aika.fielddefs;


import network.aika.fielddefs.inputs.FieldInputsDefinition;
import network.aika.fields.FieldObject;

public interface FieldInputDefinition<D extends ObjectDefinition<D, O>, O extends FieldObject<D, O>> {

    FieldInputsDefinition<D, O, ?> getInputs();

    D getObject();

}
