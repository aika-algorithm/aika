package network.aika.fielddefs;


import network.aika.fielddefs.inputs.FieldInputsDefinition;

public interface FieldInputDefinition<O extends ObjectDefinition<O>> {

    FieldInputsDefinition<O, ?> getInputs();

    O getObject();

}
